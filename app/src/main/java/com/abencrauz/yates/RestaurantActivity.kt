package com.abencrauz.yates

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abencrauz.yates.adapters.RestaurantAdapter
import com.abencrauz.yates.adapters.RestaurantFilterAdapter
import com.abencrauz.yates.models.Restaurant
import com.abencrauz.yates.models.RestaurantFilter
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class RestaurantActivity : AppCompatActivity() {
    private var db = Firebase.firestore
    private var restRef = db.collection("restaurants")
    private lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var rvRestaurant: RecyclerView
    private lateinit var restaurants : Vector<Restaurant>

    private lateinit var rType: Vector<RestaurantFilter>
    private lateinit var rMeal: Vector<RestaurantFilter>

    private lateinit var rTypeAdapter: RestaurantFilterAdapter
    private lateinit var rMealAdapter: RestaurantFilterAdapter

    private lateinit var rvType: RecyclerView
    private lateinit var rvMeal: RecyclerView

    private lateinit var pb: ProgressBar

    private lateinit var btnAdd: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        btnAdd = findViewById(R.id.btn_add)
        restaurantAdapter = RestaurantAdapter(this)
        rvRestaurant = findViewById(R.id.rv_restaurant)
        rvRestaurant.apply {
            layoutManager = GridLayoutManager(this@RestaurantActivity,1) as RecyclerView.LayoutManager?
            adapter = restaurantAdapter
        }
        rvRestaurant.isNestedScrollingEnabled = false
        restaurants = Vector()
        rType = Vector()
        rMeal = Vector()

        pb = findViewById(R.id.pb)
        toggleLoad(true)
        initFilters()
        initFilterAdapter()
        btnAdd.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, AddRestaurantActivity::class.java)
            startActivity(intent)
        })

    }

    override fun onStart() {
        super.onStart()
        toggleLoad(true)
        var query = restRef.limit(10)
        restaurants = Vector()
        restRef.get()
            .addOnSuccessListener { documents->
                if(documents.size()>0){
                    for(document in documents){
                        var r = Restaurant(
                            document.data["name"] as String,
                            document.data["location"] as String?,
                            document.data["image"] as String?,
                            document.data["address"] as String,
                            document.data["hours"] as String,
                            document.data["type"] as String,
                            document.data["meal"] as String
                        )
                        restaurants.add(r)
                    }
                }

                restaurantAdapter.setRestaurant(restaurants)
                restaurantAdapter.notifyDataSetChanged()
                toggleLoad(false)
            }
    }

    fun initFilters(){
        rMeal.add(RestaurantFilter("Meal","Breakfast",getDrawable(R.drawable.breakfast)))
        rMeal.add(RestaurantFilter("Meal","Brunch",getDrawable(R.drawable.brunch)))
        rMeal.add(RestaurantFilter("Meal","Lunch",getDrawable(R.drawable.lunch)))
        rMeal.add(RestaurantFilter("Meal","Dinner",getDrawable(R.drawable.dinner)))
        rMeal.add(RestaurantFilter("Meal","Tea & Coffee",getDrawable(R.drawable.tea)))
        rMeal.add(RestaurantFilter("Meal","Bars & Pubs",getDrawable(R.drawable.bar)))
        rMeal.add(RestaurantFilter("Meal","Dessert",getDrawable(R.drawable.dessert)))

        rType.add(RestaurantFilter("Type","Seafood",getDrawable(R.drawable.seafood)))
        rType.add(RestaurantFilter("Type","Steakhouse",getDrawable(R.drawable.steak)))
        rType.add(RestaurantFilter("Type","Oriental",getDrawable(R.drawable.oriental)))
        rType.add(RestaurantFilter("Type","Vegetarian",getDrawable(R.drawable.vegetarian)))
        rType.add(RestaurantFilter("Type","Finger Foods",getDrawable(R.drawable.finger_food)))
        rType.add(RestaurantFilter("Type","Others",getDrawable(R.drawable.others)))

    }

    fun initFilterAdapter(){
        rTypeAdapter = RestaurantFilterAdapter(this)
        rvType = findViewById(R.id.rv_type)

        rvType.apply{
            layoutManager = LinearLayoutManager(this@RestaurantActivity,LinearLayoutManager.HORIZONTAL,false)
            adapter = rTypeAdapter
        }
        rTypeAdapter.setFilter(rType)
        rTypeAdapter.notifyDataSetChanged()

        rMealAdapter = RestaurantFilterAdapter(this)
        rvMeal = findViewById(R.id.rv_meal)

        rvMeal.apply{
            layoutManager = LinearLayoutManager(this@RestaurantActivity,LinearLayoutManager.HORIZONTAL,false)
            adapter = rMealAdapter
        }
        rMealAdapter.setFilter(rMeal)
        rMealAdapter.notifyDataSetChanged()
    }

    fun toggleLoad(isLoading: Boolean){
        if(isLoading){
            pb.visibility = View.VISIBLE
            rvRestaurant.visibility = View.GONE
        }else{
            pb.visibility = View.GONE
            rvRestaurant.visibility = View.VISIBLE
        }
    }
}
