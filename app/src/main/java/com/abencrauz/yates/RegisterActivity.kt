package com.abencrauz.yates

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

        etFullname.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val fullname = etFullname.text.toString()
                if (fullname.length < 3) {
                    Log.d("Validation fail", "fullname")
                    etFullname.error = getString(R.string.fullname_short)
                    //Toast.makeText(this, getString(R.string.fullname_short), Toast.LENGTH_LONG).show()
                    toggleLoading(false)
                }else{
                    etFullname.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
        etEmail.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val email = etEmail.text.toString()
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Log.d("Validation fail", "email")
                    etEmail.error = getString(R.string.email_invalid)
                    //Toast.makeText(this, getString(R.string.email_invalid),Toast.LENGTH_LONG).show()
                    toggleLoading(false)
                }else{
                    etEmail.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

        etPassword.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(!validatePassword()){
                    Log.d("Validation fail", "password")
                    etPassword.error = getString(R.string.password_invalid)
                    //Toast.makeText(this, getString(R.string.password_invalid), Toast.LENGTH_LONG).show()
                    toggleLoading(false)

                }else{
                    etPassword.error = null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        btnReg.setOnClickListener(View.OnClickListener {
            toggleLoading(true)
            val fullname = etFullname.text.toString()
            if (fullname.length < 3) {
                Log.d("Validation fail", "fullname")
                etFullname.error = getString(R.string.fullname_short)
                //Toast.makeText(this, getString(R.string.fullname_short), Toast.LENGTH_LONG).show()
                toggleLoading(false)
                return@OnClickListener
            }else{
                etFullname.error = null
            }

            val email = etEmail.text.toString()
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Log.d("Validation fail", "email")
                etEmail.error = getString(R.string.email_invalid)
                //Toast.makeText(this, getString(R.string.email_invalid),Toast.LENGTH_LONG).show()
                toggleLoading(false)
                return@OnClickListener
            }else{
                etEmail.error = null
            }


            if(!validatePassword()){
                Log.d("Validation fail", "password")
                etPassword.error = getString(R.string.password_invalid)
                //Toast.makeText(this, getString(R.string.password_invalid), Toast.LENGTH_LONG).show()
                toggleLoading(false)

            }else{
                etPassword.error = null
            }

            val username = etUsername.text.toString()
            val query = userRef.whereEqualTo("username",username)
            query.get()
                .addOnSuccessListener { documents->
                    ///Log.d("Result", "${documents.size()} data found regarding ${username}")
                    if(documents.size() > 0){
                        etUsername.error = getString(R.string.username_exist)
                        //Toast.makeText(this, getString(R.string.username_exist), Toast.LENGTH_LONG).show()
                        toggleLoading(false)
                    }else{
                        etUsername.error = null
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

    private fun validatePassword():Boolean{
        var symbol = 0
        var digit = 0
        var alpha = 0
        val password = etPassword.text.toString()
        for (i in password.indices){
            if (password[i].isLetter())
                alpha++
            else if(password[i].isDigit())
                digit++
            else if(!password[i].isLetter() and !password[i].isDigit() and (password[i] != ' '))
                symbol++
        }
        return ((alpha!=0) and (digit!=0) and (symbol==0))
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
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }


    }
}
