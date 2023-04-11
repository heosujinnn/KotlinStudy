package com.soojin.jinstargram

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.soojin.jinstargram.databinding.ActivityLoginBinding
import com.soojin.jinstargram.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        binding.emailLoginBtn.setOnClickListener {
            signinAndSignup()
        }
        binding.googleSignInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        var gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient=GoogleSignIn.getClient(this,gso)


        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException) {
                    Toast.makeText(this, "Google sign in failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun firebaseAuthWithGoogle(account:GoogleSignInAccount?){
        var credential=GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //로그인 성공
                    moveMainPage(task.result.user)
                } else {  //실패
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }
            }

    }


    fun signinAndSignup() {
        auth?.createUserWithEmailAndPassword(binding.emailEt.text.toString(), binding.passwordEt.text.toString())?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                moveMainPage(task.result.user)
            } else if (task.exception?.message.isNullOrBlank()) {
                //에러메세지
                Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
            } else {
                signinEmail()

            }
        }
    }

    fun signinEmail() {
        auth?.signInWithEmailAndPassword(binding.emailEt.text.toString(), binding.passwordEt.text.toString())
            ?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //로그인 성공
                moveMainPage(task.result.user)
            } else {  //실패
                Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
            }
        }
    }
    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

}