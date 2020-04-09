package com.abencrauz.yates.tab_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.abencrauz.yates.R
import com.abencrauz.yates.adapter.PostRecycleViewAdapter
import com.abencrauz.yates.models.UserPost

class PostFragment() : Fragment() {

    lateinit var listPost:MutableList<UserPost>

    lateinit var postRecycleViewAdapter: PostRecycleViewAdapter

    constructor(listPost : MutableList<UserPost>) : this() {
        this@PostFragment.listPost = listPost
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var v = inflater.inflate(R.layout.fragment_post, container, false)

        var recyclerView = v.findViewById<RecyclerView>(R.id.recycler_view)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(v.context)
            postRecycleViewAdapter = PostRecycleViewAdapter()
            adapter = postRecycleViewAdapter
        }

        addDataSet()

        return v
    }

    private fun addDataSet(){
        postRecycleViewAdapter.submitList(listPost)
        postRecycleViewAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        postRecycleViewAdapter.notifyDataSetChanged()
    }

}
