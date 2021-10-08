package pe.edu.upeu.myapplication

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.*
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
    lateinit var ibPasswordToggle:ImageButton
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
        ibPasswordToggle=findViewById(R.id.ib_login_password_toggle)
        sharedPreferences = getSharedPreferences("credentials", MODE_PRIVATE)
        userService = APIUtils.getUserService()

        if(sharedPreferences.getBoolean("savedCredentials",false)){
            startActivity(Intent(applicationContext,DashActivity::class.java))

        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            authUser(etUsername.text.toString(), etPassword.text.toString())
        }

        ibPasswordToggle.setOnClickListener {

            if (etPassword.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ibPasswordToggle.setImageResource(R.drawable.ic_baseline_visibility_off_24)

            } else {
                etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ibPasswordToggle.setImageResource(R.drawable.ic_baseline_visibility_24)
            }
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
                    if(response.isSuccessful){
                        val edit = sharedPreferences.edit()

                        if (cbSaveCredentials.isChecked) {
                            response.body()?.iduser?.let { edit.putInt("iduser", it) }
                            edit.putString("username", response.body()?.username)
                            edit.putBoolean("savedCredentials",true)
                            edit.apply()

                        }else{
                            edit.putBoolean("savedCredentials",false)
                            response.body()?.iduser?.let { edit.putInt("iduser", it) }
                            edit.apply()
                        }


                        startActivity(Intent(applicationContext,DashActivity::class.java))
                    }else{
                        Toast.makeText(applicationContext,"Invalid Username Or Password",Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onFailure(call: Call<UserModel>, t: Throwable) {
                    Log.i(ContentValues.TAG, "$t")
                }

            })


    }
}