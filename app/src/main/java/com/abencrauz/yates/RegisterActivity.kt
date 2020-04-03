package com.abencrauz.yates

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.abencrauz.yates.models.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private val userRef = db.collection("users")
    private lateinit var etFullname:TextInputEditText
    private lateinit var etUsername:TextInputEditText
    private lateinit var etEmail:TextInputEditText
    private lateinit var etPassword:TextInputEditText
    private lateinit var etDescription:TextInputEditText
    private lateinit var btnReg: MaterialButton
    private lateinit var pb: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initComponent()

        btnReg.setOnClickListener(View.OnClickListener {
            toggleLoading(true)
            val fullname = etFullname.text.toString()
            if (fullname.length < 3) {
                Log.d("Validation fail", "fullname")
                Toast.makeText(this, getString(R.string.fullname_short), Toast.LENGTH_LONG).show()
                toggleLoading(false)
                return@OnClickListener
            }

            val email = etEmail.text.toString()
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Log.d("Validation fail", "email")
                Toast.makeText(this, getString(R.string.email_invalid),Toast.LENGTH_LONG).show()
                toggleLoading(false)
                return@OnClickListener
            }

//            val password = etPassword.text.toString()
//            if(!isAlphaNumeric(password)){
//                Log.d("Validation fail", "password")
//                Toast.makeText(this, getString(R.string.password_invalid), Toast.LENGTH_LONG).show()
//                toggleLoading(false)
//                return@OnClickListener
//            }

            val username = etUsername.text.toString()
            val query = userRef.whereEqualTo("username",username)
            query.get()
                .addOnSuccessListener { documents->
                    ///Log.d("Result", "${documents.size()} data found regarding ${username}")
                    if(documents.size() > 0){
                        Toast.makeText(this, getString(R.string.username_exist), Toast.LENGTH_LONG).show()
                        toggleLoading(false)
                    }else{
                        toggleLoading(true)
                        registerUser()
                    }

                }

        })
    }

    fun initComponent(){
        etFullname = findViewById(R.id.et_fullname)
        etUsername = findViewById(R.id.et_username)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        etDescription = findViewById(R.id.et_description)

        btnReg = findViewById(R.id.btn_reg)
        pb = findViewById(R.id.pb)

        pb.visibility = View.GONE
    }

    fun toggleLoading(loading: Boolean){
        if(loading){
            pb.visibility = View.VISIBLE
            btnReg.text = getText(R.string.loading)
            btnReg.isEnabled = false
        }else{
            pb.visibility = View.GONE
            btnReg.text = getText(R.string.register)
            btnReg.isEnabled = true
        }

    }

    fun isAlphaNumeric(ss: String): Boolean {
        var c1=0
        var c2=0
        val s = ss.toCharArray()
        for(i in 1..ss.length){
            if(s[i] in 'A'..'Z' || s[i] in 'a'..'z'){
                c1++
            }else if(s[i] in '0'..'9'){
                c2++
            }
        }

        return c1>0 && c2>0
    }

    fun registerUser(){
        val data = User(
            fullname = etFullname.text.toString(),
            username = etUsername.text.toString(),
            email = etEmail.text.toString(),
            password = etPassword.text.toString(),
            description = etDescription.text.toString(),
            image = "")
        userRef.add(data)
            .addOnSuccessListener { documentReference ->
                Log.d("Registered user with ID", documentReference.id)
                val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("user_id", documentReference.id)
                editor.commit()
                Toast.makeText(this, getString(R.string.register_succes), Toast.LENGTH_LONG).show()
                toggleLoading(false)
            }


    }
}
