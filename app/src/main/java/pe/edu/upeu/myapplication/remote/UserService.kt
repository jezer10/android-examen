package pe.edu.upeu.myapplication.remote

import pe.edu.upeu.myapplication.model.UserModel
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @GET("users")
    fun getUsers():Call<List<UserModel>>

    @GET("users/{id}")
    fun getUserById(@Path("id") id:Int) :Call<UserModel>

    @POST("users")
    fun createUser(@Body userModel: UserModel):Call<UserModel>

    @POST("users/auth")
    fun authUser(@Body userModel: UserModel):Call<UserModel>


    @PUT("users/{id}")
    fun updateUser(@Path("id") id:Int,@Body userModel: UserModel):Call<UserModel>

    @DELETE("users/{id}")
    fun deleteUser(@Path("id") id:Int):Call<UserModel>

}