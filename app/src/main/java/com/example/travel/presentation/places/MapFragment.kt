package com.example.travel.presentation.places

import android.Manifest
import android.R
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel.MainActivity
import com.example.travel.data.local.prefs.SharedPreferences
import com.example.travel.databinding.FragmentMapBinding
import com.example.travel.domain.model.AudioModel
import com.example.travel.domain.model.CategoryModel
import com.example.travel.domain.model.DayPlaceModel
import com.example.travel.presentation.auth.AuthViewModel
import com.example.travel.presentation.auth.AuthViewModelFactory
import com.example.travel.presentation.calendar.CalendarViewModel
import com.example.travel.presentation.calendar.CalendarViewModelFactory
import com.example.travel.presentation.calendar.DayListByUserAdapter
import com.example.travel.presentation.service.AudioData
import com.example.travel.presentation.service.AudioService
import com.example.travel.presentation.utils.DialogManager
import com.example.travel.presentation.utils.addPermissionToRequestedList
import com.example.travel.presentation.utils.checkPermissionGranted
import com.example.travel.presentation.utils.showToast
import com.example.travel.presentation.weather.ForecastAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt


class MapFragment : Fragment(), TextToSpeech.OnInitListener {

    private val viewModel: PlacesViewModel by viewModels {
        PlacesViewModelFactory()
    }

    private val viewModelCalendar: CalendarViewModel by viewModels {
        CalendarViewModelFactory()
    }

    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory()
    }

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var myLat: Double? = 0.0
    private var myLon: Double? = 0.0

//    private val sharedPreferences: SharedPreferences =
//        SharedPreferences(activity?.applicationContext)

    private val sharedPreferences: SharedPreferences by lazy {
        SharedPreferences(requireContext())
    }

    private lateinit var mIntent: Intent

    var trackFilesArrayList = ArrayList<AudioData>()
    private lateinit var outputPath: String
    var filePath = ""
//    private val categoryAdapter by lazy { CategoryListAdapter(this) }

    private var isFineLocationPermissionGranted = false
    private var isCoarseLocationPermissionGranted = false
    private var isBackgroundLocationPermissionGranted = false

    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>

    private var cityId = 0
    private var category = ""
    private var search = ""

    var placeId = ""
    private var tts: TextToSpeech? = null
    var text = ""

    private var mLocOverlay: MyLocationNewOverlay? = null
    private var flag = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsOsm()
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments?.getStringArrayList("key")

        registerPermissions()
        checkLocationEnabled()
        startAudioService()

        checkPermission()
        requestPermission()

        val token = "Bearer ${sharedPreferences.getStringValue("token")}"
        viewModel.getCityList()
        Log.d("MY_TAG", "token: ${sharedPreferences.getStringValue("token")}")
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cityList.collect {
                Log.d("MY_TAG", "onViewCreated: $it")

                val itemName = it.map { item -> item.name }
                val items = it

                withContext(Dispatchers.Main) {
                    val adapter =
                        ArrayAdapter(requireContext(), R.layout.simple_spinner_item, itemName)
                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                    var pos = 0

                    val spinner = binding.spinnerCity
                    spinner.adapter = adapter
                    if (sharedPreferences.getStringValue("pos").toString().isEmpty()) {
                        spinner.setSelection(pos)
                    } else {
                        sharedPreferences.getStringValue("pos")
                            ?.let { it1 -> spinner.setSelection(it1.toInt()) }
                    }

                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long
                        ) {
                            val itemSelected = parent.getItemAtPosition(position).toString()
                            items.map { city ->
                                if (city.name == itemSelected) {
                                    cityId = city.id
                                    sharedPreferences.save("pos", position.toString())
                                    sharedPreferences.save("city", city.name)
                                    sharedPreferences.saveFloat("city_lat", city.latitude.toFloat())
                                    sharedPreferences.saveFloat(
                                        "city_lon",
                                        city.longitude.toFloat()
                                    )
                                    binding.map.controller.setZoom(17.0)
                                    binding.map.controller.animateTo(
                                        GeoPoint(
                                            city.latitude,
                                            city.longitude
                                        )
                                    )
                                }
                            }
                            binding.map.overlays.clear()
                            try {
                                viewModel.getPlaceList(cityId, search, category)

                            } catch (e: Exception) {
                                Log.e("MY_TAG", "onViewCreated: ${e.message}")
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }


                }
            }
        }

        binding.map.overlays.add(RotationGestureOverlay(binding.map))

        val rvCategoryAdapter = CategoryListAdapter(object : CategoryActionListener {
            override fun onChoosePlace(place: CategoryModel) {

            }

            override fun getCategory(name: String) {
                category = name
                binding.map.overlays.clear()
                viewModel.getPlaceList(cityId, search, category)


                Log.d("TAG", "getCategory: ")
//                binding.map.invalidate()


            }

        })
        binding.rvCategoryList.adapter = rvCategoryAdapter
        binding.rvCategoryList.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )

        viewModel.getCategoryList()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoryList.collect{
//                withContext()
                Log.d("MY_TAG", "category: $it")
                rvCategoryAdapter.submitList(it)
            }
        }
