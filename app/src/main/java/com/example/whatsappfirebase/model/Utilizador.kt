package com.example.whatsappfirebase.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Utilizador(
    var id: String = "",
    var nome: String = "",
    var email: String = "",
    var foto: String = ""
) : Parcelable