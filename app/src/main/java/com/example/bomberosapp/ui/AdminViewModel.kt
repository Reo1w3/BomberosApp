package com.example.bomberosapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bomberosapp.data.model.Emergency
import com.example.bomberosapp.data.network.FirebaseClient
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val db = FirebaseClient.db
    private var isObserving = false

    var emergencies by mutableStateOf<List<Emergency>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun startObserving() {
        if (isObserving) return
        isObserving = true
        observeEmergencies()
    }

    private fun observeEmergencies() {
        isLoading = true
        viewModelScope.launch {
            FirebaseClient.connect()
            getEmergenciesFlow().collectLatest { list ->
                emergencies = list
                isLoading = false
            }
        }
    }

    private fun getEmergenciesFlow(): Flow<List<Emergency>> = callbackFlow {
        val registration = db.collection("emergencias")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Emergency::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { registration.remove() }
    }
}