//        val rvAdapter = PlaceListAdapter(
//            object : PlaceActionListener {
//                override fun onChoosePlace(place: PlaceModel) {
//                    Log.d("MY_TAG", "args: ${args?.get(1).toString().toLong()}}")
//                    viewModelCalendar.addDayPlace(
//                        token,
//                        DayPlaceModel(
//                            placeId = place.id,
//                            dateVisiting = args?.get(0).toString(),
//                            tripId = args?.get(1).toString().toInt(),
//                        )
//                    )
//                }
//
//                override fun getPlaceId(id: String) {
//                    placeId = id
//                    binding.container.visibility = View.VISIBLE
//                    Log.d("MyLog", "placeId: $placeId")
//
//                    viewModel.getAudioListByPlace(placeId)
//                    viewModel.getPlaceById(placeId)
//
//                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
//                        viewModel.place.collect { place ->
//                            withContext(Dispatchers.Main) {
//                                binding.name.text = place.name
//                            }
//                        }
//                    }
//
//                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
//                        viewModel.audioListByPlace.collect { audioList ->
//                            withContext(Dispatchers.Main) {
////                                binding.audio.text = audioList.toString()
//                                text = audioList[0].desc
//                            }
//                        }
//                    }
//                }
//
//            }
//
//        )
//        binding.rvPlaceList.adapter = rvAdapter


//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.placeList.collect {
//                Log.d("MY_TAG", "onViewCreated: $it")
//                rvAdapter.submitList(it)
//            }
//        }

        val rvAdapter = AudioListByPlaceAdapter(
            object : AudioActionListener {
                override fun playAudio(audio: AudioModel) {
                    speakOut(audio.desc)
                }
            }
        )

        val rvAdapterPlace = PlaceListAdapter()
        binding.rvPlaceList.adapter = rvAdapterPlace

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.placeList.collect {
                Log.d("MY_TAG", "dayListByUser: $it")
                rvAdapterPlace.submitList(it)
            }
        }

//        binding.rvAudioList.adapter = rvAdapter

        binding.btnPlaceList.setOnClickListener {
            binding.apply {
                container.visibility = View.VISIBLE
                placeListContainer.visibility = View.VISIBLE
            }
        }

        binding.btnCloseList.setOnClickListener {
            binding.container.visibility = View.GONE
            binding.placeListContainer.visibility = View.GONE
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.audioListByPlace.collect {
                Log.d("MY_TAG", "onViewCreated: $it")
//                val list = listOf<AudioModel>(
//                    AudioModel("123", "ererser", "ere", "1223"),
//                    AudioModel("123", "ererss", "ere", "1223"),
//                    AudioModel("123", "erersers", "ere", "1223"),
//                    AudioModel("123", "erersersj", "ere", "1223")
//                )
//                val myHashRender: HashMap<String?, String?> = HashMap<String?, String?>()
//                val wakeUpText = "Are you up yet?"
//                val destFileName = "/sdcard/myAppCache/wakeUp.wav"
//                myHashRender[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = wakeUpText
//                tts?.synthesizeToFile(wakeUpText, myHashRender, destFileName)
                rvAdapter.submitList(it)
            }
        }

//        val mLocProvider = GpsMyLocationProvider(activity)
//        mLocOverlay = MyLocationNewOverlay(mLocProvider, binding.map)
//        mLocOverlay?.enableMyLocation()
//        mLocOverlay?.runOnFirstFix {
//            binding.map.overlays.add(mLocOverlay)
//        }
//
//        myLat = mLocOverlay?.myLocation?.latitude
//        myLon = mLocOverlay?.myLocation?.longitude
//        Log.d("MY_TAG", "mLocOverlay: ${myLat}")
////        val res = mLocOverlay.myLocation
        nearbyPlace()
//        MediaPlayer(context).tex


        tts = TextToSpeech(context, this)
//        binding.btnAudio.setOnClickListener {
//            speakOut()
//        }

        binding.map.setMultiTouchControls(true)
