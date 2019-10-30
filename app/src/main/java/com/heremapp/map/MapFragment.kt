package com.heremapp.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.heremapp.R
import com.heremapp.communication.geo.HereGeoService
import com.heremapp.communication.places.HerePlacesService
import com.heremapp.data.PersistedDataStore
import com.heremapp.data.models.place.Place
import com.heremapp.databinding.FragmentMapBinding
import com.heremapp.presentation.extensions.onChanged
import com.heremapp.presentation.main.MainViewModel
import com.heremapp.presentation.main.PlaceViewModel
import com.heremapp.presentation.map.MapFragmentViewModel
import com.heremapp.utility.messaging.MessageHandler
import com.heremapp.utility.messaging.MessageHandler.Companion.LENGTH_LONG
import com.heremapp.utility.messaging.MessageHandler.Companion.log
import com.location.LocationService
import com.trello.rxlifecycle2.components.support.RxFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Screen that displays a map with user's current location and any points of interest selected by the user.
 */
class MapFragment : RxFragment(), OnMapReadyCallback, OnMarkerClickListener, OnMapClickListener {

    companion object {
        // Camera constants
        const val CAMERA_ZOOM_MAX = 17f
        const val CAMERA_ZOOM_MED = 14f
        const val CAMERA_ZOOM_MIN = 10f

        // Search radius constants
        const val RADIUS_STROKE_WIDTH = 4f
    }

    @Inject
    internal lateinit var messageHandler: MessageHandler

    @Inject
    internal lateinit var locationService: LocationService

    @Inject
    internal lateinit var hereGeoService: HereGeoService

    @Inject
    internal lateinit var herePlacesService: HerePlacesService

    @Inject
    internal lateinit var mainViewModel: MainViewModel

    @Inject
    internal lateinit var dataStore: PersistedDataStore

    private lateinit var gmap: GoogleMap
    private lateinit var binding: FragmentMapBinding

    private var isMapInitialized = false
    private var searchCircle: Circle? = null

    // A map of place Id's to markers, representing the discovered places.
    private val placeMarkers by lazy { HashMap<String, Marker>() }
    private val map: MapView by lazy { binding.map }

