package com.abencrauz.yates.tab_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.abencrauz.yates.R
import com.abencrauz.yates.adapter.ReviewRecycleViewAdapter
import com.abencrauz.yates.models.UserReview

class ReviewFragment() : Fragment() {

    lateinit var listReview:MutableList<UserReview>

    lateinit var reviewRecycleViewAdapter:ReviewRecycleViewAdapter

    constructor(listReview : MutableList<UserReview>) : this() {
        this@ReviewFragment.listReview = listReview
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var v = inflater.inflate(R.layout.fragment_review, container, false)

        var recyclerView = v.findViewById<RecyclerView>(R.id.recycler_view)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(v.context)
            reviewRecycleViewAdapter = ReviewRecycleViewAdapter()
            adapter = reviewRecycleViewAdapter
        }

        addDataSet()

        return v
    }

    private fun addDataSet(){
        reviewRecycleViewAdapter.submitList(listReview)
        reviewRecycleViewAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        reviewRecycleViewAdapter.notifyDataSetChanged()
    }

}
