package com.abencrauz.yates

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abencrauz.yates.models.User

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    private var user:Map<String,Any> = mutableMapOf()
    private val db = Firebase.firestore
    private val userRef = db.collection("users")
    private val RC_SIGN_IN = 1
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var btnLog: MaterialButton
    private lateinit var btnReg: MaterialButton
    private lateinit var btnGoogle: SignInButton
    private lateinit var etUser: TextInputEditText
    private lateinit var etPass: TextInputEditText
    private lateinit var pb: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1071516260384-7f3t418l19li8oi95e02oel09lpa9num.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        //mGoogleSignInClient.signOut()
        val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", "")
        Log.d("Logged in", userId)
        if(userId != ""){
            val intent = Intent(this, RestaurantActivity::class.java)
            startActivity(intent)
        }
        initComponent()


        btnGoogle.setOnClickListener(View.OnClickListener {
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if(account!= null){

                val query = userRef.whereEqualTo("email",account.email).limit(1)
                query.get()
                    .addOnSuccessListener { documents->
                        for(document in documents){
                            val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("user_id", document.id)
                            editor.commit()

                            val intent = Intent(this, RestaurantActivity::class.java)
                            startActivity(intent)
                        }

                    }
            }else{
                val intent = mGoogleSignInClient.signInIntent
                startActivityForResult(intent, RC_SIGN_IN)
            }

        })

        btnReg.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        })

        btnLog.setOnClickListener(View.OnClickListener {
            if(etUser.text.toString() == ""){
                val msg = "Username "+getString(R.string.empty_error)
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }else if(etPass.text.toString() == ""){
                val msg = "Password "+getString(R.string.empty_error)
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }else{
                toggleLoad(true)
                searchUser(etUser.text.toString(), etPass.text.toString())

            }

        })
    }

    fun initComponent(){
        btnGoogle = findViewById(R.id.btn_google)
        btnLog = findViewById(R.id.btn_log)
        btnReg = findViewById(R.id.btn_reg)
        etUser = findViewById(R.id.et_username)
        etPass = findViewById(R.id.et_password)
        pb = findViewById(R.id.pb)

        pb.visibility = View.GONE
    }

    fun searchUser(username:String, password:String){

        var query = userRef.whereEqualTo("username",username).whereEqualTo("password",password)
        query.get()
            .addOnSuccessListener { documents->
                if(documents.size()>0){
                    for(document in documents){
                        user = document.data
                        val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("user_id", document.id)
                        editor.commit()
                        var message = getString(R.string.welcome)+", "+ user["fullname"].toString()
                        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
                        val intent = Intent(this, RestaurantActivity::class.java)
                        startActivity(intent)
                    }
                }else{
                    Toast.makeText(this,getString(R.string.auth_error), Toast.LENGTH_LONG).show()
                }

                toggleLoad(false)
            }
    }

    fun toggleLoad(loading: Boolean){
        if(loading){
            pb.visibility = View.VISIBLE
            btnLog.text = getText(R.string.loading)
            btnLog.isEnabled = false
        }else{
            pb.visibility = View.GONE
            btnLog.text = getText(R.string.login)
            btnLog.isEnabled = true
        }
    }
    fun handleSignInResult(task: Task<GoogleSignInAccount>){
        Log.d("Debug","In handle")
        try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)

            if (account != null) {
                Log.d("Google Account: ", account.displayName.toString())
                handleAccount(account)
            }else{
                Log.d("Google Account: ", "none")
            }

        }catch (e: ApiException){
            Log.d("Sign in failed", e.statusCode.toString())
        }

    }

    fun handleAccount(account: GoogleSignInAccount){
        val query = userRef.whereEqualTo("email",account.email).limit(1)
        query.get()
            .addOnSuccessListener { documents->
                if(documents.size()>0){
                    for(document in documents){
                        val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("user_id", document.id)
                        editor.commit()
                        val intent = Intent(this, RestaurantActivity::class.java)
                        startActivity(intent)
                    }
                }else{
                    registerUser(account)
                }

            }
    }

    fun registerUser(account:GoogleSignInAccount){
        toggleLoad(true)

        val data = User(
            fullname =  account.displayName,
            username = account.displayName,
            email = account.email,
            password = "",
            description = "",
            image = "")

        userRef.add(data)
            .addOnSuccessListener { documentReference ->
                Log.d("Registered user with ID", documentReference.id)
                val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("user_id", documentReference.id)
                editor.commit()
                Toast.makeText(this, getString(R.string.register_succes), Toast.LENGTH_LONG).show()
                toggleLoad(false)
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            Log.d("Debug","Sign in done")
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
        }
    }
}
