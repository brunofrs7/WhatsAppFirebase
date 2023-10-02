package com.example.whatsappfirebase.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Mensagem(
    var id: String = "",
    var mensagem: String = "",
    @ServerTimestamp
    var data: Date? = null,
)
