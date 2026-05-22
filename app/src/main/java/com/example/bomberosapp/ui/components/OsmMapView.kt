package com.example.bomberosapp.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

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

    DisposableEffect(Unit) {
        onDispose {
            mapView.onDetach()
        }
    }

    AndroidView(
        factory = {
            mapView.apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(zoomLevel)
                controller.setCenter(center)
                
                marker.position = center
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
        modifier = modifier.fillMaxSize()
    )
}
