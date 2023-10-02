package com.example.whatsappfirebase.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsappfirebase.databinding.ActivityRegistoBinding
import com.example.whatsappfirebase.model.Utilizador
import com.example.whatsappfirebase.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class RegistoActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRegistoBinding.inflate(layoutInflater) }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initToolbar()
        initClickEvents()
    }

    private fun initClickEvents() {
        binding.buttonRegistar.setOnClickListener {
            if (validarCampos()) {
                registarUtilizador(nome, email, senha)
            }
        }
    }

    private fun registarUtilizador(nome: String, email: String, senha: String) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { resultado ->
                if (resultado.isSuccessful) {
                    //gravar no Firestore
                    val id = resultado.result.user?.uid
                    if (id != null) {
                        val utilizador = Utilizador(id, nome, email)
                        gravarUtilizadorFirestore(utilizador)
                    }
                }
            }
            .addOnFailureListener { erro ->
                try {
                    throw erro
                } catch (erro: FirebaseAuthWeakPasswordException) {
                    erro.printStackTrace()
                    showMessage("Insira uma senha forte")
                } catch (erro: FirebaseAuthInvalidCredentialsException) {
                    erro.printStackTrace()
                    showMessage("Email inválido")
                } catch (erro: FirebaseAuthUserCollisionException) {
                    erro.printStackTrace()
                    showMessage("Utilizador já registado")
                }
            }
    }

    private fun gravarUtilizadorFirestore(utilizador: Utilizador) {
        db.collection("utilizadores")
            .document(utilizador.id)
            .set(utilizador)
            .addOnSuccessListener {
                showMessage("Utilizador registado com sucesso")
                startActivity(Intent(this, MainActivity::class.java))
            }
            .addOnFailureListener {
                showMessage("Erro ao registar utilizador")
            }
    }

    private fun validarCampos(): Boolean {
        nome = binding.editNome.text.toString()
        email = binding.editEmail.text.toString()
        senha = binding.editSenha.text.toString()

        if (nome.isNotEmpty()) {
            binding.textInputNome.error = null
            if (email.isNotEmpty()) {
                binding.textInputEmail.error = null
                if (senha.isNotEmpty()) {
                    binding.textInputSenha.error = null
                    return true
                } else {
                    binding.textInputSenha.error = "Preencha a sua senha"
                    return false
                }
            } else {
                binding.textInputEmail.error = "Preencha o seu email"
                return false
            }
        } else {
            binding.textInputNome.error = "Preencha o seu nome"
            return false
        }
    }

    private fun initToolbar() {
        val toolbar = binding.includeToolbar.toolbarPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Registe-se"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}