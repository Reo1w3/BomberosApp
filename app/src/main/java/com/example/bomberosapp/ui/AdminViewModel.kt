package com.example.bomberosapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bomberosapp.data.model.Emergency
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    var emergencies by mutableStateOf<List<Emergency>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        observeEmergencies()
    }

    private fun observeEmergencies() {
        isLoading = true
        viewModelScope.launch {
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
                val list = snapshot?.documents?.mapNotNull { it.toObject(Emergency::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(list)
            }
        awaitClose { registration.remove() }
    }
}
