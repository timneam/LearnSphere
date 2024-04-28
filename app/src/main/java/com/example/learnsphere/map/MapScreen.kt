//Change ur package name here
package com.example.learnsphere.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.learnsphere.MainActivity
import com.example.learnsphere.R
import com.example.learnsphere.ui.theme.LearnSphereTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.StreetViewPanoramaOptions
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GroundOverlay
import com.google.maps.android.compose.GroundOverlayPosition
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.streetview.StreetView
import com.google.maps.android.ktx.MapsExperimentalFeature

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class, MapsExperimentalFeature::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    context: Context,
    navController: NavController = rememberNavController()
) {
    val singapore = LatLng(1.37750067423464, 103.84875202354904)
//    val southwest = LatLng(1.37, 103.71) // Coordinates for the southwest corner
//    val northeast = LatLng(1.38, 103.81) // Coordinates for the northeast corner
    val southwest = LatLng(1.3772613263462115, 103.84837312835475)
    val northeast = LatLng(1.3776627020303884, 103.84916768607826)

//    val cameraPositionState = rememberCameraPositionState {
//        position = CameraPosition.fromLatLngZoom(singapore, 30f)
//    }


//    val imageOverlay = BitmapDescriptor("res/drawable/overlay.png")

//    val imageOverlay = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)

    val fusedLocationProviderClient =
        remember { LocationServices.getFusedLocationProviderClient(context) }

    var lastKnownLocation by remember {
        mutableStateOf<Location?>(null)
    }
    var deviceLatLng by remember {
        mutableStateOf(singapore)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
    }



        fusedLocationProviderClient.lastLocation
            .addOnCompleteListener(context as MainActivity) { task ->
        if (task.isSuccessful) {
            // Set the map's camera position to the current location of the device.
            lastKnownLocation = task.result
            deviceLatLng = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
            Log.d("MAP", "Current location is here. Using defaults.")

        } else {
            Log.d("MAP", "Current location is null. Using defaults.")
            Log.e("MAP", "Exception: %s", task.exception)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Direction")
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        },
    ){ innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            Box(
                modifier = Modifier
                    .height(350.dp),
                contentAlignment = Alignment.Center
            ){
                GoogleMap (
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    cameraPositionState = cameraPositionState,
                ) {
                    MarkerInfoWindowContent(
                        state = MarkerState(position = deviceLatLng)
                    ) { marker ->
                        Text(marker.title ?: "You", color = Color.Red)
                    }
//                    Marker(
////                        state = MarkerState(position = singapore),
////                        title = "Singapore",
////                        snippet = "Marker in Singapore"
//                        state = MarkerState(
//                            position = cameraPositionState.position.target
//                        )
//                    )
                    
                    GroundOverlay(
                        position = GroundOverlayPosition.create(LatLngBounds(southwest, northeast)),
                        image = createBitmapDescriptorFromAsset(context, R.drawable.overlay_transparent)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.padding(10.dp)){
                Text(
                    text ="Location: LT3A",
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                   text = "Time: 2pm to 4pm",
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleMedium
                )

                StreetView(
                    streetViewPanoramaOptionsFactory = {
                        StreetViewPanoramaOptions().position(deviceLatLng)
                    },
                    isPanningGesturesEnabled = true,
                    isStreetNamesEnabled = true,
                    isUserNavigationEnabled = true,
                    isZoomGesturesEnabled = true
                )
            }
        }
    }
}


fun createBitmapDescriptorFromAsset(context: Context, drawableId: Int): BitmapDescriptor {
//    val inputStream = context.assets.open(assetName)
//    val bitmap = BitmapFactory.decodeStream(inputStream)

    val bitmap = BitmapFactory.decodeResource(context.resources, drawableId)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@Preview (showBackground = true)
@Composable
fun MapScreenPreview(){
    val context = LocalContext.current
    LearnSphereTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            MapScreen(context = context)
        }
    }
}