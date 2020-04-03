package com.abencrauz.yates

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abencrauz.yates.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class HomeActivity : AppCompatActivity() {

    val db = Firebase.firestore
    val storage = Firebase.storage

    companion object{
        var users = User()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
        var editor = sharedPreferences.edit()
        editor.putString("user_id", "pqCRQXmE9NFCv9jdnegt")
        editor.commit()

        initializeBottomNavigationMenu()
        getUserAccountData()
    }

    private fun initializeBottomNavigationMenu(){
        var bottomNavigationMenu = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationMenu.selectedItemId = R.id.nav_home

        bottomNavigationMenu.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.nav_profile -> {
                val intent = Intent(this,ProfileActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
        }
        false
    }

    private fun getUserAccountData(){
        val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", "")
        val userRef = db.collection("users").document( userId.toString() )
        userRef.get()
            .addOnSuccessListener { document ->
                users.fullname = document.data?.get("fullname").toString()
                users.username = document.data?.get("username").toString()
                users.password = document.data?.get("password").toString()
                users.email = document.data?.get("email").toString()
                users.description = document.data?.get("description").toString()
            }
    }

}
