package com.example.travel.presentation.places

import android.Manifest
import android.R
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
//import com.example.travel.R
//import com.example.gpstrackerapp.R
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.view.View.OnClickListener
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travel.MainActivity
import com.example.travel.data.local.prefs.SharedPreferences
import com.example.travel.databinding.FragmentMapBinding
import com.example.travel.domain.model.AudioModel
import com.example.travel.domain.model.CategoryModel
import com.example.travel.domain.model.DayPlaceModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.UpdateScoresRequest
import com.example.travel.domain.model.UserProfileModel
import com.example.travel.domain.model.UserSubscribeAdapterModel
import com.example.travel.domain.model.review.CreateReviewModel
import com.example.travel.domain.model.review.ReviewAdapterModel
import com.example.travel.presentation.auth.AuthViewModel
import com.example.travel.presentation.auth.AuthViewModelFactory
import com.example.travel.presentation.calendar.CalendarViewModel
import com.example.travel.presentation.calendar.CalendarViewModelFactory
import com.example.travel.presentation.profile.ProfileViewModel
import com.example.travel.presentation.profile.ProfileViewModelFactory
import com.example.travel.presentation.service.AudioData
import com.example.travel.presentation.service.AudioService
import com.example.travel.presentation.utils.DialogManager
import com.example.travel.presentation.utils.addPermissionToRequestedList
import com.example.travel.presentation.utils.checkPermissionGranted
import com.example.travel.presentation.utils.showToast
import com.example.travel.presentation.utils.textChangedFlow
import com.musfickjamil.snackify.Snackify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
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
    private var isServiceRunning = false
    private var pl: Polyline? = null
    private var isSelectedId: Long = 0

    private var myLat: Double? = 0.0
    private var myLon: Double? = 0.0

    lateinit var runnable: Runnable
    private var handler = Handler()

    private var locModel: Int = 0
    private var curPos: Int = 0

    private val sharedPreferences: SharedPreferences by lazy {
        SharedPreferences(requireContext())
    }

    private var lastSelectedMarker: Marker? = null

    private lateinit var mIntent: Intent

    var trackFilesArrayList = ArrayList<AudioData>()
    private lateinit var outputPath: String
    var filePath = ""

    private lateinit var currentUser: UserProfileModel

    private var isFineLocationPermissionGranted = false
    private var isCoarseLocationPermissionGranted = false
    private var isBackgroundLocationPermissionGranted = false
    private var isReadPermissionGranted = false
    private var isWritePermissionGranted = false

    //    val rvAdapterPlace: PlaceListAdapter by lazy { PlaceListAdapter() }
    private var firstStart = false
    private var fl = false

    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>

    private var cityId = 0
    private var category = ""
    private var search = ""

    var placeStringId = ""
    private var placeIsFav = false
    private var tts: TextToSpeech? = null
    var text = ""

    private var mLocOverlay: MyLocationNewOverlay? = null
    private var flag = false

    lateinit var cluster: RadiusMarkerClusterer
    private lateinit var review: String
    private lateinit var token: String

    private val viewModelProfile: ProfileViewModel by activityViewModels {
        ProfileViewModelFactory()
    }

    private val markersOriginalIcons = hashMapOf<Marker, Drawable?>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsOsm()
        cluster = RadiusMarkerClusterer(activity?.applicationContext)
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var service: AudioService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, serviceBinder: IBinder) {
            // Мы привязаны к сервису
            val binder = serviceBinder as AudioService.LocalBinder
            service = binder.getService()
            isBound = true
            // Теперь можно вызывать методы сервиса
//            initSeekBar()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        // Привязываемся к сервису
        Intent(activity, AudioService::class.java).also { intent ->
            activity?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            // Отключаемся от сервиса
            activity?.unbindService(connection)
            isBound = false
        }
    }

    fun callPauseOnService() {
        if (isBound) {
            service?.pauseMusic()
        }
    }

    fun clear() {
        if (isBound) {
            service?.clearMusic()
        }
    }

    @OptIn(FlowPreview::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments?.getStringArrayList("key")

        registerPermissions()
//        checkLocationEnabled()
        startAudioService()

//        checkPermission()
//        if (checkPermission()) {
//            return
//        } else {
//            requestPermission()
//        }
//        requestPermission()
        registerLocReceiver()
        registerReceiver()
        checkServiceState()
        locationUpdates()
        token = "Bearer ${sharedPreferences.getStringValue("token")}"
//        viewModel.getCityList()
        viewModel.getCityData()
//        viewModel.upload()

        if ((activity as MainActivity).checkAuthTime()) {
            Log.d("MY_TAG", "auth: ")

            viewModelProfile.uploadUserProfile(token)
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                viewModelProfile.userProfile.collect { user ->
                    currentUser = user
                }
            }
        }

        Log.d("MY_TAG", "token: ${sharedPreferences.getStringValue("token")}")
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModel.cityList.collect {
                Log.d("MY_TAG", "onViewCreated: $it")

                val itemName = it.map { item -> item.name }
                val items = it

                withContext(Dispatchers.Main) {
                    val adapter =
                        ArrayAdapter(requireContext(), R.layout.simple_spinner_item, itemName)
                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                    val pos = sharedPreferences.getStringValue("pos")?.toInt() ?: 0

                    val spinner = binding.spinnerCity
                    spinner.adapter = adapter
                    spinner.setSelection(pos)

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
//                                    cityId += 1
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
//                                viewModel.getPlaceList(cityId, search, category)
                                viewModel.getPlaceListData(cityId, search, category)
                            } catch (e: Exception) {
                                Log.e("MY_TAG", "onViewCreated: ${e.message}")
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }


                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            binding.searchView.textChangedFlow()
                .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .debounce(50)
                .flowOn(Dispatchers.IO)
                .collectLatest { textSearch ->
                    search = textSearch.lowercase()
//                    viewModel.getPlaceList(cityId, search, category)
                    viewModel.getPlaceListFromApi(cityId, search, category)

//                    viewModel.getPlaceListData(cityId, search, category)
                }
        }


        binding.map.overlays.add(RotationGestureOverlay(binding.map))

        val rvCategoryAdapter = CategoryListAdapter(object : CategoryActionListener {
            override fun onChoosePlace(place: CategoryModel) {

            }

            override fun getCategory(name: String) {
                category = name
//                viewModel.getPlaceListData(cityId, search, category)
                viewModel.getPlaceListFromApi(cityId, search, category)
            }

        })
        binding.rvCategoryList.adapter = rvCategoryAdapter
        binding.rvCategoryList.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )

        viewModel.getCategoryList()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModel.categoryList.collect { list ->
                withContext(Dispatchers.Main) {
                    rvCategoryAdapter.submitList(list)
                }
            }
        }

        val rvAdapter = PlaceListAdapter(
            object : PlaceActionListener {
                override fun getPlaceId(genId: Long) {
                    viewModel.uploadPlaceFromDb(genId, cityId, search, category) //get
                }
            }
        )

        binding.rvPlaceList.adapter = rvAdapter

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModel.placeList.collect {
                Log.d("MY_TAG", "places: ${it.size}")
                withContext(Dispatchers.Main) {
                    val newList = mutableListOf<PlaceModel>()
                    it.map { item ->
                        if (item.subTypePlace != "city") {
                            newList.add(item)
                        }
                    }
                    rvAdapter.submitList(newList)
                }

            }
        }

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

        val rvReviewAdapter = ReviewAdapter()
        binding.rvReviewList.adapter = rvReviewAdapter