    /*
     * Listens for scale gestures, if search mode is enabled scales the search radius and disables the map zoom until
     * scaling has ended.
     */
    private val scaleGestureListener = object : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            // If search mode is enabled disable map zoom gestures and apply scaling to search radius
            if (viewModel.isSearchModeEnabled.get()) {
                gmap.uiSettings.isZoomGesturesEnabled = false
                viewModel.onScaleGestureDetected(detector.scaleFactor)
            }
            return super.onScale(detector)
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            // Make sure to re-enable zoom gestures for map
            gmap.uiSettings.isZoomGesturesEnabled = true
            super.onScaleEnd(detector)
        }
    }

    private val viewModel: MapFragmentViewModel by lazy {
        MapFragmentViewModel(
            hereGeoService,
            locationService,
            mainViewModel,
            herePlacesService,
            dataStore,
            lifecycle()
        )
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_map, null, true)
        binding.viewModel = viewModel
        // Set the scale gesture detector to intercept touch events. Necessary to avoid propagation to mapview.
        binding.mapInterceptor.scaleGestureListener = scaleGestureListener
        requestMap(savedInstanceState)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onStart() {
        super.onStart()
        map.onStart()
    }

    override fun onStop() {
        super.onStop()
        map.onStop()
    }

    override fun onPause() {
        map.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        map.onDestroy()
        isMapInitialized = false
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map.onLowMemory()
    }

    /**
     * Map is initialized, now we can react to location & discovery updates and display them on the map.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        log("Gmap initialized, setting listeners..")
        gmap = googleMap
        isMapInitialized = true

        // Set map related listeners
        gmap.setOnMarkerClickListener(this)
        gmap.setOnMapClickListener(this)

        // Set view model listeners that are contingent on map initialization
        viewModel.lastKnownLocation.onChanged(::initMapForNewLocation)
        viewModel.discoveredPlaces.onChanged(::setDiscoveredPlaces)
        viewModel.selectedPlace.onChanged(::onPlaceSelected)
        viewModel.isSearchModeEnabled.onChanged(::onSearchModeEnabledChanged)
        viewModel.searchRadius.onChanged(::onSearchRadiusChanged)

        // Set the known locations on the new map
        setDiscoveredPlaces(viewModel.discoveredPlaces.get())
        // Finally set map to known location
        initMapForNewLocation(viewModel.lastKnownLocation.get())

    }

    override fun onMapClick(latLng: LatLng) {
        // Click events on markers will be captured separately and not trigger this function
        viewModel.onEmptyMapLocationClicked()
    }

    /**
     * Marker has been clicked, find the selected marker in the member map and notify the viewmodel of the selected
     * place's id.
     */
    override fun onMarkerClick(marker: Marker): Boolean {
        log(String.format("Marker clicked: {Title:%s|LatLng:%s,%s}", marker.title, marker.position.latitude, marker.position.longitude))
        placeMarkers.entries.firstOrNull { it.value == marker }
            ?.let {
            viewModel.onPlaceMarkerClicked(it.key)
        }
        return false
    }

    private fun requestMap(savedState: Bundle?) {
        map.onCreate(savedState)

        if (!isMapInitialized) {
            map.getMapAsync(this)
        }
    }

    /**
     * Basic map initialization per location, location, zoom, search radius circle.
     */
    private fun initMapForNewLocation(location: Location?) {
        if (location != null) {
            log("Updating map for new location: [${location.latitude},${location.longitude}]")

            initGmap()
            setMapAtCoordinates(LatLng(location.latitude, location.longitude))

            val newSearchCircle = gmap.addCircle(CircleOptions()
                .center(LatLng(location.latitude, location.longitude))
                .radius(viewModel.searchRadius.get())
                .strokeWidth(RADIUS_STROKE_WIDTH)
                .strokeColor(ContextCompat.getColor(map.context, R.color.BlueAlpha))
                .fillColor(ContextCompat.getColor(map.context, R.color.DodgerBlueAlpha))
                .visible(viewModel.isSearchModeEnabled.get()))

            searchCircle?.remove()
            searchCircle = newSearchCircle
        } else {
            searchCircle?.remove()
            log("Unknown location!")
        }
    }

    /**
     * Set the map centered at the given [LatLng].
     */
    private fun setMapAtCoordinates(latLng: LatLng) {
        // Animate camera to last known location at medium zoom.
        gmap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(latLng)
                    .zoom(CAMERA_ZOOM_MED)
                    .build()
            )
        )
    }

    /**
     * Display or hide the search radius based on the given value.
     */
    private fun onSearchModeEnabledChanged(isDisplayed: Boolean) {
        log("Search radius display state changed, is displayed: $isDisplayed")
        searchCircle?.isVisible = isDisplayed
    }

    /**
     * Update the search circle radius to the given radius. Triggered by scale events.
     */
    private fun onSearchRadiusChanged(radius: Double) {
        log("Search radius changed: $radius")
        searchCircle?.radius = radius.toDouble()
    }

    /**
     * A new place has been selected by the user, select corresponding marker and move map to its location.
     */
    private fun onPlaceSelected(place: PlaceViewModel?) {
        log("OnPlaceSelected:\n$place")
        place?.let { placeSafe ->
            placeSafe.position.get().let(::setMapAtCoordinates)
            placeMarkers[placeSafe.id.get()]?.showInfoWindow()
        }
    }

    /**
     * Update the map with the given list of [Place]'s.
     */
    private fun setDiscoveredPlaces(places: List<PlaceViewModel>?) {
        log("Clearing previous markers!")
        placeMarkers.forEach { it.value.remove() }
        placeMarkers.clear()

        places?.forEach { place ->
            log("Adding new markers!")

            Glide.with(map.context)
                .setDefaultRequestOptions(
                    RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .asBitmap()
                .load(place.icon.get())
                .into(object : SimpleTarget<Bitmap>() {

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        placeMarkers[place.id.get()] = gmap.addMarker(
                            MarkerOptions()
                                .position(place.position.get())
                                .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                .title(place.title.get())
                        )
                    }
                })
        }
    }

    /**
     * Set default map properties, this method assumes permissions have already been validated.
     */
    @SuppressLint("MissingPermission")
    private fun initGmap() {
        gmap.isMyLocationEnabled = true
        gmap.setMinZoomPreference(CAMERA_ZOOM_MIN)
        gmap.setMaxZoomPreference(CAMERA_ZOOM_MAX)
        gmap.setOnMyLocationButtonClickListener(::onMyLocationClicked)
    }

    /**
     * Display last known user locations address.
     */
    private fun onMyLocationClicked(): Boolean {
        val address = viewModel.lastKnownAddress.get()

        if (address == null) {
            // TODO: Make funny stuffs like UFO's & shit.
        } else {
            messageHandler.message(
                binding.root,
                LENGTH_LONG,
                resources.getString(R.string.map_current_address_text, address.toStreetAddressString())
            )
        }

        return false
    }
}
