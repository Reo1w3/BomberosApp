package com.example.bomberosapp.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun OsmMapView(
    modifier: Modifier = Modifier,
    center: GeoPoint = GeoPoint(14.6349, -90.5069), // Guatemala City default
    zoomLevel: Double = 15.0,
    onLocationSelected: (GeoPoint) -> Unit = {},
    onMapReady: (MapView) -> Unit = {}
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val marker = remember { Marker(mapView) }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    // Sincronizar el centro y marcador solo cuando cambia la propiedad 'center'
    LaunchedEffect(center) {
        mapView.controller.setCenter(center)
        marker.position = center
        mapView.invalidate()
    }

    // Sincronizar el zoom solo cuando cambia la propiedad 'zoomLevel'
    LaunchedEffect(zoomLevel) {
        mapView.controller.setZoom(zoomLevel)
    }

    AndroidView(
        factory = {
            mapView.apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = "Ubicación"
                overlays.add(marker)

                // Listener para capturar toque en el mapa
                val mapEventsOverlay = org.osmdroid.views.overlay.MapEventsOverlay(object : org.osmdroid.events.MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                        marker.position = p
                        mapView.invalidate()
                        onLocationSelected(p)
                        return true
                    }
                    override fun longPressHelper(p: GeoPoint): Boolean = false
                })
                overlays.add(0, mapEventsOverlay)
                
                onMapReady(this)
            }
        },
        update = {
            // El bloque update se deja vacío para evitar que el mapa regrese al centro
            // ante cualquier recomposición trivial de la UI (como estados de carga).
            // Los cambios en 'center' se manejan de forma controlada con LaunchedEffect.
        },
        modifier = modifier.fillMaxSize()
    )
}
