package com.example.bomberosapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bomberosapp.data.model.Paramedico
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ParamedicoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    var paramedicos by mutableStateOf<List<Paramedico>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun startObserving() {
        isLoading = true
        viewModelScope.launch {
            getParamedicosFlow().collectLatest { list ->
                paramedicos = list
                isLoading = false
            }
        }
    }

    private fun getParamedicosFlow(): Flow<List<Paramedico>> = callbackFlow {
        val registration = db.collection("paramedicos")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Paramedico::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(list)
            }

        awaitClose { registration.remove() }
    }

    fun actualizarParamedico(paramedico: Paramedico, onSuccess: () -> Unit) {
        if (paramedico.id.isBlank()) return

        viewModelScope.launch {
            try {
                db.collection("paramedicos")
                    .document(paramedico.id)
                    .set(paramedico)
                    .await()

                onSuccess()
            } catch (e: Exception) {
            }
        }
    }

    fun eliminarParamedico(id: String, onSuccess: () -> Unit) {
        if (id.isBlank()) return

        viewModelScope.launch {
            try {
                db.collection("paramedicos")
                    .document(id)
                    .delete()
                    .await()

                onSuccess()
            } catch (e: Exception) {
            }
        }
    }
}