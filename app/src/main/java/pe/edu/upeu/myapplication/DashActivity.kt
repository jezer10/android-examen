package pe.edu.upeu.myapplication

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import pe.edu.upeu.myapplication.model.UserModel
import pe.edu.upeu.myapplication.remote.APIUtils
import pe.edu.upeu.myapplication.remote.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashActivity : AppCompatActivity() {
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button
    private lateinit var btnLogOut: Button
    private lateinit var btnSaveChanges: Button
    private lateinit var tvUsername: TextView
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText

    private lateinit var etPassword: EditText
    private lateinit var ibPasswordToggle: ImageButton
    private lateinit var etConfirmPassword: EditText
    private lateinit var ibConfirmPasswordToggle: ImageButton
    var isFocusable: Boolean = false
    private lateinit var userService: UserService
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userModel: UserModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash)
        init()
        focusableAction()

        if (sharedPreferences.getInt("iduser", 0) != 0) {
            getUserData(sharedPreferences.getInt("iduser", 0))
        } else {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }

        setup()


    }

    private fun focusableAction() {
        if (!isFocusable) {
            btnEdit.text = "Editar"
            btnEdit.setTextColor(Color.WHITE)
            etFirstName.isFocusable = isFocusable
            etLastName.isFocusable = isFocusable
            etConfirmPassword.isFocusable = isFocusable
            etPassword.isFocusable = isFocusable
        } else {
            btnEdit.text = "Cancelar"
            btnEdit.setTextColor(Color.RED)
            etFirstName.isFocusableInTouchMode = isFocusable
            etLastName.isFocusableInTouchMode = isFocusable
            etConfirmPassword.isFocusableInTouchMode = isFocusable
            etPassword.isFocusableInTouchMode = isFocusable
        }


        isFocusable = !isFocusable

    }

    private fun init() {
        btnDelete = findViewById(R.id.btn_dash_delete)
        btnEdit = findViewById(R.id.btn_dash_edit)
        btnSaveChanges = findViewById(R.id.btn_dash_save_changes)

        etFirstName = findViewById(R.id.et_dash_firstname)
        etLastName = findViewById(R.id.et_dash_lastname)
        etPassword = findViewById(R.id.et_dash_password)
        etConfirmPassword = findViewById(R.id.et_dash_confirm_password)
        ibPasswordToggle = findViewById(R.id.ib_dash_password_toggle)
        ibConfirmPasswordToggle = findViewById(R.id.ib_dash_confirm_password_toggle)
        tvUsername = findViewById(R.id.tv_dash_username)
        btnLogOut = findViewById(R.id.btn_dash_logout)
        userService = APIUtils.getUserService()
        sharedPreferences = getSharedPreferences("credentials", MODE_PRIVATE)

    }

    private fun getUserData(iduser: Int) {
        userService.getUserById(iduser).enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                Log.i(ContentValues.TAG, "${response.body()?.username}")
                userModel = response.body()!!
                initData(userModel)
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                Log.i(ContentValues.TAG, "$t")

            }


        })

    }

    private fun initData(userModel: UserModel) {
        tvUsername.text = userModel.username
        etFirstName.setText(userModel.firstname)
        etLastName.setText(userModel.lastname)
    }

    private fun clearData() {
        etFirstName.setText("")
        etLastName.setText("")
        etPassword.setText("")
        etConfirmPassword.setText("")
    }

    private fun updateUser(userModel: UserModel) {
        userService.updateUser(userModel.iduser, userModel).enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.isSuccessful){
                    Toast.makeText(applicationContext, "Succesfully updated", Toast.LENGTH_SHORT).show()

                }else{
                    Log.i(ContentValues.TAG,"${response.body()} ${response.message()}")
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()

                }

            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                Log.i(ContentValues.TAG, "$t")
                Toast.makeText(applicationContext, "Algo Fallo", Toast.LENGTH_SHORT).show()

            }

        })
    }

    private fun setup() {
        btnEdit.setOnClickListener {
            focusableAction()
        }
        btnSaveChanges.setOnClickListener {
            updateUser(
                UserModel(
                    sharedPreferences.getInt("iduser", 0),
                    sharedPreferences.getString("username", "").toString(),
                    etPassword.text.toString(),
                    etFirstName.text.toString(),
                    etLastName.text.toString()
                )
            )
        }

        btnDelete.setOnClickListener {
            MaterialAlertDialogBuilder(
                this,
                R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_FullWidthButtons
            )
                .setTitle("¿Desea eliminar su cuenta?")
                .setMessage("Este cambio no podra ser revertido.")
                .setNegativeButton("No") { dialog, which ->
                    return@setNegativeButton
                }
                .setPositiveButton("Si") { dialog, which ->
                    userService.deleteUser(sharedPreferences.getInt("iduser", 0))
                        .enqueue(object : Callback<UserModel> {
                            override fun onResponse(
                                call: Call<UserModel>,
                                response: Response<UserModel>
                            ) {
                                Toast.makeText(applicationContext,"Successfully Deleted",Toast.LENGTH_SHORT).show()

                                startActivity(Intent(applicationContext,LoginActivity::class.java))
                            }

                            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                                Toast.makeText(applicationContext,"Ocurrio Un error mientras se borraba",Toast.LENGTH_SHORT).show()
                            }

                        })
                }
                .show()
        }
        btnLogOut.setOnClickListener {
            val edit = sharedPreferences.edit()
            edit.putBoolean("savedCredentials", false)
            edit.remove("iduser")
            edit.remove("username")
            edit.apply()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
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

        ibConfirmPasswordToggle.setOnClickListener {

            if (etConfirmPassword.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                etConfirmPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ibConfirmPasswordToggle.setImageResource(R.drawable.ic_baseline_visibility_off_24)

            } else {
                etConfirmPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ibConfirmPasswordToggle.setImageResource(R.drawable.ic_baseline_visibility_24)
            }
        }
    }


}