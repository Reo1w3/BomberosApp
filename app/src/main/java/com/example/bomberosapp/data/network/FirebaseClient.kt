package com.example.bomberosapp.data.network

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseClient {
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private var isConnecting = false

    suspend fun connect() {
        if (auth.currentUser == null) {
            if (isConnecting) return
            isConnecting = true
            try {
                auth.signInAnonymously().await()
                Log.d("FirebaseClient", "Anonymous auth successful: ${auth.currentUser?.uid}")
            } catch (e: Exception) {
                Log.e("FirebaseClient", "Anonymous auth failed: ${e.message}")
            } finally {
                isConnecting = false
            }
        }
    }
}
