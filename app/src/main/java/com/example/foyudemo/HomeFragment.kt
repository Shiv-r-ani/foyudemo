package com.example.foyudemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var tvLocation: TextView

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) getLocation() else tvLocation.text = "Permission Denied"
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Location setup
        val locationLayout = view.findViewById<LinearLayout>(R.id.locationlayout)
        tvLocation = view.findViewById(R.id.loc)
        locationLayout.setOnClickListener { checkLocationPermission() }

        // Service list
        val fullServiceList = listOf(
            ServiceItem("CarCleaning",   "Doorstep car cleaning services", R.drawable.car),
            ServiceItem("Plumbing",      "Fix leaks and pipe issues",       R.drawable.plumbing),
            ServiceItem("HouseCleaning", "Deep home cleaning",              R.drawable.broom),
            ServiceItem("Electrician",   "All electrical work done",        R.drawable.electrician),
            ServiceItem("PestControl",   "Get rid of your pests",           R.drawable.pestcontrol),
            ServiceItem("PetGrooming",   "Squeaky clean pets",              R.drawable.petgrooming),
            ServiceItem("Shifting",      "Shift your things",               R.drawable.shifting),
            ServiceItem("Furnishing",    "Get your house furnished",        R.drawable.furnishing)
        )

        // RecyclerView setup
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = buildAdapter(fullServiceList)

        // Search bar setup
        val searchBar = view.findViewById<EditText>(R.id.searchBar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                val filtered = if (query.isBlank()) {
                    fullServiceList
                } else {
                    fullServiceList.filter {
                        it.title.contains(query, ignoreCase = true) ||
                                it.description.contains(query, ignoreCase = true)
                    }
                }
                recyclerView.adapter = buildAdapter(filtered)
            }
        })

        // Auto fetch location if permission already granted
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        }
    }

    // Builds adapter with click navigation
    private fun buildAdapter(list: List<ServiceItem>): Adapter {
        return Adapter(list) { item ->
            val intent = when (item.title) {
                "CarCleaning"   -> Intent(requireContext(), CarCleaning::class.java)
                "HouseCleaning" -> Intent(requireContext(), HouseCleaningActivity::class.java)
                "Electrician"   -> Intent(requireContext(), ElectricianActivity::class.java)
                "Plumbing"-> Intent(requireContext(), PlumbingActivity::class.java)
                "PestControl"   -> Intent(requireContext(), PestControlActivity::class.java)
                "PetGrooming"   -> Intent(requireContext(), PetGrooming::class.java)
                "Shifting"      -> Intent(requireContext(), Shifting::class.java)
                "Furnishing"    -> Intent(requireContext(), Furnishing::class.java)
                else            -> null
            }
            intent?.let { startActivity(it) }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                try {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val area = address.subLocality
                        val city = address.locality
                        tvLocation.text = when {
                            area != null && city != null -> "$area, $city"
                            city != null                 -> city
                            else                         -> "Location found"
                        }
                    } else {
                        tvLocation.text = "Location not found"
                    }
                } catch (e: Exception) {
                    tvLocation.text = "Location unavailable"
                }
            } else {
                tvLocation.text = "Tap to get location"
            }
        }
    }
}