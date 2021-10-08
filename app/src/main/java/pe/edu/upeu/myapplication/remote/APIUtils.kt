package pe.edu.upeu.myapplication.remote

class APIUtils {
    constructor(){

    }


    companion object{
        const val API_URL = "https://app-an.herokuapp.com/"

        fun getUserService(): UserService {
            return RetrofitClient.getClient(API_URL).create(UserService::class.java)

        }
    }
}