//        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
//            viewModel.audioListByPlace.collect {
//                Log.d("MY_TAG", "onViewCreated: $it")
////                val list = listOf<AudioModel>(
////                    AudioModel("123", "ererser", "ere", "1223"),
////                    AudioModel("123", "ererss", "ere", "1223"),
////                    AudioModel("123", "erersers", "ere", "1223"),
////                    AudioModel("123", "erersersj", "ere", "1223")
////                )
////                val myHashRender: HashMap<String?, String?> = HashMap<String?, String?>()
////                val wakeUpText = "Are you up yet?"
////                val destFileName = "/sdcard/myAppCache/wakeUp.wav"
////                myHashRender[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = wakeUpText
////                tts?.synthesizeToFile(wakeUpText, myHashRender, destFileName)
//                rvAdapter.submitList(it)
//            }
//        }

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

        tts = TextToSpeech(context, this)

        binding.map.setMultiTouchControls(true)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModel.placeList.collect {
//                withContext(Dispatchers.Main) {
                binding.map.overlays.clear()
                cluster.items.clear()
                it.map { place ->
                    if (place.subTypePlace != "city") {
                        val point =
                            GeoPoint(place.latitude.toDouble(), place.longitude.toDouble())
                        val marker = Marker(binding.map)
                        marker.position = point
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        if (place.subTypePlace == "place") {
                            marker.icon = getDrawable(
                                requireContext(),
                                com.example.travel.R.drawable.ic_tree
                            )
                        }
                        if (place.typePlace == "building") {
                            marker.icon = getDrawable(
                                requireContext(),
                                com.example.travel.R.drawable.ic_museum
                            )
                        }
                        if (place.typePlace == "attraction") {
                            marker.icon = getDrawable(
                                requireContext(),
                                com.example.travel.R.drawable.ic_monument
                            )
                        }
                        if (place.typePlace == "branch") {
                            if (place.name.lowercase()
                                    .contains("магазин") || place.name.lowercase()
                                    .contains("супермаркет") || place.name.lowercase()
                                    .contains("гипермаркет") || place.name.lowercase()
                                    .contains("дискаунтер")
                            ) {
                                marker.icon = getDrawable(
                                    requireContext(),
                                    com.example.travel.R.drawable.ic_supermarket
                                )
                            } else {
                                marker.icon = getDrawable(
                                    requireContext(),
                                    com.example.travel.R.drawable.ic_restaurant
                                )
                            }
//                                if (place.name.lowercase().contains("кафе") || place.name.lowercase().contains("ресторан") || place.name.lowercase().contains("бар")) {
//                                    marker.icon = getDrawable(
//                                        requireContext(),
//                                        com.example.travel.R.drawable.ic_restaurant
//                                    )
//                                }
                        }


                        marker.setOnMarkerClickListener { clickedMarker, mapView ->
//                            determineIconByType(lastSelectedMarker) // обновлённая функция для определения иконки
//                            clickedMarker.icon = getDrawable(
//                                requireContext(),
//                                com.example.travel.R.drawable.ic_selected_monument // Предположим, что это выбранная иконка
//                            )
//
//                            // Сохраняем текущий выбранный маркер как последний выбранный
//                            lastSelectedMarker = clickedMarker

//                            true
//                            if (place.subTypePlace == "place") {
//                                marker.icon = getDrawable(
//                                    requireContext(),
//                                    com.example.travel.R.drawable.ic_tree
//                                )
//                            }
//                            if (place.typePlace == "building") {
//                                marker.icon = getDrawable(
//                                    requireContext(),
//                                    com.example.travel.R.drawable.ic_museum
//                                )
//                            }
//                            if (place.typePlace == "attraction") {
//                                marker.icon = getDrawable(
//                                    requireContext(),
//                                    com.example.travel.R.drawable.ic_selected_monument
//                                )
//                            }
//                            if (place.typePlace == "branch") {
//                                if (place.name.lowercase()
//                                        .contains("магазин") || place.name.lowercase()
//                                        .contains("супермаркет") || place.name.lowercase()
//                                        .contains("гипермаркет") || place.name.lowercase()
//                                        .contains("дискаунтер")
//                                ) {
//                                    marker.icon = getDrawable(
//                                        requireContext(),
//                                        com.example.travel.R.drawable.ic_supermarket
//                                    )
//                                } else {
//                                    marker.icon = getDrawable(
//                                        requireContext(),
//                                        com.example.travel.R.drawable.ic_restaurant
//                                    )
//                                }
//                            }
                            placeStringId = place.id
                            service?.stopPlayer()
                            binding.linearProgressBar.progress = 0
                            viewModelProfile.getUserList()
                            if (placeStringId.isNotEmpty()) {
                                viewModel.getReviewListByPlace(placeStringId)
                            }

                            binding.btnSaveReview.setOnClickListener {

                                viewModel.createReview(
                                    token, CreateReviewModel(
                                        placeId = placeStringId,
                                        text = review,
                                        rating = binding.ratingBar.rating.toInt(),
                                    )
                                )
                                viewModel.getReviewListByPlace(placeStringId)
                                Log.d(TAG, "placeId: ${placeStringId}")
                                Snackify.success(
                                    binding.detailContainer,
                                    "Отзыв успешно добавлен!",
                                    Snackify.LENGTH_LONG
                                ).show()
                                binding.containerReview.visibility = View.GONE
                            }

                            binding.map.controller.setZoom(19.0)
                            binding.map.controller.animateTo(
                                GeoPoint(
                                    place.latitude.toDouble(),
                                    place.longitude.toDouble()
                                ), 19.0, 2
                            )
                            binding.container.visibility = View.VISIBLE
                            binding.detailContainer.visibility = View.VISIBLE

                            clearFileContents(filePath)

                            viewModel.getAudioListByPlace(place.id)
                            viewModel.getPlaceById(place.id)

//                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
//                                viewModel.reviewListByPlace.collect { reviewList ->
////                                    viewModelProfile.userList.collect { userList ->
////                                        Log.d(TAG, "review user2: $reviewList $userList")
//                                        withContext(Dispatchers.Main) {
//                                            val newList = mutableListOf<ReviewAdapterModel>()
//                                            reviewList.map { review ->
////                                                userList.map { user ->
////                                                    if (review.userId == user.id) {
//                                                        newList.add(
//                                                            ReviewAdapterModel(
//                                                                userName = review.userId.toString(),
//                                                                text = review.text,
//                                                                rating = review.rating,
//                                                                date = review.date,
//                                                                placeId = review.placeId
//                                                            )
//                                                        )
////                                                    }
////                                                }
//                                            }
//
//                                            Log.d(TAG, "rvReviewList: $newList")
//                                            rvReviewAdapter.submitList(newList)
////                                        }
//                                    }
//
//                                }
//                            }

                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                val reviewList = viewModel.reviewListByPlace.first()
                                val userList = viewModelProfile.userList.first()
//                                viewModel.getReviewListByPlace(placeStringId)
                                val newList = mutableListOf<ReviewAdapterModel>()

                                reviewList.forEach { review ->
                                    val user = userList.firstOrNull { it.id == review.userId }
                                    newList.add(
                                        ReviewAdapterModel(
                                            userName = user?.username ?: "Аноним", // Если пользователь не найден, используем "Аноним"
                                            text = review.text,
                                            rating = review.rating,
                                            date = review.date,
                                            placeId = review.placeId
                                        )
                                    )
                                }

                                withContext(Dispatchers.Main) {
                                    Log.d(TAG, "rvReviewList: $newList")
                                    rvReviewAdapter.submitList(newList)
                                }
                            }

                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                viewModel.place.collect { currentPlace ->
                                    withContext(Dispatchers.Main) {
                                        with(binding) {
                                            name.text = currentPlace.name
                                            tvType.text = "${currentPlace.latitude} ${currentPlace.longitude}"
//                                            tvType.text = currentPlace.typePlace
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

                                            binding.containerReview.visibility = View.GONE
                                            binding.btnCreateReview.setOnClickListener {
                                                binding.containerReview.visibility =
                                                    View.VISIBLE
                                            }

                                            binding.etReview.doAfterTextChanged { text ->
                                                review = text.toString()
                                            }

//                                            binding.btnSaveReview.setOnClickListener {
//
//                                                viewModel.createReview(
//                                                    token, CreateReviewModel(
//                                                        placeId = currentPlace.id,
//                                                        text = review,
//                                                        rating = binding.ratingBar.rating.toInt(),
//                                                    )
//                                                )
//                                                Log.d(TAG, "placeId: ${currentPlace.id}")
//                                                Snackify.success(
//                                                    binding.detailContainer,
//                                                    "Отзыв успешно добавлен!",
//                                                    Snackify.LENGTH_LONG
//                                                ).show()
//                                                binding.containerReview.visibility = View.GONE
//                                            }

                                            setOnClicks(
                                                currentPlace.latitude,
                                                currentPlace.longitude,
                                                currentPlace.generatedId
                                            )

                                            btnAddPlace.setOnClickListener {
                                                if (args.isNullOrEmpty()) {
                                                    Snackify.info(
                                                        binding.detailContainer,
                                                        "Выберите в календаре день, в который хотите посетить выбранное место!",
                                                        Snackify.LENGTH_LONG
                                                    ).show()
                                                } else {
                                                    viewModelCalendar.addDayPlace(
                                                        token,
                                                        DayPlaceModel(
                                                            placeId = place.id,
                                                            dateVisiting = args[0].toString(),
                                                            tripId = args[1].toString().toInt(),
                                                        )
                                                    )
                                                    Snackify.success(
                                                        binding.detailContainer,
                                                        "Место добавлено!",
                                                        Snackify.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (token.isNotEmpty()) {
                                viewModelProfile.getSubscribeListByUser(token)

                                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    viewModelProfile.subscribeListByUser.collect { subscribeListByUser ->
                                        subscribeListByUser.forEach { sub ->
                                            val format =
                                                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                            val period: Int = when (sub.typeId) {
                                                1L -> {
                                                    2
                                                }

                                                2L -> {
                                                    3
                                                }

                                                else -> {
                                                    0
                                                }
                                            }
                                            val dateString = format.format(sub.date.time)
                                            val dateFormatOriginal =
                                                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                            val date = dateFormatOriginal.parse(dateString)
                                            val calendar = Calendar.getInstance()
                                            calendar.time = date
                                            calendar.add(Calendar.DAY_OF_MONTH, period - 1)
                                            val newDateString =
                                                dateFormatOriginal.format(calendar.time)
                                            Log.d(TAG, "calendar.time: ${calendar.time}")
                                            if (sub.city == sharedPreferences.getStringValue("city") && (Date(System.currentTimeMillis()).time - (sub.date.time + period * 24 * 60 * 60 * 1000)) < 0) {
                                                withContext(Dispatchers.Main) {
                                                    binding.containerAudio.visibility = View.VISIBLE
                                                }
                                            } else {
                                                withContext(Dispatchers.Main) {
                                                    binding.containerAudio.visibility = View.GONE
                                                }
                                            }
                                        }

                                    }
                                }
                            }

                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                viewModel.audioListByPlace.collect { audioList ->
                                    withContext(Dispatchers.Main) {
                                        if (audioList.isNotEmpty()) {

                                            viewModel.addAudio(AudioModel(
                                                audioList[0].id,
                                                audioList[0].placeId,
                                                audioList[0].text,
                                                audioList[0].status)
                                            )
                                            binding.textAudio.visibility = View.VISIBLE
//                                            binding.containerAudio.setBackgroundColor(requireContext().getColor(com.example.travel.R.color.bright_foreground_light_disabled))
                                            text = audioList[0].text
                                        } else {
                                            binding.containerAudio.visibility = View.GONE
                                            binding.textAudio.visibility = View.GONE
                                        }
//                                            if (place.description.isNotEmpty())
//                                                text = audioList[0].text
//                                            }
                                        Log.d("MY_TAG", "audioList: ${audioList}")
//                                    if (audioList.isNotEmpty())
//                                        text = audioList[0].desc
//                                    Log.d(TAG, "text: $text")
                                    }
                                }
                            }

                            true
                        }
                        cluster.add(marker)

                    }

//                    }
                }
                cluster.invalidate()
                binding.map.overlays.add(cluster)
                binding.map.invalidate()
            }
        }


    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == AudioService.DURATION) {
                locModel = intent.getIntExtra(AudioService.DURATION, 0)
                curPos = intent.getIntExtra(AudioService.CURPOS, 0)
                binding.linearProgressBar.max = locModel
                binding.linearProgressBar.progress = curPos
                Log.d(TAG, "duration: $locModel $curPos")
            }
        }
    }

    private val receiverLoc = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == LocationService.LOC_MODEL_INTENT) {
                val locModel =
                    intent.getSerializableExtra(LocationService.LOC_MODEL_INTENT) as LocationModel
                viewModel.locationUpdates.value = locModel
            }
        }
    }

    private fun registerLocReceiver() {
        val locFilter = IntentFilter(LocationService.LOC_MODEL_INTENT)
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .registerReceiver(receiverLoc, locFilter)
    }

    private fun setOnClicks(lat: String, lon: String, genId: Long) = with(binding) {
        val listener = onClicks()
        binding.btnGo.setOnClickListener(listener)
        sharedPreferences.save("placeLat", lat)
        sharedPreferences.save("placeLon", lon)
        sharedPreferences.save("placeGenId", genId.toString())
    }

    private fun onClicks(): OnClickListener {
        return OnClickListener {
            when (it.id) {
                com.example.travel.R.id.btnGo -> startStopService()
            }
        }
    }


    private fun startStopService() {
        if (!isServiceRunning) {
            startLocService()
        } else {
            activity?.stopService(Intent(activity, LocationService::class.java))
//            binding.btnGo.setImageResource(R.drawable.ic_play)
//            timer?.cancel()
        }
        isServiceRunning = !isServiceRunning
    }

    private fun checkServiceState() {
        isServiceRunning = LocationService.isRunning
        if (isServiceRunning) {
//            binding.fStartStop.setImageResource(R.drawable.ic_stop)
//            startTimer()
        }
    }

    private fun locationUpdates() = with(binding) {

        viewModel.locationUpdates.observe(viewLifecycleOwner) {
            updatePolyline(it.geoPointsList)
            Log.d(TAG, "myLocation: ${it.geoPointsList.last().latitude} ${it.geoPointsList.last().longitude}")
            Log.d(TAG, "myLocation2: ${sharedPreferences.getStringValue("placeLat")?.toDouble() ?: 0.0} ${sharedPreferences.getStringValue("placeLon")?.toDouble() ?: 0.0}")
            Log.d(TAG, "myLocation3: ${getDistanceFromLatLonInKm(it.geoPointsList.last().latitude, it.geoPointsList.last().longitude, sharedPreferences.getStringValue("placeLat")?.toDouble() ?: 0.0, sharedPreferences.getStringValue("placeLon")?.toDouble() ?: 0.0) * 1000}")

            val distance = getDistanceFromLatLonInKm(
                it.geoPointsList.last().latitude,
                it.geoPointsList.last().longitude,
//                55.784181,
//                37.711021
//                55.670020, 37.279790
                sharedPreferences.getStringValue("placeLat")?.toDouble() ?: 0.0,
                sharedPreferences.getStringValue("placeLon")?.toDouble() ?: 0.0
            ) * 1000

            if (distance <= 20) {
//                fl = true
                activity?.stopService(Intent(activity, LocationService::class.java))
                viewModel.updatePlaceVisited(true, sharedPreferences.getStringValue("placeGenId")?.toLong() ?: 0)
                viewModel.getPlaceListData(cityId, search, category)
                Log.d("MY_TAG", "token: ${token} ")

                if ((activity as MainActivity).checkAuthTime()) {
                    viewModelProfile.updateScoresFromApi(token, UpdateScoresRequest(currentUser.scores + 10))
                    Snackify.success(
                        binding.detailContainer,
                        "Место посещено! +10 баллов!",
                        Snackify.LENGTH_LONG
                    ).show()

                } else {
                    Snackify.success(
                        binding.detailContainer,
                        "Место посещено!",
                        Snackify.LENGTH_LONG
                    ).show()
                }
            } else {
                Snackify.info(
                    binding.detailContainer,
                    "До места $distance м!",
                    Snackify.LENGTH_LONG
                ).show()
            }
        }
    }

