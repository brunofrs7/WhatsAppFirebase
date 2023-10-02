package com.example.whatsappfirebase.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsappfirebase.databinding.ActivityLoginBinding
import com.example.whatsappfirebase.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val auth by lazy { FirebaseAuth.getInstance() }

    private lateinit var email: String
    private lateinit var senha: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initClickEvents()
    }

    override fun onStart() {
        super.onStart()
        verificarAutenticado()
    }

    private fun verificarAutenticado() {
        val utilizador = auth.currentUser
        if (utilizador != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun validarCampos(): Boolean {
        email = binding.editLoginEmail.text.toString()
        senha = binding.editLoginSenha.text.toString()

        if (email.isNotEmpty()) {
            binding.textInputLoginEmail.error = null
            if (senha.isNotEmpty()) {
                binding.textInputLoginSenha.error = null
                return true
            } else {
                binding.textInputLoginSenha.error = "Preencha a sua senha"
                return false
            }
        } else {
            binding.textInputLoginEmail.error = "Preencha o seu email"
            return false
        }
    }

    private fun initClickEvents() {
        binding.textRegistar.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RegistoActivity::class.java
                )
            )
        }
        binding.buttonEntrar.setOnClickListener {
            if (validarCampos()) {
                autenticar()
            }
        }
    }

    private fun autenticar() {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener {
                showMessage("Bem-vindo!")
                startActivity(Intent(this, MainActivity::class.java))
            }
            .addOnFailureListener { erro ->
                try {
                    throw erro
                } catch (erro: FirebaseAuthInvalidUserException) {
                    erro.printStackTrace()
                    showMessage("Email não existente")
                } catch (erro: FirebaseAuthInvalidCredentialsException) {
                    erro.printStackTrace()
                    showMessage("Email e/ou senha incorretos")  //valida apenas senha
                }
            }
    }
}