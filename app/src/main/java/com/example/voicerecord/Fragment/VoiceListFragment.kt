package com.example.voicerecord.Fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voicerecord.Adapter.VoiceListAdapter
import com.example.voicerecord.R
import java.io.File

class VoiceListFragment : Fragment() {
    private var path: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_voice_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        //从指定路径下获取文件列表，将数据传给recyclerView的adapter
        path = context?.getExternalFilesDir("").toString() + "/voice/"
        val files = File(path).listFiles()
        val lists = ArrayList<String>()
        for (file in files!!) {
            println(file.absolutePath)
            lists.add(file.absolutePath)
        }
        val recyclerView = activity?.findViewById<RecyclerView>(R.id.recycler_view_voice_list)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = VoiceListAdapter(lists)
        if (recyclerView!!.itemDecorationCount == 0) {
            recyclerView.addItemDecoration(SpacesItemDecoration(10))
        }

    }

    override fun onResume() {
        super.onResume()
        path = context?.getExternalFilesDir("").toString() + "/voice/"
        val files = File(path).listFiles()
        val lists = ArrayList<String>()
        for (file in files!!) {
            println(file.absolutePath)
            lists.add(file.absolutePath)
        }
        val recyclerView = activity?.findViewById<RecyclerView>(R.id.recycler_view_voice_list)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = VoiceListAdapter(lists)
        if (recyclerView!!.itemDecorationCount == 0) {
            recyclerView.addItemDecoration(SpacesItemDecoration(10))
        }
    }


    //设置间距的类
    class SpacesItemDecoration(private val value: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.bottom = value
        }
    }

}