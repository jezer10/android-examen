package pe.edu.upeu.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var intent:Intent= Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}