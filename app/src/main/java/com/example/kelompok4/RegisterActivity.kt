package com.example.kelompok4

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        FirebaseApp.initializeApp(this)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        nameEditText = findViewById(R.id.name)
        registerButton = findViewById(R.id.registerButton)
        progressBar = findViewById(R.id.progressBar)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val name = nameEditText.text.toString().trim()

            if (TextUtils.isEmpty(email)){
                emailEditText.error = "Email is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)){
                passwordEditText.error = "Password is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(name)){
                nameEditText.error = "Name is required"
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE



            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful){
                        val userId = mAuth.currentUser?.uid
                        val user = hashMapOf(
                            "name" to name,
                            "email" to email
                        )

                        userId?.let {
                            db.collection("users").document(it)
                                .set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "User registered successfully",
                                        Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, DefaultActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error: ${e.message}",
                                        Toast.LENGTH_LONG).show()
                                }
                        }
                    }else{
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG).show()
                        Log.v("errornya", task.exception?.message ?: "Unknown error")
                    }

                }



        }


    }
}