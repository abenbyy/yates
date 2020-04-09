package com.abencrauz.yates

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class EditProfileActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST: Int = 1
    private val db = Firebase.firestore
    private val storage = Firebase.storage.reference.child("image/")

    private lateinit var profilePicture:CircleImageView
    private lateinit var urlImageDownload:Uri
    private lateinit var updateProfileBtn:Button

    var editTextList:MutableList<TextInputEditText> = mutableListOf()
    var textInputLayoutList:MutableList<TextInputLayout> = mutableListOf()

    var users = HomeActivity.users

    lateinit var changeProfilePicture:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        initializeTextInputEditText()
        initializeTextInputLayout()

        updateProfileBtn = findViewById(R.id.update_profile_btn)
        val cancelUpdateBtn = findViewById<Button>(R.id.cancel_update_btn)

        changeProfilePicture = findViewById(R.id.change_profile_picture)
        profilePicture = findViewById(R.id.image_profile)

        setProfilePicture()
        setTextInputEditText()
        textChangeListener()
        buttonListener(cancelUpdateBtn)
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

    private fun textChangeListener(){
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

    private fun buttonListener(cancelUpdateBtn:Button){
        cancelUpdateBtn.setOnClickListener(){
            finish()
        }
        updateProfileBtn.setOnClickListener(){
            if(validate()){
                updateProfile(updateProfileBtn)
            }else{
                Toast.makeText(this, "Failed Update Profile", Toast.LENGTH_LONG).show()
            }
        }

        changeProfilePicture.setOnClickListener(){
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            val imageData = data.data!!
            val imageRef = storage.child("profile-picture/${imageData.lastPathSegment.toString()}")
            imageRef.putFile(imageData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Change Profile Picture Success", Toast.LENGTH_LONG).show()
                }.addOnFailureListener{
                    Toast.makeText(this, "Failed Change Profile Picture", Toast.LENGTH_LONG).show()
                }.addOnCompleteListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        urlImageDownload = uri
                        Picasso.get().load(uri).into(profilePicture)
                        updateProfileBtn.isEnabled = true
                    }
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

    private fun setProfilePicture(){
        val profilePicture = findViewById<CircleImageView>(R.id.image_profile)
        if(users.image != "")
            Picasso.get().load( Uri.parse(users.image) ).into(profilePicture)
    }

    private fun setUsers(){
        users.fullname = editTextList[0].text.toString()
        users.username = editTextList[1].text.toString()
        users.password = editTextList[2].text.toString()
        users.email = editTextList[3].text.toString()
        users.description = editTextList[4].text.toString()
        users.image = urlImageDownload.toString()
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
                "description" to users.description,
                "image" to users.image
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