//        binding.map
        val cluster = RadiusMarkerClusterer(activity?.applicationContext)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.placeList.collect {
                Log.d("MY_TAG", "placeList: $it")
//                withContext(Dispatchers.Main) {
//                    binding.map.overlays.clear() // Удалит все оверлеи, включая маркеры
//                    binding.map.invalidate()
//                }
                it.map { place ->
                    val point = GeoPoint(place.latitude.toDouble(), place.longitude.toDouble())
                    val marker = Marker(binding.map)
                    marker.position = point
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)


                    marker.setOnMarkerClickListener { _, _ ->
                        binding.map.controller.setZoom(19.0)
                        binding.map.controller.animateTo(
                            GeoPoint(
                                place.latitude.toDouble(),
                                place.longitude.toDouble()
                            ), 19.0, 2
                        )
                        binding.container.visibility = View.VISIBLE
                        binding.detailContainer.visibility = View.VISIBLE

                        if (flag) {
                            activity?.stopService(mIntent)
                            flag = false
                        }

//                        clearFileContents(filePath)


                        viewModel.getAudioListByPlace(place.id)
                        viewModel.getPlaceById(place.id)

                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.place.collect { currentPlace ->
                                withContext(Dispatchers.Main) {
                                    with(binding) {
                                        name.text = currentPlace.name
                                        tvType.text = currentPlace.typePlace
                                        tvAddress.text = currentPlace.addressId

                                        if (currentPlace.description != " ") {
                                            tvDesc.visibility = View.VISIBLE
                                            tvDesc.text = currentPlace.description
                                            text = currentPlace.description
                                        } else {
                                            tvDesc.visibility = View.GONE
                                        }

                                        btnClose.setOnClickListener {
                                            container.visibility = View.GONE
                                            detailContainer.visibility = View.GONE
                                        }

                                        btnAddPlace.setOnClickListener {
                                            if (args.isNullOrEmpty()) {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Выберите в календаре день, в который хотите посетить выбранное место!",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            } else {
                                                viewModelCalendar.addDayPlace(
                                                    token,
                                                    DayPlaceModel(
                                                        placeId = place.id,
                                                        dateVisiting = args[0].toString(),
                                                        tripId = args[1].toString().toInt(),
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.audioListByPlace.collect { audioList ->
                                withContext(Dispatchers.Main) {
//                                binding.audio.text = audioList.toString()
                                    Log.d("MY_TAG", "audioList: ${audioList.isNotEmpty()}")
//                                    if (audioList.isNotEmpty())
//                                        text = audioList[0].desc
//                                    Log.d(TAG, "text: $text")
                                }
                            }
                        }

                        true
                    }

                    cluster.add(marker)
                    binding.map.overlays.add(cluster)
                }
            }
        }

    }

    fun clearFileContents(filePath: String) {
        try {
            val file = File(filePath)

            if (file.exists()) {
                // Очищаем содержимое файла, открывая 'FileOutputStream' с параметром 'false'
                val fileOutputStream = FileOutputStream(file, false)
                fileOutputStream.close()
                // Файл теперь пуст
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Обработать исключение, возможно, сообщить пользователю
        }
    }

    private fun startAudioService() {
        mIntent = Intent(requireContext(),AudioService::class.java)
        poppulateFiles() //.mp3
        mIntent.putExtra("tracklist", trackFilesArrayList)
        binding.fStart.setOnClickListener(){
            flag = true
            activity?.startService(mIntent)
        }
        binding.fStop.setOnClickListener(){
            flag = false
            activity?.stopService(mIntent)
        }
    }

    private companion object {
        //PERMISSION request constant, assign any value
        private const val STORAGE_PERMISSION_CODE = 100
        private const val TAG = "PERMISSION_TAG"
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11(R) or above
            Environment.isExternalStorageManager()
        } else {
            //Android is below 11(R)
            val write =
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read =
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11(R) or above
            try {
                Log.d(TAG, "requestPermission: try")
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", activity?.packageName, null)
                intent.data = uri
                storageActivityResultLauncher.launch(intent)
            } catch (e: Exception) {
                Log.e(TAG, "requestPermission: ", e)
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityResultLauncher.launch(intent)
            }
        } else {
            //Android is below 11(R)
            ActivityCompat.requestPermissions(
                (activity as MainActivity),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    private val storageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d(TAG, "storageActivityResultLauncher: ")
            //here we will handle the result of our intent
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //Android is 11(R) or above
                if (Environment.isExternalStorageManager()) {
                    //Manage External Storage Permission is granted
                    Log.d(
                        TAG,
                        "storageActivityResultLauncher: Manage External Storage Permission is granted"
                    )
//                    createFolder()
                } else {
                    //Manage External Storage Permission is denied....
                    Log.d(
                        TAG,
                        "storageActivityResultLauncher: Manage External Storage Permission is denied...."
                    )
//                    toast("Manage External Storage Permission is denied....")
                }
            } else {
                //Android is below 11(R)
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                //check each permission if granted or not
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (write && read) {
                    //External Storage Permission granted
                    Log.d("MY_TAG", "onRequestPermissionsResult: External Storage Permission granted")
//                    createFolder()
                } else {
                    //External Storage Permission denied...
                    Log.d("MY_TAG", "onRequestPermissionsResult: External Storage Permission denied...")
//                    toast("External Storage Permission denied...")
                }
            }
        }
    }

    private fun poppulateFiles() {
        val path = Environment.getExternalStorageDirectory()
        val file = File(path, "tts_output.wav")
        outputPath = file.absolutePath
        filePath = "${Environment.getExternalStorageDirectory()}/tts_output.wav"
        trackFilesArrayList.clear()
        trackFilesArrayList.add(AudioData(
            filePath
        ))
    }


    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("ru")

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                viewModel.place.collect { currentPlace ->
                    withContext(Dispatchers.Main) {
                        val filename = "UniqueFileName" // Уникальное имя для этой операции

                        // Ассоциируем имя файла с результатом TTS
                        val params = Bundle()
                        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "")

                        // Синтез речи с записью в файл
                        tts?.synthesizeToFile(currentPlace.description, params, File(outputPath), filename)

                        // Установка слушателя для отслеживания успешного сохранения
                        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {
                                // Called when the TTS starts speaking
                            }

                            override fun onDone(utteranceId: String?) {
                                // Called when the TTS has done speaking
                            }

                            override fun onError(utteranceId: String?) {
                                // Called on an error during TTS
                            }
                        })
                    }
                }
            }

        }
    }

    private fun speakOut(text: String) {
        val myHashRender: HashMap<String?, String?> = HashMap<String?, String?>()
        val wakeUpText = "Are you up yet?"
        val rootDirName = Environment.getExternalStorageDirectory().absolutePath + "/file.wav"
        myHashRender[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = wakeUpText
        tts?.synthesizeToFile(wakeUpText, myHashRender, rootDirName)
    }

    private fun registerPermissions() {
        pLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    isFineLocationPermissionGranted =
                        permissions[Manifest.permission.ACCESS_FINE_LOCATION]
                            ?: isFineLocationPermissionGranted
                    isCoarseLocationPermissionGranted =
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION]
                            ?: isCoarseLocationPermissionGranted
                    isBackgroundLocationPermissionGranted =
                        permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION]
                            ?: isBackgroundLocationPermissionGranted
