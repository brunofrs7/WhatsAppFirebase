package com.example.whatsappfirebase.activities

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.whatsappfirebase.databinding.ActivityPerfilBinding
import com.example.whatsappfirebase.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class PerfilActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPerfilBinding.inflate(layoutInflater) }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }

    private var temPermissaoGaleria = false
    private var temPermissaoCamera = false

    private val gerirGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            binding.imagePerfil.setImageURI(uri)
            uploadImagemStorage(uri)
        } else {
            showMessage("Nenhuma imagem selecionada")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initToolbar()
        getPermissions()
        initClickEvents()
    }

    override fun onStart() {
        super.onStart()
        recuperarDadosIniciais()
    }

    private fun recuperarDadosIniciais() {
        val id = auth.currentUser?.uid

        if (id != null) {
            db.collection("utilizadores")
                .document(id)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val dados = documentSnapshot.data
                    if (dados != null) {
                        val nome = dados["nome"] as String
                        val foto = dados["foto"] as String

                        binding.editPerfilNome.setText(nome)

                        if (foto.isNotEmpty()) {
                            Picasso.get()
                                .load(foto)
                                .into(binding.imagePerfil)
                        }

                    }
                }
        }
    }

    private fun getPermissions() {
        //verificar se já tem permissão
        temPermissaoCamera = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        temPermissaoGaleria = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        //lista de permissões negadas
        val listaPermissoesNegadas = mutableListOf<String>()
        if (!temPermissaoCamera) {
            listaPermissoesNegadas.add(android.Manifest.permission.CAMERA)
        }
        if (!temPermissaoGaleria) {
            listaPermissoesNegadas.add(android.Manifest.permission.READ_MEDIA_IMAGES)
        }

        if (listaPermissoesNegadas.isNotEmpty()) {
            //solicitar múltiplas permissões
            val gerirPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissoes ->
                temPermissaoCamera =
                    permissoes[android.Manifest.permission.CAMERA] ?: temPermissaoCamera
                temPermissaoGaleria =
                    permissoes[android.Manifest.permission.READ_MEDIA_IMAGES] ?: temPermissaoGaleria
            }
            gerirPermissoes.launch(listaPermissoesNegadas.toTypedArray())
        }
    }

    private fun initClickEvents() {
        binding.fabSelecionar.setOnClickListener {
            if (temPermissaoGaleria) {
                gerirGaleria.launch("image/*")
            } else {
                showMessage("Não tem permissão para abrir galeria de imagens")
                getPermissions()
            }
        }
        binding.buttonAtualizarPerfil.setOnClickListener {
            val nome = binding.editPerfilNome.text.toString()
            if (nome.isNotEmpty()) {
                val id = auth.currentUser?.uid

                if (id != null) {
                    val dados = mapOf(
                        "nome" to nome
                    )
                    atualizarDadosPerfil(dados, id)
                }
            } else {
                showMessage("Preencher o nome antes de atualizar")
            }
        }
    }

    private fun uploadImagemStorage(uri: Uri) {
        val id = auth.currentUser?.uid

        if (id != null) {
            storage.getReference("fotos")
                .child("utilizadores")
                .child(id)
                .child("perfil.jpg")
                .putFile(uri)
                .addOnSuccessListener { task ->
                    showMessage("Sucesso no upload da imagem")
                    task.metadata?.reference?.downloadUrl
                        ?.addOnSuccessListener { url ->
                            val dados = mapOf(
                                "foto" to url.toString()
                            )
                            atualizarDadosPerfil(dados, id)
                        }
                        ?.addOnFailureListener {
                            showMessage("Erro ao obter o url da imagem")
                        }
                }
                .addOnFailureListener {
                    showMessage("Erro ao fazer upload da imagem")
                }
        }
    }

    private fun atualizarDadosPerfil(dados: Map<String, String>, id: String) {
        db.collection("utilizadores").document(id)
            .update(dados)
            .addOnSuccessListener {
                showMessage("Dados de utilizador atualizados com sucesso")
            }
            .addOnFailureListener {
                showMessage("Erro ao atualizar os dados do utilizador")
            }
    }

    private fun initToolbar() {
        val toolbar = binding.includeToolbarPerfil.toolbarPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Editar Perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}