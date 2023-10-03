package com.example.whatsappfirebase.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappfirebase.adapters.MensagensAdapter
import com.example.whatsappfirebase.databinding.ActivityMensagensBinding
import com.example.whatsappfirebase.model.Conversa
import com.example.whatsappfirebase.model.Mensagem
import com.example.whatsappfirebase.model.Utilizador
import com.example.whatsappfirebase.utils.Constantes
import com.example.whatsappfirebase.utils.Constantes.BD_CONVERSAS
import com.example.whatsappfirebase.utils.Constantes.BD_MENSAGENS
import com.example.whatsappfirebase.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

class MensagensActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMensagensBinding.inflate(layoutInflater) }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private var dadosDestinatario: Utilizador? = null
    private var dadosRemetente: Utilizador? = null
    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var conversasAdapter: MensagensAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recuperarDadosUtilizadores()
        initToolbar()
        initClickEvents()
        initRecyclerView()
        initListeners()
    }

    private fun initRecyclerView() {
        with(binding) {
            conversasAdapter = MensagensAdapter()
            rvMensagens.adapter = conversasAdapter
            rvMensagens.layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    private fun initListeners() {
        val idRemetente = auth.currentUser?.uid
        val idDestinatario = dadosDestinatario?.id
        if (idRemetente != null && idDestinatario != null) {
            listenerRegistration = db.collection(BD_MENSAGENS)
                .document(idRemetente)
                .collection(idDestinatario)
                .orderBy("data", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshow, error ->
                    if (error != null) {
                        showMessage("Erro ao carregar mensagens")
                    } else {
                        val listaMensagens = mutableListOf<Mensagem>()
                        val documentos = querySnapshow?.documents
                        documentos?.forEach { documentSnapshot ->
                            val mensagem = documentSnapshot.toObject(Mensagem::class.java)
                            if (mensagem != null) {
                                listaMensagens.add(mensagem)
                                Log.i("show_messages", "${mensagem.data}")
                            }
                        }
                        if (listaMensagens.isNotEmpty()) {
                            //carregar os dados no adapter
                            conversasAdapter.adicionarLista(listaMensagens)
                        }
                    }

                }
        }
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

                //guardar conversa para o remetente (foto e nome do destinatario)
                val conversaRemetente =
                    Conversa(
                        idRemetente,
                        idDestinatario,
                        dadosDestinatario!!.foto,
                        dadosDestinatario!!.nome,
                        textoMensagem
                    )
                guardarConversa(conversaRemetente)

                //guardar para o destinatário
                guardarMensagem(idDestinatario, idRemetente, mensagem)

                //guardar conversa para o destinatario (foto e nome do remetente)
                db.collection("utilizadores").document(idRemetente)
                val conversaDestinatario =
                    Conversa(
                        idDestinatario,
                        idRemetente,
                        dadosRemetente!!.foto,
                        dadosRemetente!!.nome,
                        textoMensagem
                    )
                guardarConversa(conversaDestinatario)

                binding.editMensagem.setText("")
            }
        }
    }

    private fun guardarConversa(conversa: Conversa) {
        db.collection(BD_CONVERSAS)
            .document(conversa.idRemetente)
            .collection(Constantes.BD_ULTIMAS_CONVERSAS)
            .document(conversa.idDestinatario)
            .set(conversa)
            .addOnFailureListener {
                showMessage("Erro ao enviar mensagem")
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

    private fun recuperarDadosUtilizadores() {
        //Recuperar dados autenticado
        val id = auth.currentUser?.uid
        if (id != null) {
            db.collection(Constantes.BD_UTILIZADORES)
                .document(id)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val utilizador = documentSnapshot.toObject(Utilizador::class.java)
                    if (utilizador != null) {
                        dadosRemetente = utilizador
                    }
                }
        }
        //Recuperar dados Destinatário
        val extras = intent.extras
        if (extras != null) {
            dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable("dadosDestinatario", Utilizador::class.java)
            } else {
                extras.getParcelable("dadosDestinatario")
            }
        }
    }
}