//                    isExternalWriteStoragePermissionGranted =
//                        permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]
//                            ?: isExternalWriteStoragePermissionGranted
                } else {
                    isFineLocationPermissionGranted =
                        permissions[Manifest.permission.ACCESS_FINE_LOCATION]
                            ?: isFineLocationPermissionGranted
//                    isExternalWriteStoragePermissionGranted =
//                        permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]
//                            ?: isExternalWriteStoragePermissionGranted
                }

            }
    }

    private fun checkLocationEnabled() {
        val locManager =
            (activity as AppCompatActivity).getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        Log.d("MyLog", "checkLocationEnabled: $isEnabled")
        if (!isEnabled) {
            DialogManager.showYesNoDialog(
                activity as AppCompatActivity, "Dialog",
                "Message", myClickListener
            )
        } else {
            showToast("Loc enabled")
        }
    }

    private val myClickListener =
        DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

        }

    private fun requestPermissions() {
        isFineLocationPermissionGranted = checkPermissionGranted(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
//        isExternalWriteStoragePermissionGranted = checkPermissionGranted(
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isCoarseLocationPermissionGranted = checkPermissionGranted(
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            isBackgroundLocationPermissionGranted = checkPermissionGranted(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )

//            isExternalWriteStoragePermissionGranted = checkPermissionGranted(
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            )
        }

        val permissionRequestList = ArrayList<String>()

        addPermissionToRequestedList(
            isFineLocationPermissionGranted,
            Manifest.permission.ACCESS_FINE_LOCATION, permissionRequestList
        )
        addPermissionToRequestedList(
            isCoarseLocationPermissionGranted,
            Manifest.permission.ACCESS_COARSE_LOCATION, permissionRequestList
        )
//        addPermissionToRequestedList(
//            isExternalWriteStoragePermissionGranted,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE, permissionRequestList
//        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            addPermissionToRequestedList(
                isBackgroundLocationPermissionGranted,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION, permissionRequestList
            )
