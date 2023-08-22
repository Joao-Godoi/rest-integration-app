package com.example.viacepintegration

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.viacepintegration.databinding.ActivityMainBinding
import com.example.viacepintegration.http.service.ApiService
import com.example.viacepintegration.http.service.ViaCepResponse
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        findViewById<ImageButton>(R.id.searchCEP).setOnClickListener {
            val cep = binding.cep.unMasked
            if (cep.isEmpty()) {
                message(it, "Preencha com um CEP válido!")
            } else if (cep.length != 8) {
                message(it, "Preencha com um CEP válido!")
            } else {
                val retrofit = Retrofit.Builder().baseUrl("https://viacep.com.br/ws/")
                    .addConverterFactory(GsonConverterFactory.create()).build()
                apiService = retrofit.create(ApiService::class.java)

                CoroutineScope(Dispatchers.Main).launch {
                    val addressInfo = apiService.getViaCepResponse(cep)
                    setAddressInfo(addressInfo)
                }
            }
        }

        findViewById<Button>(R.id.btnRegister).setOnClickListener {

            val name = findViewById<EditText>(R.id.name).text.toString()
            val bornDate = binding.bornDate.unMasked
            val email = findViewById<EditText>(R.id.email).text.toString()
            val phone = binding.phone.unMasked
            val street = findViewById<EditText>(R.id.street).text.toString()
            val neighborhood = findViewById<EditText>(R.id.neighborhood).text.toString()
            val city = findViewById<EditText>(R.id.city).text.toString()
            val state = findViewById<EditText>(R.id.state).text.toString()

            when {
                name.isEmpty() -> {
                    message(it, "Preencha seu nome!")
                }

                bornDate.isEmpty() -> {
                    message(it, "Preencha sua data de nascimento!")
                }

                email.isEmpty() -> {
                    message(it, "Preencha seu email!")
                }

                !email.contains("@") -> {
                    message(it, "Email inválido!")
                }

                phone.isEmpty() -> {
                    message(it, "Preencha seu telefone!")
                }

                phone.length < 11 -> {
                    message(it, "Telefone inválido!")
                }

                street.isEmpty() -> {
                    message(it, "Preencha sua rua!")
                }

                neighborhood.isEmpty() -> {
                    message(it, "Preencha sua bairro!")
                }

                city.isEmpty() -> {
                    message(it, "Preencha sua cidade!")
                }

                state.isEmpty() -> {
                    message(it, "Preencha seu estado!")
                }

                else -> {
                    navegateToFinish()
                }
            }
        }
    }

    private fun message(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor("#FF0000"))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }

    private fun navegateToFinish() {
        val intent = Intent(this, Finish::class.java)
        startActivity(intent)
    }

    private fun setAddressInfo(addressInfo: ViaCepResponse) {
        findViewById<EditText>(R.id.cep).setText(addressInfo.cep)
        findViewById<EditText>(R.id.street).setText(addressInfo.logradouro)
        findViewById<EditText>(R.id.neighborhood).setText(addressInfo.bairro)
        findViewById<EditText>(R.id.city).setText(addressInfo.localidade)
        findViewById<EditText>(R.id.state).setText(addressInfo.uf)
    }
}
