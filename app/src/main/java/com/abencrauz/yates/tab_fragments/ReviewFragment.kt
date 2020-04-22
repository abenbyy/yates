package com.abencrauz.yates.tab_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.abencrauz.yates.R
import com.abencrauz.yates.adapter.ReviewRecycleViewAdapter
import com.abencrauz.yates.models.UserReview

class ReviewFragment() : Fragment() {

    var listReview:MutableList<UserReview> = mutableListOf()

    lateinit var reviewRecycleViewAdapter:ReviewRecycleViewAdapter

    lateinit var recyclerView: RecyclerView
    lateinit var noReviewTextView: TextView

    constructor(listReview : MutableList<UserReview>) : this() {
        this@ReviewFragment.listReview = listReview
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var v = inflater.inflate(R.layout.fragment_review, container, false)

        recyclerView = v.findViewById(R.id.recycler_view)
        noReviewTextView = v.findViewById(R.id.no_review_tv)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(v.context)
            reviewRecycleViewAdapter = ReviewRecycleViewAdapter()
            adapter = reviewRecycleViewAdapter
        }

        addDataSet()

        return v
    }

    private fun addDataSet(){
        if(listReview.isEmpty()){
            recyclerView.visibility = View.GONE
            noReviewTextView.visibility = View.VISIBLE
        }else{
            noReviewTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        reviewRecycleViewAdapter.submitList(listReview)
        reviewRecycleViewAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        reviewRecycleViewAdapter.notifyDataSetChanged()
    }

}
