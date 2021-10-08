package pe.edu.upeu.myapplication

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import pe.edu.upeu.myapplication.model.UserModel
import pe.edu.upeu.myapplication.remote.APIUtils
import pe.edu.upeu.myapplication.remote.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity() {
    lateinit var btnBack: Button
    lateinit var btnRegister: Button
    lateinit var ibPasswordToggle: ImageButton
    lateinit var ibConfirmPasswordToggle: ImageButton
    lateinit var etUsername: EditText
    lateinit var etFirstName: EditText

    lateinit var etLastName: EditText

    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText

    lateinit var userService: UserService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        btnBack = findViewById(R.id.btn_register_back_to_login)
        btnRegister = findViewById(R.id.btn_register)
        ibPasswordToggle = findViewById(R.id.ib_register_password_toggle)
        ibConfirmPasswordToggle = findViewById(R.id.ib_register_confirm_password_toggle)
        etUsername=findViewById(R.id.et_register_username)
        etFirstName=findViewById(R.id.et_register_firstname)
        etLastName=findViewById(R.id.et_register_lastname)
        etPassword = findViewById(R.id.et_register_password)
        etConfirmPassword = findViewById(R.id.et_register_confirm_password)
        userService=APIUtils.getUserService()

        btnBack.setOnClickListener {
            val intent: Intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        ibPasswordToggle.setOnClickListener {
            Log.i(
                ContentValues.TAG,
                "${etPassword.inputType} ${InputType.TYPE_TEXT_VARIATION_PASSWORD}"
            )

            if (etPassword.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ibPasswordToggle.setImageResource(R.drawable.ic_baseline_visibility_off_24)

            } else {
                etPassword.inputType =InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ibPasswordToggle.setImageResource(R.drawable.ic_baseline_visibility_24)
            }
        }

        ibConfirmPasswordToggle.setOnClickListener {
            Log.i(
                ContentValues.TAG,
                "${etConfirmPassword.inputType} ${InputType.TYPE_TEXT_VARIATION_PASSWORD}"
            )

            if (etConfirmPassword.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                etConfirmPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ibConfirmPasswordToggle.setImageResource(R.drawable.ic_baseline_visibility_off_24)

            } else {
                etConfirmPassword.inputType =InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ibConfirmPasswordToggle.setImageResource(R.drawable.ic_baseline_visibility_24)
            }
        }

        btnRegister.setOnClickListener{
            createUser(
                etUsername.text.toString(),
                etPassword.text.toString(),
                etConfirmPassword.text.toString(),
                etFirstName.text.toString(),
                etLastName.text.toString()
            )
        }

    }
    private fun createUser(username:String,password:String,confirmPassword:String,firstname:String,lastname:String){
        btnRegister.isEnabled=false
        if(username.isBlank()||password.isBlank()||firstname.isBlank()||lastname.isBlank()){
            Toast.makeText(this,"Empty Required Fields",Toast.LENGTH_SHORT).show()
            btnRegister.isEnabled=true

            return
        }
        if(password!=confirmPassword){
            Toast.makeText(this,"Contraseñas diferentes",Toast.LENGTH_SHORT).show()
            btnRegister.isEnabled=true
            return

        }

        var call = userService.createUser(UserModel(0,username, password, firstname, lastname))
        call.enqueue(object:Callback<UserModel>{
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                Log.w(ContentValues.TAG,"$response")
                if(!response.isSuccessful){
                    Toast.makeText(applicationContext,"Username already exist",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext,"Succesfully inserted row",Toast.LENGTH_SHORT).show()
                }
                btnRegister.isEnabled=true

            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                Log.w(ContentValues.TAG,"$t")
                Toast.makeText(applicationContext,"Something went wrong",Toast.LENGTH_SHORT).show()
                btnRegister.isEnabled=true

            }

        })


    }
}


