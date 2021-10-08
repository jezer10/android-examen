package pe.edu.upeu.myapplication.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object {

        fun getClient(url: String): Retrofit {
              var retrofit: Retrofit = Retrofit.Builder().baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create()).build()
            return retrofit
        }
    }
}