//    private fun getDrawableForPlaceType(place: PlaceModel?): Drawable? {
//        place ?: return null
//        val context = requireContext()
//        return when (place.subTypePlace) {
//            "city" -> null // если нужно
//            "place" -> ContextCompat.getDrawable(context, com.example.travel.R.drawable.ic_tree)
//            "building" -> ContextCompat.getDrawable(context, com.example.travel.R.drawable.ic_museum)
//            "attraction" -> ContextCompat.getDrawable(context, com.example.travel.R.drawable.ic_monument)
//            // Добавьте другие условия для отображения соответствующих иконок
//            else -> null
//        }
//    }

    private fun addPoint(list: List<GeoPoint>) {
        pl?.addPoint(list[list.size - 1])
    }

    private fun fillPolyline(list: List<GeoPoint>) {
        list.forEach {
            pl?.addPoint(it)
        }
    }

    private fun updatePolyline(list: List<GeoPoint>) {
        if (list.size > 1 && firstStart) {
            fillPolyline(list)
            firstStart = true
        } else {
            addPoint(list)
        }

    }


    private fun startLocService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(Intent(activity, LocationService::class.java))
        } else {
            activity?.startService(Intent(activity, LocationService::class.java))
        }
//        binding.fStartStop.setImageResource(R.drawable.ic_stop)
        LocationService.startTime = System.currentTimeMillis()
