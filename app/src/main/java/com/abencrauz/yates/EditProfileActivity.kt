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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class EditProfileActivity : AppCompatActivity() {

    val db = Firebase.firestore
    val storage = Firebase.storage

    var editTextList:MutableList<TextInputEditText> = mutableListOf()
    var textInputLayoutList:MutableList<TextInputLayout> = mutableListOf()

    var users = HomeActivity.users

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        initializeTextInputEditText()
        initializeTextInputLayout()

        val updateProfileBtn = findViewById<Button>(R.id.update_profile_btn)
        val cancelUpdateBtn = findViewById<Button>(R.id.cancel_update_btn)

        setTextInputEditText()
        textChangeListener(updateProfileBtn)
        buttonListener(cancelUpdateBtn, updateProfileBtn)
    }

    private fun initializeTextInputEditText(){
        editTextList.add(findViewById(R.id.fullname_et))
        editTextList.add(findViewById(R.id.username_et))
        editTextList.add(findViewById(R.id.password_et))
        editTextList.add(findViewById(R.id.email_et))
        editTextList.add(findViewById(R.id.description_et))
    }

    private fun initializeTextInputLayout(){
        textInputLayoutList.add(findViewById(R.id.fullname_tl))
        textInputLayoutList.add(findViewById(R.id.username_tl))
        textInputLayoutList.add(findViewById(R.id.password_tl))
        textInputLayoutList.add(findViewById(R.id.email_tl))
    }

    private fun textChangeListener(updateProfileBtn:Button){
        editTextList[0].addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                updateProfileBtn.isEnabled = true
                if(!validateFullname())
                    textInputLayoutList[0].error = "Fullname length min 3 characters"
                else
                    textInputLayoutList[0].error = null
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        editTextList[1].addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                updateProfileBtn.isEnabled = true
                if(!validateUsername())
                    textInputLayoutList[1].error = "Username has already been taken"
                else
                    textInputLayoutList[1].error = null
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        editTextList[2].addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                updateProfileBtn.isEnabled = true
                if(!validatePassword())
                    textInputLayoutList[2].error = "Password must be alphanumeric"
                else
                    textInputLayoutList[2].error = null
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        editTextList[3].addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                updateProfileBtn.isEnabled = true
                if(!validateEmail())
                    textInputLayoutList[3].error = "Email must use an email format"
                else
                    textInputLayoutList[3].error = null
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
            val toProfileActivity = Intent(this, ProfileActivity::class.java)
            toProfileActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(toProfileActivity)
        }
        updateProfileBtn.setOnClickListener(){
            if(validate()){
                updateProfile(updateProfileBtn)
            }else{
                Toast.makeText(this, "Failed Update Profile", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setTextInputEditText(){
        editTextList[0].setText(users.fullname)
        editTextList[1].setText(users.username)
        editTextList[2].setText(users.password)
        editTextList[3].setText(users.email)
        editTextList[4].setText(users.description)
    }

    private fun setUsers(){
        users.fullname = editTextList[0].text.toString()
        users.username = editTextList[1].text.toString()
        users.password = editTextList[2].text.toString()
        users.email = editTextList[3].text.toString()
        users.description = editTextList[4].text.toString()
    }

    private fun updateProfile(updateProfileBtn: Button){
        setUsers()
        val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", "")
        val userRef = db.collection("users").document( userId.toString() )
        userRef.update(
            mapOf(
                "fullname" to users.fullname,
                "username" to users.username,
                "password" to users.password,
                "email" to users.email,
                "description" to users.description
            )
        ).addOnSuccessListener {
            Toast.makeText(this, "Success Update Profile", Toast.LENGTH_LONG).show()
            updateProfileBtn.isEnabled= false
        }.addOnFailureListener {
            Toast.makeText(this, "Failed Update Profile", Toast.LENGTH_LONG).show()
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
