package com.example.whatsappfirebase.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import com.example.whatsappfirebase.R
import com.example.whatsappfirebase.adapters.ViewPagerAdapter
import com.example.whatsappfirebase.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolbar()
        initNav()
    }

    private fun initNav() {
        val tabLayout = binding.tabLayoutPrincipal
        val viewPager = binding.viewPagerPrincipal

        //Adapter
        val abas = listOf("CONVERSAS", "CONTACTOS")
        viewPager.adapter = ViewPagerAdapter(abas, supportFragmentManager, lifecycle)

        tabLayout.isTabIndicatorFullWidth = true
        TabLayoutMediator(tabLayout, viewPager) { aba, posicao ->
            aba.text = abas[posicao]
        }.attach()
    }

    private fun initToolbar() {
        val toolbar = binding.includeToolbarPrincipal.toolbarPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "WhatsApp"
        }

        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_principal, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.item_perfil -> {
                            startActivity(Intent(applicationContext, PerfilActivity::class.java))
                        }

                        R.id.item_sair -> {
                            logout()
                        }
                    }
                    return true
                }

            }
        )
    }

    private fun logout() {
        AlertDialog.Builder(this)
            .setTitle("Sair")
            .setMessage("Pretende sair?")
            .setNegativeButton("Não") { dialog, posicao -> }
            .setPositiveButton("Sim") { dialog, posicao ->
                auth.signOut()
                finish()
            }
            .create()
            .show()

    }
}