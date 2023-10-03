package com.example.whatsappfirebase.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappfirebase.R
import com.example.whatsappfirebase.activities.MensagensActivity
import com.example.whatsappfirebase.adapters.ContactosAdapter
import com.example.whatsappfirebase.adapters.ConversasAdapter
import com.example.whatsappfirebase.databinding.FragmentContactosBinding
import com.example.whatsappfirebase.databinding.FragmentConversasBinding
import com.example.whatsappfirebase.model.Conversa
import com.example.whatsappfirebase.model.Utilizador
import com.example.whatsappfirebase.utils.Constantes
import com.example.whatsappfirebase.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ConversasFragment : Fragment() {
    private lateinit var binding: FragmentConversasBinding
    private lateinit var eventoSnapshot: ListenerRegistration
    private lateinit var conversasAdapter: ConversasAdapter

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConversasBinding.inflate(inflater, container, false)
        conversasAdapter = ConversasAdapter { conversa ->
            val intent = Intent(context, MensagensActivity::class.java)
            val utilizador = Utilizador(
                id = conversa.idDestinatario,
                nome = conversa.nome,
                foto = conversa.foto
            )
            intent.putExtra("dadosDestinatario", utilizador)
            //intent.putExtra("origem", Constantes.ORIGEM_CONVERSA)
            startActivity(intent)
        }
        binding.rvConversas.adapter = conversasAdapter
        binding.rvConversas.layoutManager = LinearLayoutManager(context)
        binding.rvConversas.addItemDecoration(
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerConversas()
    }

    private fun adicionarListenerConversas() {
        val id = auth.currentUser?.uid
        if (id != null) {
            eventoSnapshot = db.collection(Constantes.BD_CONVERSAS)
                .document(id)
                .collection(Constantes.BD_ULTIMAS_CONVERSAS)
                .orderBy("data", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        activity?.showMessage("Erro ao carregar conversas")
                    } else {
                        val documents = querySnapshot?.documents
                        val listaConversas = mutableListOf<Conversa>()
                        documents?.forEach { documentSnapshot ->
                            val conversa = documentSnapshot.toObject(Conversa::class.java)
                            if (conversa != null) {
                                listaConversas.add(conversa)
                            }
                        }
                        // atualiza o RecyclerView
                        if (listaConversas.isNotEmpty()) {
                            conversasAdapter.adicionarLista(listaConversas)
                        }
                    }
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }
}