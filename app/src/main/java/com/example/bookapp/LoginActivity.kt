package com.example.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    // create firebase authentication object
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // initialize the firebase auth object
        auth = FirebaseAuth.getInstance()

        // if the user is signed in send them to the main screen
        val user = auth.currentUser
        if(user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        title="Login"
        pb.visibility = View.INVISIBLE

        // set up listeners for the two buttons
        btn_login.setOnClickListener { login() }
        tv_register.setOnClickListener { switch() }
    }

    // handles the login functionality
    private fun login() {
        val email = et_email.text.toString()
        val password = et_password.text.toString()
        pb.visibility = View.VISIBLE

        if(email.isEmpty()) {
            et_email.error = "Email address is required"
            pb.visibility = View.INVISIBLE
            return
        }

        if(password.isEmpty()) {
            et_password.error = "Password is required"
            pb.visibility = View.INVISIBLE
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_LONG).show()
        }
    }

    // switches to the registration activity
    private fun switch() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}