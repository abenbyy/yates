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
import com.abencrauz.yates.adapter.PostRecycleViewAdapter
import com.abencrauz.yates.models.UserPost

class PostFragment() : Fragment() {

    var listPost:MutableList<UserPost> = mutableListOf()

    lateinit var postRecycleViewAdapter: PostRecycleViewAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var noPostTextView:TextView

    constructor(listPost : MutableList<UserPost>) : this() {
        this@PostFragment.listPost = listPost
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var v = inflater.inflate(R.layout.fragment_post, container, false)

        recyclerView = v.findViewById(R.id.recycler_view)
        noPostTextView = v.findViewById(R.id.no_post_tv)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(v.context)
            postRecycleViewAdapter = PostRecycleViewAdapter()
            adapter = postRecycleViewAdapter
        }

        addDataSet()

        return v
    }

    private fun addDataSet(){
        if(listPost.isEmpty()){
            recyclerView.visibility = View.GONE
            noPostTextView.visibility = View.VISIBLE
        }else{
            noPostTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        postRecycleViewAdapter.submitList(listPost)
        postRecycleViewAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        postRecycleViewAdapter.notifyDataSetChanged()
    }

}
