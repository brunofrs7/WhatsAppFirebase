package com.example.whatsappfirebase.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Conversa(
    val idRemetente: String = "",
    val idDestinatario: String = "",
    val foto: String = "",
    val nome: String = "",
    val ultimaMensagem: String = "",
    @ServerTimestamp
    val data: Date? = null,
)