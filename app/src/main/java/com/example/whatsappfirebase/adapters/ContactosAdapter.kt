package com.example.whatsappfirebase.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.whatsappfirebase.databinding.ItemContactosBinding
import com.example.whatsappfirebase.model.Utilizador
import com.squareup.picasso.Picasso

class ContactosAdapter(private val onClick: (Utilizador) -> Unit) :
    Adapter<ContactosAdapter.ContactosViewHolder>() {

    private var listaContactos = emptyList<Utilizador>()
    fun adicionarLista(lista: List<Utilizador>) {
        listaContactos = lista
        notifyDataSetChanged()
    }

    inner class ContactosViewHolder(private val binding: ItemContactosBinding) :
        ViewHolder(binding.root) {
        fun bind(utilizador: Utilizador) {
            binding.imageContactoNome.text = utilizador.nome
            Picasso.get().load(utilizador.foto).into(binding.imageContactoFoto)

            binding.clItemContacto.setOnClickListener {
                onClick(utilizador)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactosViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemContactosBinding.inflate(inflater, parent, false)
        return ContactosViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactosViewHolder, position: Int) {
        val utilizador = listaContactos[position]
        holder.bind(utilizador)
    }

    override fun getItemCount(): Int {
        return listaContactos.size
    }

}