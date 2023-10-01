package com.example.whatsappfirebase.model

data class Utilizador(
    var id: String,
    var nome: String,
    var email: String,
    var foto: String = ""
)