package com.abencrauz.yates

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import com.abencrauz.yates.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.regex.Pattern

class AccountActivity : AppCompatActivity() {

    val db = Firebase.firestore
    val storage = Firebase.storage
    var editTextList:MutableList<TextInputEditText> = mutableListOf()

    var users = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        editTextList.add(findViewById(R.id.fullname_et))
        editTextList.add(findViewById(R.id.username_et))
        editTextList.add(findViewById(R.id.password_et))
        editTextList.add(findViewById(R.id.email_et))
        editTextList.add(findViewById(R.id.description_et))
        val updateProfileBtn = findViewById<Button>(R.id.update_profile_btn)
        val cancelUpdateBtn = findViewById<Button>(R.id.cancel_update_btn)

        val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
        var editor = sharedPreferences.edit()
        editor.putString("user_id", "pqCRQXmE9NFCv9jdnegt")
        editor.commit()

        getUserAccountData(updateProfileBtn)
        buttonListener(cancelUpdateBtn, updateProfileBtn)
    }

    private fun textChangeListener(updateProfileBtn:Button){
        editTextList[0].addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                updateProfileBtn.isEnabled = true
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        editTextList[1].addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                updateProfileBtn.isEnabled = true
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        editTextList[2].addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                updateProfileBtn.isEnabled = true
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        editTextList[3].addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                updateProfileBtn.isEnabled = true
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        editTextList[4].addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                updateProfileBtn.isEnabled = true
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun buttonListener(cancelUpdateBtn:Button, updateProfileBtn:Button){
        cancelUpdateBtn.setOnClickListener(){
            val toHomeActivity = Intent(this, HomeActivity::class.java)
            toHomeActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(toHomeActivity)
        }
        updateProfileBtn.setOnClickListener(){
            if(validate()){
                updateProfile(updateProfileBtn)
            }else{
                Toast.makeText(this, "Failed Update Profile", Toast.LENGTH_LONG)
            }
        }
    }

    private fun getUserAccountData(updateProfileBtn: Button){
//      Sharepreference usersDocumentId
        val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", "")
        val userRef = db.collection("users").document( userId.toString() )
        userRef.get()
            .addOnSuccessListener { document ->
//                Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                users.fullname = document.data?.get("fullname").toString()
                users.username = document.data?.get("username").toString()
                users.password = document.data?.get("password").toString()
                users.email = document.data?.get("email").toString()
                users.description = document.data?.get("description").toString()
                setTextInputEditText()
                textChangeListener(updateProfileBtn)
            }
    }

    private fun setTextInputEditText(){
        editTextList[0].setText(users.fullname)
        editTextList[1].setText(users.username)
        editTextList[2].setText(users.password)
        editTextList[3].setText(users.email)
        editTextList[4].setText(users.description)
    }

    private fun updateProfile(updateProfileBtn: Button){
        val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", "")
        val userRef = db.collection("users").document( userId.toString() )
        userRef.update(
            mapOf(
                "fullname" to editTextList[0].text.toString(),
                "username" to editTextList[1].text.toString(),
                "password" to editTextList[2].text.toString(),
                "email" to editTextList[3].text.toString(),
                "description" to editTextList[4].text.toString()
            )
        ).addOnSuccessListener {
            Toast.makeText(this, "Success Update Profile", Toast.LENGTH_LONG)
            updateProfileBtn.isEnabled= false
        }.addOnFailureListener {
            Toast.makeText(this, "Failed Update Profile", Toast.LENGTH_LONG)
        }
    }

    private fun validateFullname():Boolean{
        return editTextList[0].text.toString().length >= 3
    }

    private fun validateUsername():Boolean{
        val username = editTextList[1].text.toString()
        var different = true
        if(username == users.username)
            return true
        else{
            val userRef = db.collection("users")
            var query = userRef.whereEqualTo("username", username)
            query.get()
                .addOnSuccessListener { documents ->
                    if(documents != null)
                        different = false
                }
        }
        return different
    }

    private fun validatePassword():Boolean{
        var symbol = 0
        var digit = 0
        var alpha = 0
        val password = editTextList[2].text.toString()
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

    private fun validateEmail():Boolean{
        val email = editTextList[3].text.toString()
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validate():Boolean{
        if(!validateFullname())
            return false
        else if(!validateUsername())
            return false
        else if(!validatePassword())
            return false
        else if(!validateEmail())
            return false

        return true
    }
}
