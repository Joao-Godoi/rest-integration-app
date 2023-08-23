package com.example.viacepintegration

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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

        binding.searchCEP.setOnClickListener {
            val cep = binding.cep.unMasked
            if (cep.isEmpty() || cep.length != 8) {
                showSnackBar("Preencha com um CEP válido!")
            } else {
                setupRetrofit(cep)
            }
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.name.text.toString()
            val bornDate = binding.bornDate.unMasked
            val email = binding.email.text.toString()
            val phone = binding.phone.unMasked
            val street = binding.street.text.toString()
            val number = binding.number.text.toString()
            val neighborhood = binding.neighborhood.text.toString()
            val city = binding.city.text.toString()
            val state = binding.state.text.toString()

            when {
                name.isEmpty() -> showSnackBar("Preencha seu nome!")
                bornDate.isEmpty() -> showSnackBar("Preencha sua data de nascimento!")
                email.isEmpty() -> showSnackBar("Preencha seu email!")
                !email.contains("@") -> showSnackBar("Email inválido!")
                phone.isEmpty() || phone.length < 11 -> showSnackBar("Telefone inválido!")
                street.isEmpty() -> showSnackBar("Preencha sua rua!")
                number.isEmpty() -> showSnackBar("Preencha seu numero!")
                neighborhood.isEmpty() -> showSnackBar("Preencha seu bairro!")
                city.isEmpty() -> showSnackBar("Preencha sua cidade!")
                state.isEmpty() -> showSnackBar("Preencha seu estado!")
                else -> navigateToFinish()
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).apply {
            setBackgroundTint(Color.parseColor("#FF0000"))
            setTextColor(Color.parseColor("#FFFFFF"))
            show()
        }
    }

    private fun navigateToFinish() {
        startActivity(Intent(this, Finish::class.java))
    }

    private fun setupRetrofit(cep: String) {
        val retrofit = Retrofit.Builder().baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        apiService = retrofit.create(ApiService::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            val addressInfo = apiService.getViaCepResponse(cep)
            setAddressInfo(addressInfo)
        }
    }

    private fun setAddressInfo(addressInfo: ViaCepResponse) {
        with(binding) {
            cep.setText(addressInfo.cep)
            street.setText(addressInfo.logradouro)
            neighborhood.setText(addressInfo.bairro)
            city.setText(addressInfo.localidade)
            state.setText(addressInfo.uf)
        }
    }
}
