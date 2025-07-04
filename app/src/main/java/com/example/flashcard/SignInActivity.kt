package com.example.flashcard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flashcard.backup.BackupManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class SignInActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Always prompt the Google Sign-In screen
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    // Store userId in AppData (for consistency)
                    getSharedPreferences("AppData", MODE_PRIVATE)
                        .edit().putString("userId", account.id).apply()

                    val userId = account.id
                    UserSession.setCurrentUserId(this, userId.toString())

                    val db = AppDatabase.getInstance(this)

                        // Perform restore
                    BackupManager.restoreFromFirebase(this, db, userId!!) {
                        val intent = Intent(this, Stats::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }



                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Google Sign-In failed (no account).", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: ApiException) {
                Log.e("SignInActivity", "Google sign in failed: ${e.statusCode}")
                Toast.makeText(this, "Google Sign-In error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}