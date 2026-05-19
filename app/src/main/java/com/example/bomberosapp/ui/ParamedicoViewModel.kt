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

    fun eliminarParamedico(id: String, onSuccess: () -> Unit) {
        db.collection("paramedicos")
            .document(id)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
    }

    fun actualizarParamedico(paramedico: Paramedico, onSuccess: () -> Unit) {
        db.collection("paramedicos")
            .document(paramedico.id)
            .set(paramedico)
            .addOnSuccessListener {
                onSuccess()
            }
    }

    private fun getParamedicosFlow(): Flow<List<Paramedico>> = callbackFlow {
        val registration = db.collection("paramedicos")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { document ->
                    val paramedico = document.toObject(Paramedico::class.java)
                    paramedico?.copy(id = document.id)
                } ?: emptyList()

                trySend(list)
            }

        awaitClose { registration.remove() }
    }
}