package com.abencrauz.yates

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.abencrauz.yates.models.UserPost
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.util.*

class AddPostActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private var storageRef = Firebase.storage.reference.child("post picture/")
    private var db = Firebase.firestore

    private var clickOnce = 0

    private lateinit var shareBtn:Button
    private lateinit var backBtn:ImageView

    private lateinit var descriptionET:TextInputEditText
    private lateinit var imagePost:ImageView

    private lateinit var progressBar:ProgressBar

    private lateinit var urlImageDownload:Uri
    private lateinit var uuid:String
    private lateinit var locationId:String

    private lateinit var locationAc:AutoCompleteTextView
    private lateinit var dropDownBtn:ImageView
    private lateinit var loadingImage:ProgressBar

    companion object{
        lateinit var addPostActivity: AddPostActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        addPostActivity = this

        initialize()
        initDropDown()
        getPostPicture()
        btnSetOnClickListener()
    }

    private fun getPostPicture(){
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageData = data.data!!
            val imageRef = storageRef.child(uuid)
            imageRef.putFile(imageData)
                .addOnSuccessListener {
                    Log.e("Success Image", uuid)
                }.addOnFailureListener {
                    Log.e("Fail Image", uuid)
                }.addOnCompleteListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        urlImageDownload = uri
                        Picasso.get().load(uri).resize(100,100).centerCrop().into(imagePost, object: com.squareup.picasso.Callback{
                            override fun onSuccess() {
                                imagePost.visibility = View.VISIBLE
                                loadingImage.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                            }
                        })
                    }
                }
        } else {
            addPostActivity.intentToHome()
        }
    }

    private fun initialize(){
        shareBtn = findViewById(R.id.share_btn)
        backBtn = findViewById(R.id.back_btn)
        descriptionET = findViewById(R.id.description_et)
        imagePost = findViewById(R.id.image_post)
        progressBar = findViewById(R.id.progress_bar)
        loadingImage = findViewById(R.id.loading_image)
        imagePost.visibility = View.GONE
        uuid = UUID.randomUUID().toString()
        locationId = ""
    }

    private fun initDropDown(){
        locationAc = findViewById(R.id.location_ac)

        val arrayAdapter = ArrayAdapter(this, R.layout.location_list_layout, HomeActivity.listCityName)
        locationAc.setAdapter(arrayAdapter)

        dropDownBtn = findViewById(R.id.dropdown_btn)

        dropDownBtn.setOnClickListener {
            locationAc.showDropDown()
        }
    }

    private fun btnSetOnClickListener(){
        backBtn.setOnClickListener(){
            val imageRef = storageRef.child(uuid)
            imageRef.delete().addOnSuccessListener {
                intentToHome()
            }
        }
        shareBtn.setOnClickListener(){
            val idx = HomeActivity.listCityName.indexOf(locationAc.text.toString())
            progressBar.visibility = View.VISIBLE
            clickOnce++
            if(clickOnce == 1) {
                val sharedPreferences = getSharedPreferences("users", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getString("user_id", "")
                if(idx!=-1)
                    locationId = HomeActivity.listCity[idx].cityId
                else
                    locationId = ""
                val userPost = UserPost(
                    userId!!,
                    urlImageDownload.toString(),
                    descriptionET.text.toString(),
                    locationId,
                    Timestamp.now()
                )
                db.collection("user-post").add(userPost)
                    .addOnSuccessListener {
                        progressBar.visibility = View.INVISIBLE
                        Toast.makeText(this, "Post has been successfully added", Toast.LENGTH_LONG).show()
                        HomeActivity.addNewPost = true
                        intentToHome()
                    }
            }
        }
    }

    private fun intentToHome(){
        finish()
    }

    override fun onStop() {
        super.onStop()
        if(clickOnce != 1) {
            val imageRef = storageRef.child(uuid)
            imageRef.delete()
        }
    }

}
