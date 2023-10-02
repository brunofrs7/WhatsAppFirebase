package com.example.whatsappfirebase.activities

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsappfirebase.databinding.ActivityMensagensBinding
import com.example.whatsappfirebase.model.Mensagem
import com.example.whatsappfirebase.model.Utilizador
import com.example.whatsappfirebase.utils.Constantes
import com.example.whatsappfirebase.utils.Constantes.BD_MENSAGENS
import com.example.whatsappfirebase.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class MensagensActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMensagensBinding.inflate(layoutInflater) }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private var dadosDestinatario: Utilizador? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recuperarDadosUtilizadorDestinatario()
        initToolbar()
        initClickEvents()

    }

    private fun initClickEvents() {
        binding.fabEnviar.setOnClickListener {
            val mensagem = binding.editMensagem.text.toString()
            gravarMensagem(mensagem)
        }
    }

    private fun gravarMensagem(textoMensagem: String) {
        if (textoMensagem.isNotEmpty()) {
            val idRemetente = auth.currentUser?.uid
            val idDestinatario = dadosDestinatario?.id

            if (idRemetente != null && idDestinatario != null) {

                val mensagem = Mensagem(idRemetente, textoMensagem)

                //guardar para o remetente
                guardarMensagem(idRemetente, idDestinatario, mensagem)

                //guardar para o destinatário
                guardarMensagem(idDestinatario, idRemetente, mensagem)

                binding.editMensagem.setText("")
            }
        }
    }

    private fun guardarMensagem(idRemetente: String, idDestinatario: String, mensagem: Mensagem) {
        db.collection(BD_MENSAGENS)
            .document(idRemetente)
            .collection(idDestinatario)
            .add(mensagem)
            .addOnFailureListener {
                showMessage("Erro ao enviar mensagem")
            }
    }

    private fun initToolbar() {
        val toolbar = binding.tbMensagens
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            if (dadosDestinatario != null) {
                binding.textNome.text = dadosDestinatario!!.nome
                Picasso.get().load(dadosDestinatario!!.foto).into(binding.imageFotoPerfil)
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun recuperarDadosUtilizadorDestinatario() {
        val extras = intent.extras
        if (extras != null) {
            val origem = extras.getString("origem")
            if (origem == Constantes.ORIGEM_CONTACTO) {
                dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable("dadosDestinatario", Utilizador::class.java)
                } else {
                    extras.getParcelable("dadosDestinatario")
                }
            } else if (origem == Constantes.ORIGEM_CONVERSA) {

            }
        }
    }
}