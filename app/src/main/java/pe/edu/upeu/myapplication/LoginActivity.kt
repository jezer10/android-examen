package pe.edu.upeu.myapplication

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import pe.edu.upeu.myapplication.model.UserModel
import pe.edu.upeu.myapplication.remote.APIUtils
import pe.edu.upeu.myapplication.remote.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var btnRegister: Button
    lateinit var btnLogin: Button
    lateinit var etUsername: EditText
    lateinit var etPassword: EditText
    lateinit var cbSaveCredentials: CheckBox
    lateinit var sharedPreferences: SharedPreferences
    lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnRegister = findViewById(R.id.btn_login_register)
        btnLogin = findViewById(R.id.btn_login_log)
        etUsername = findViewById(R.id.et_login_username)
        etPassword = findViewById(R.id.et_login_password)
        cbSaveCredentials = findViewById(R.id.cb_login_save_credentials)
        sharedPreferences = getSharedPreferences("credentials", MODE_PRIVATE)
        userService = APIUtils.getUserService()

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            authUser(etUsername.text.toString(), etPassword.text.toString())
        }
    }

    private fun authUser(username: String, password: String) {
        if (password.isBlank() || username.isBlank()) {
            Toast.makeText(this, "Empty username or password", Toast.LENGTH_SHORT).show()
        }

        userService.authUser(UserModel(0,username, password, "", ""))
            .enqueue(object : Callback<UserModel> {
                override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                    Log.i(ContentValues.TAG, "${response.body()?.username}")
                    if (cbSaveCredentials.isChecked) {
                        val edit = sharedPreferences.edit()
                        response.body()?.iduser?.let { edit.putInt("iduser", it) }
                        edit.putString("username", response.body()?.username)
                        edit.apply()

                    }
                    startActivity(Intent(applicationContext,DashActivity::class.java))
                }

                override fun onFailure(call: Call<UserModel>, t: Throwable) {
                    Log.i(ContentValues.TAG, "$t")
                }

            })


    }
}