//        startTimer()
    }

    private fun registerReceiver() {
        val locFilter = IntentFilter(AudioService.DURATION)
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .registerReceiver(receiver, locFilter)
    }

    private fun initSeekBar() {
        val seekBar = binding.linearProgressBar

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, pos: Int, changed: Boolean) {
                if (changed && isBound) {
                    service?.seekTo(pos)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                // Опционально: остановить автообновление SeekBar при перетаскивании
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                // Опционально: возобновить автообновление SeekBar после отпускания
            }
        })

        // Запуск Relay для обновления SeekBar каждую секунду
        val runnable = object : Runnable {
            override fun run() {
                seekBar.progress = curPos
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(runnable, 1000)
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
        mIntent = Intent(requireContext(), AudioService::class.java)
        poppulateFiles() //.mp3
        mIntent.putExtra("tracklist", trackFilesArrayList)
        mIntent.putExtra("action_mucsic_backser", 2)
        binding.fStop.visibility = View.GONE
        binding.fStart.setOnClickListener() {
            flag = true
            activity?.startService(mIntent)
//            Snackify.info(
//                binding.detailContainer,
//                "Для прослушивания аудиогида купите подписку!",
//                Snackify.LENGTH_LONG
//            ).show()
            initSeekBar()
            binding.fStop.visibility = View.VISIBLE
            binding.fStart.visibility = View.GONE
        }
        binding.fStop.setOnClickListener() {
            flag = false
//            activity?.stopService(mIntent)
            callPauseOnService()
            binding.fStart.visibility = View.VISIBLE
            binding.fStop.visibility = View.GONE
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
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            val read =
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
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
                    Log.d(
                        "MY_TAG",
                        "onRequestPermissionsResult: External Storage Permission granted"
                    )
//                    createFolder()
                } else {
                    //External Storage Permission denied...
                    Log.d(
                        "MY_TAG",
                        "onRequestPermissionsResult: External Storage Permission denied..."
                    )
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

        trackFilesArrayList.add(
            AudioData(
                filePath
            )
        )
    }


    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("ru")
            Log.d(TAG, "onInit: $status")
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                viewModel.place.collect { currentPlace ->
                    withContext(Dispatchers.Main) {
//                        activity?.stopService(mIntent)
                        val filename = "UniqueFileName" // Уникальное имя для этой операции

                        // Ассоциируем имя файла с результатом TTS
                        val params = Bundle()
                        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "")
//                        if (currentPlace.description == "") {
//                            binding.containerAudio.visibility = View.GONE
//                            return@withContext
//                        }
                        // Синтез речи с записью в файл
                        tts?.synthesizeToFile(
                            text,
                            params,
                            File(outputPath),
                            filename
                        )

                        // Установка слушателя для отслеживания успешного сохранения
                        tts?.setOnUtteranceProgressListener(object :
                            UtteranceProgressListener() {
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
                } else {
                    isFineLocationPermissionGranted =
                        permissions[Manifest.permission.ACCESS_FINE_LOCATION]
                            ?: isFineLocationPermissionGranted
                }

            }
    }

//    private fun checkLocationEnabled() {
//        val locManager =
//            (activity as AppCompatActivity).getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val isEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        Log.d("MyLog", "checkLocationEnabled: $isEnabled")
//        if (!isEnabled) {
//            DialogManager.showYesNoDialog(
//                activity as AppCompatActivity, "Dialog",
//                "Message", myClickListener
//            )
//        } else {
//            showToast("Loc enabled")
//        }
//    }

    private val myClickListener =
        DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

        }

    private fun requestPermissions() {
        isFineLocationPermissionGranted = checkPermissionGranted(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        isReadPermissionGranted = checkPermissionGranted(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        isWritePermissionGranted = checkPermissionGranted(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isCoarseLocationPermissionGranted = checkPermissionGranted(
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            isBackgroundLocationPermissionGranted = checkPermissionGranted(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
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
            isReadPermissionGranted,
            Manifest.permission.READ_EXTERNAL_STORAGE, permissionRequestList
        )

        addPermissionToRequestedList(
            isWritePermissionGranted,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, permissionRequestList
        )

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
        pl = Polyline()
        pl?.outlinePaint?.color = Color.BLUE
        map.controller.setZoom(20.0)
        val mLocOverlay = MyLocationNewOverlay(mLocProvider, map)
        mLocOverlay.enableMyLocation()
        mLocOverlay.runOnFirstFix {
            val myLocationGeoPoint = mLocOverlay.myLocation
            map.overlays.add(mLocOverlay)
            myLat = myLocationGeoPoint?.latitude
            myLon = myLocationGeoPoint?.longitude
            Log.d("LOCATION", "Широта: $myLat, Долгота: $myLon")
            map.overlays.add(pl)
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

//        val distance = getDistanceFromLatLonInKm(myLat!!, myLon!!, 55.784181, 37.711021)
//        Log.d("MY_TAG", "distance: ${distance} ")
//
//        if (distance <= 20) {
//            Toast.makeText(requireContext(), "Рядом!", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(requireContext(), "Не рядом!", Toast.LENGTH_SHORT).show()
//        }
    }

    fun deg2rad(deg: Double): Double {
        return deg * (PI / 180)
    }

    private fun getDistanceFromLatLonInKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val radius = 6378
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

    private fun openRegisterFragment() {
        if (!(activity as MainActivity).checkAuthTime()) {
            findNavController().navigate(com.example.travel.R.id.requireRegisterFragment)
        }
    }

}