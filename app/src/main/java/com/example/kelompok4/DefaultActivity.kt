package com.example.kelompok4

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class DefaultActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "LoginActivity"
    }

    // DITAMBAHKAN: Deklarasi untuk EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText

    private lateinit var loginButton: Button
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var googleSignInButton: Button
    private lateinit var registerButton: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    // ... (fungsi checkUserSession, firebaseAuthWithGoogle, onActivityResult, signIn tetap sama) ...
    private fun checkUserSession(){
        val currentUser: FirebaseUser? = mAuth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this@DefaultActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG,"firebaseAuthWithGoogle: " + acct.id)
        val credential: AuthCredential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user: FirebaseUser? = mAuth.currentUser
                    Toast.makeText(this, "Authentication Successful.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
                progressBar.visibility = View.GONE
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signIn() {
        progressBar.visibility = View.VISIBLE

        // TAMBAHKAN BARIS INI untuk sign out dari Google Client
        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            // Setelah proses sign out lokal selesai, baru mulai intent untuk login
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    // DITAMBAHKAN: Fungsi untuk login dengan email dan password
    private fun loginWithEmailPassword() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan Password tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    // Sign in success, update UI
                    Log.d(TAG, "signInWithEmail:success")
                    checkUserSession() // Panggil checkUserSession untuk pindah halaman
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Autentikasi gagal: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_default)
        mAuth = FirebaseAuth.getInstance()

        // Inisialisasi semua view
        googleSignInButton = findViewById(R.id.googleSignInButton)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        progressBar = findViewById(R.id.progressBar)
        emailInput = findViewById(R.id.emailInput) // DITAMBAHKAN
        passwordInput = findViewById(R.id.passwordInput) // DITAMBAHKAN


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInButton.setOnClickListener {
            signIn()
        }

        loginButton.setOnClickListener {
            loginWithEmailPassword()
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this@DefaultActivity, RegisterActivity::class.java))
        }

        checkUserSession()
    }
}