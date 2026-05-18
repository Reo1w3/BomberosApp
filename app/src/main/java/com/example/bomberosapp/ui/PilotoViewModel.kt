package com.example.bomberosapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bomberosapp.data.model.Piloto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PilotoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    var pilotos by mutableStateOf<List<Piloto>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun startObserving() {
        isLoading = true
        viewModelScope.launch {
            getPilotosFlow().collectLatest { list ->
                pilotos = list
                isLoading = false
            }
        }
    }

    private fun getPilotosFlow(): Flow<List<Piloto>> = callbackFlow {
        val registration = db.collection("pilotos")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull {
                    it.toObject(Piloto::class.java)
                } ?: emptyList()

                trySend(list)
            }

        awaitClose { registration.remove() }
    }
}