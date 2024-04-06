package com.example.travel.presentation.places

import android.Manifest
import android.R
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.travel.data.local.prefs.SharedPreferences
import com.example.travel.databinding.FragmentMapBinding
import com.example.travel.domain.model.AudioModel
import com.example.travel.presentation.calendar.CalendarViewModel
import com.example.travel.presentation.calendar.CalendarViewModelFactory
import com.example.travel.presentation.utils.DialogManager
import com.example.travel.presentation.utils.addPermissionToRequestedList
import com.example.travel.presentation.utils.checkPermissionGranted
import com.example.travel.presentation.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Locale


class MapFragment : Fragment(), TextToSpeech.OnInitListener {

    private val viewModel: PlacesViewModel by viewModels {
        PlacesViewModelFactory()
    }

    private val viewModelCalendar: CalendarViewModel by viewModels {
        CalendarViewModelFactory()
    }

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val sharedPreferences: SharedPreferences =
        SharedPreferences(activity?.applicationContext)

    private var isFineLocationPermissionGranted = false
    private var isCoarseLocationPermissionGranted = false
    private var isBackgroundLocationPermissionGranted = false
    private var isExternalWriteStoragePermissionGranted = false

    private var readPermissionGranted = false
    private var writePermissionGranted = false
    private var readMediaImagesPermissionGranted = false
    private var readMediaVideoPermissionGranted = false
    private var readMediaAudioPermissionGranted = false

    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>

    private var cityId = 0

    var placeId = ""
    private var tts: TextToSpeech? = null
    var text = ""

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

//        registerPermissions()
//        checkLocationEnabled()
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
            writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted
            readMediaImagesPermissionGranted = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: readMediaImagesPermissionGranted
            readMediaVideoPermissionGranted = permissions[Manifest.permission.READ_MEDIA_VIDEO] ?: readMediaVideoPermissionGranted
            readMediaAudioPermissionGranted = permissions[Manifest.permission.READ_MEDIA_AUDIO] ?: readMediaAudioPermissionGranted


        }
        updateOrRequestPermissions()
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

                    val spinner = binding.spinnerCity
                    spinner.adapter = adapter

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
                                    binding.map.controller.setZoom(17.0)
                                    binding.map.controller.animateTo(GeoPoint(city.latitude, city.longitude))
                                }
                            }
                            binding.map.overlays.clear()
                            try {
                                viewModel.getPlaceList(cityId)

                            } catch (e: Exception) {
                                Log.e("MY_TAG", "onViewCreated: ${e.message}")
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }


                }
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

        binding.rvAudioList.adapter = rvAdapter

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

//        MediaPlayer(context).tex


        tts = TextToSpeech(context, this)
//        binding.btnAudio.setOnClickListener {
//            speakOut()
//        }

        binding.map.setMultiTouchControls(true)
        val cluster = RadiusMarkerClusterer(activity?.applicationContext)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.placeList.collect {
                it.map { place ->
                    val point = GeoPoint(place.latitude.toDouble(), place.longitude.toDouble())
                    val marker = Marker(binding.map)
                    marker.position = point
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)


                    marker.setOnMarkerClickListener { _, _ ->
                        binding.map.controller.setZoom(19.0)
                        binding.map.controller.animateTo(GeoPoint(place.latitude.toDouble(), place.longitude.toDouble()), 19.0, 2)
                        binding.container.visibility = View.VISIBLE

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
                                        }
                                    }
                                }
                            }
                        }

                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.audioListByPlace.collect { audioList ->
                                withContext(Dispatchers.Main) {
//                                binding.audio.text = audioList.toString()
                                    if (audioList.isNotEmpty())
                                        text = audioList[0].desc
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

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale("ru"))

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language not supported!")
            } else {
//                binding.btnAudio.isEnabled = true
            }
        }
    }

    private fun speakOut(text: String) {
        val myHashRender: HashMap<String?, String?> = HashMap<String?, String?>()
        val wakeUpText = "Are you up yet?"
        val rootDirName = Environment.getExternalStorageDirectory().absolutePath + "/file.wav"
//        val dirName = "$rootDirName/Travel/audio"
        myHashRender[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = wakeUpText
        tts?.synthesizeToFile(wakeUpText, myHashRender, rootDirName)
//        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
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
                    isExternalWriteStoragePermissionGranted =
                        permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]
                            ?: isExternalWriteStoragePermissionGranted
                } else {
                    isFineLocationPermissionGranted =
                        permissions[Manifest.permission.ACCESS_FINE_LOCATION]
                            ?: isFineLocationPermissionGranted
                    isExternalWriteStoragePermissionGranted =
                        permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]
                            ?: isExternalWriteStoragePermissionGranted
                }

            }
    }

    private fun checkLocationEnabled() {
        val locManager =
            (activity as AppCompatActivity).getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        Log.d("MyLog", "checkLocationEnabled: $isEnabled")
        if (!isEnabled) {
            DialogManager.showYesNoDialog(activity as AppCompatActivity, "Dialog",
                "Message", myClickListener)
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
        isExternalWriteStoragePermissionGranted = checkPermissionGranted(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isCoarseLocationPermissionGranted = checkPermissionGranted(
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            isBackgroundLocationPermissionGranted = checkPermissionGranted(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )

            isExternalWriteStoragePermissionGranted = checkPermissionGranted(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
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
        addPermissionToRequestedList(
            isExternalWriteStoragePermissionGranted,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, permissionRequestList
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            addPermissionToRequestedList(
                isBackgroundLocationPermissionGranted,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION, permissionRequestList
            )
            addPermissionToRequestedList(
                isExternalWriteStoragePermissionGranted,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, permissionRequestList
            )
        }

        if (permissionRequestList.isNotEmpty()) {
            if (permissionRequestList.size == 1) {
                pLauncher.launch(arrayOf(permissionRequestList[0]))
            } else if (permissionRequestList.size > 1) {
                pLauncher.launch(arrayOf(permissionRequestList[0]))
//                pLauncher2.launch(arrayOf(permissionRequestList[1]))
            }
        } else {
            initOsm()
        }
        Log.d("MyLog", "perm-s: $permissionRequestList")
    }

    override fun onResume() {
        super.onResume()
//        requestPermissions()
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
        val mLocOverlay = MyLocationNewOverlay(mLocProvider, map)
        mLocOverlay.enableMyLocation()
        mLocOverlay.runOnFirstFix {
            map.overlays.add(mLocOverlay)
        }
    }

    private fun updateOrRequestPermissions() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasReadImagesPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            TODO("VERSION.SDK_INT < TIRAMISU")
        }
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if(!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if(permissionsToRequest.isNotEmpty()) {
            pLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    override fun onDestroyView() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroyView()
    }

}