//            addPermissionToRequestedList(
//                isExternalWriteStoragePermissionGranted,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE, permissionRequestList
//            )
        }

        if (permissionRequestList.isNotEmpty()) {
            if (permissionRequestList.size == 1) {
                pLauncher.launch(arrayOf(permissionRequestList[0]))
//                myLat = mLocOverlay.myLocation.latitude
//                myLon = mLocOverlay.myLocation.longitude
//
//                nearbyPlace()
            } else if (permissionRequestList.size > 1) {
                pLauncher.launch(arrayOf(permissionRequestList[0]))
//                myLat = mLocOverlay.myLocation.latitude
//                myLon = mLocOverlay.myLocation.longitude
//
//                nearbyPlace()
//                pLauncher2.launch(arrayOf(permissionRequestList[1]))
            }
        } else {
            initOsm()
        }
        Log.d("MyLog", "perm-s: $permissionRequestList")
    }

    override fun onResume() {
        super.onResume()
        requestPermissions()
    }

    private fun settingsOsm() {
        Configuration.getInstance().load(
            activity as AppCompatActivity,
            activity?.getSharedPreferences("osm_pref", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME
    }

    private fun initOsm() = with(binding) {
        map.controller.setZoom(20.0)
        val mLocProvider = GpsMyLocationProvider(activity)
        mLocOverlay = MyLocationNewOverlay(mLocProvider, map)
        mLocOverlay?.enableMyLocation()
        mLocOverlay?.runOnFirstFix {
            val myLocationGeoPoint = mLocOverlay?.myLocation
            map.overlays.add(mLocOverlay)
            myLat = myLocationGeoPoint?.latitude
            myLon = myLocationGeoPoint?.longitude
            Log.d("LOCATION", "Широта: $myLat, Долгота: $myLon")

        }
        Log.d("MY_TAG", "mLocOverlay2: ${mLocProvider.locationSources}")
//        val res = mLocOverlay.myLocation
//        nearbyPlace()
    }

    private fun nearbyPlace() {
        // Предположим, у вас есть текущее местоположение пользователя

        val locManager =
            (activity as AppCompatActivity).getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var currentLocation: Location? = Location("")
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            currentLocation =
                locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            Log.d("TAG", "currentLocation: $currentLocation")
        }

        val distance = getDistanceFromLatLonInKm(myLat!!, myLon!!, 55.784181, 37.711021)
        Log.d("MY_TAG", "distance: ${distance} ")

        if (distance <= 20) {
            Toast.makeText(requireContext(), "Рядом!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Не рядом!", Toast.LENGTH_SHORT).show()
        }
    }

//    private fun updateOrRequestPermissions() {
////        val hasReadPermission = ContextCompat.checkSelfPermission(
////            requireContext(),
////            Manifest.permission.READ_EXTERNAL_STORAGE
////        ) == PackageManager.PERMISSION_GRANTED
////        val hasWritePermission = ContextCompat.checkSelfPermission(
////            requireContext(),
////            Manifest.permission.WRITE_EXTERNAL_STORAGE
////        ) == PackageManager.PERMISSION_GRANTED
////        val hasReadImagesPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
////            ContextCompat.checkSelfPermission(
////                requireContext(),
////                Manifest.permission.READ_MEDIA_IMAGES
////            ) == PackageManager.PERMISSION_GRANTED
////        } else {
////            TODO("VERSION.SDK_INT < TIRAMISU")
////        }
//        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
//
//        readPermissionGranted = hasReadPermission
//        writePermissionGranted = hasWritePermission || minSdk29
//
//        val permissionsToRequest = mutableListOf<String>()
//        if(!writePermissionGranted) {
//            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }
//        if(!readPermissionGranted) {
//            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
//        }
//        if(permissionsToRequest.isNotEmpty()) {
//            pLauncher.launch(permissionsToRequest.toTypedArray())
//        }
//    }

    fun deg2rad(deg: Double): Double {
        return deg * (PI / 180)
    }

    private fun getDistanceFromLatLonInKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val radius = 6.378
        val dLat = deg2rad(lat2 - lat1)
        val dLon = deg2rad(lon2 - lon1)
        val a =
            sin(dLat / 2) * sin(dLat / 2) +
                    cos(deg2rad(lat1)) * cos(deg2rad(lat2)) *
                    sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val d = radius * c
        return d
    }

    override fun onDestroyView() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroyView()
    }

}