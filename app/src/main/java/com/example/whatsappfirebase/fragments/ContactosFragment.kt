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
import com.example.whatsappfirebase.activities.MensagensActivity
import com.example.whatsappfirebase.adapters.ContactosAdapter
import com.example.whatsappfirebase.databinding.FragmentContactosBinding
import com.example.whatsappfirebase.model.Utilizador
import com.example.whatsappfirebase.utils.Constantes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ContactosFragment : Fragment() {
    private lateinit var binding: FragmentContactosBinding
    private lateinit var eventoSnapshot: ListenerRegistration
    private lateinit var contactosAdapter: ContactosAdapter
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactosBinding.inflate(inflater, container, false)
        contactosAdapter = ContactosAdapter { utilizador ->
            val intent = Intent(context, MensagensActivity::class.java)
            intent.putExtra("dadosDestinatario", utilizador)
            intent.putExtra("origem", Constantes.ORIGEM_CONTACTO)
            startActivity(intent)
        }
        binding.rvContactos.adapter = contactosAdapter
        binding.rvContactos.layoutManager = LinearLayoutManager(context)
        binding.rvContactos.addItemDecoration(
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerContactos()
    }

    private fun adicionarListenerContactos() {
        val id = auth.currentUser?.uid
        if (id != null) {
            Log.i("fragment_contactos", "$id")
            val listaContactos = mutableListOf<Utilizador>()
            eventoSnapshot = db.collection("utilizadores")
                .addSnapshotListener { querySnapshot, erro ->
                    val documents = querySnapshot?.documents
                    documents?.forEach { documentSnapshot ->
                        val utilizador = documentSnapshot.toObject(Utilizador::class.java)
                        if (utilizador != null) {
                            if (id != utilizador.id) {
                                Log.i(
                                    "fragment_contactos",
                                    "${utilizador.id} - ${utilizador.nome} - ${utilizador.email} - ${utilizador.foto}"
                                )
                                listaContactos.add(utilizador)
                            }
                        }
                    }
                    //lista de contactos atualiza o RecyclerView
                    if (listaContactos.isNotEmpty()) {
                        contactosAdapter.adicionarLista(listaContactos)
                    }
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }
}