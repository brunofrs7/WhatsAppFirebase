package com.example.whatsappfirebase.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.whatsappfirebase.databinding.ItemMensagensDestinatarioBinding
import com.example.whatsappfirebase.databinding.ItemMensagensRemetenteBinding
import com.example.whatsappfirebase.model.Mensagem
import com.example.whatsappfirebase.utils.Constantes
import com.google.firebase.auth.FirebaseAuth

class MensagensAdapter : Adapter<ViewHolder>() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private var listaMensagens = emptyList<Mensagem>()
    fun adicionarLista(lista: List<Mensagem>) {
        listaMensagens = lista
        notifyDataSetChanged()
    }

    class MensagensRemetenteViewHolder(private val binding: ItemMensagensRemetenteBinding) :
        ViewHolder(binding.root) {
        fun bind(mensagem: Mensagem) {
            binding.textMensagemRemetente.text = mensagem.mensagem
        }

        companion object {
            fun inflateLayout(
                inflater: LayoutInflater,
                parent: ViewGroup
            ): MensagensRemetenteViewHolder {
                val itemView = ItemMensagensRemetenteBinding.inflate(inflater, parent, false)
                return MensagensRemetenteViewHolder(itemView)
            }
        }
    }

    class MensagensDestinatarioViewHolder(private val binding: ItemMensagensDestinatarioBinding) :
        ViewHolder(binding.root) {
        fun bind(mensagem: Mensagem) {
            binding.textMensagemDestinatario.text = mensagem.mensagem
        }

        companion object {
            fun inflateLayout(
                inflater: LayoutInflater,
                parent: ViewGroup
            ): MensagensDestinatarioViewHolder {
                val itemView = ItemMensagensDestinatarioBinding.inflate(inflater, parent, false)
                return MensagensDestinatarioViewHolder(itemView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val mensagem = listaMensagens[position]
        val id = auth.currentUser?.uid.toString()
        return if (id == mensagem.id) {
            Constantes.TIPO_REMETENTE
        } else {
            Constantes.TIPO_DESTINATARIO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        if (viewType == Constantes.TIPO_REMETENTE) {
            return MensagensRemetenteViewHolder.inflateLayout(inflater, parent)
        }
        return MensagensDestinatarioViewHolder.inflateLayout(inflater, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mensagem = listaMensagens[position]
        when (holder) {
            is MensagensRemetenteViewHolder -> holder.bind(mensagem)
            is MensagensDestinatarioViewHolder -> holder.bind(mensagem)
        }
    }

    override fun getItemCount(): Int {
        return listaMensagens.size
    }
}