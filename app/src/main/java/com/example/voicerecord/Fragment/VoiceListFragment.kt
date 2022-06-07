package com.example.voicerecord.Fragment

import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voicerecord.Adapter.VoiceListAdapter
import com.example.voicerecord.R
import java.io.File

class VoiceListFragment : Fragment() {
    var path:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_voice_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        path=context?.getExternalFilesDir("").toString()+"/voice/"
        var files= File(path).listFiles()
        val lists=ArrayList<String>()
        for(file in files){
            System.out.println(file.absolutePath)
            lists.add(file.absolutePath)
        }

        val recyclerView= activity?.findViewById<RecyclerView>(R.id.recycler_view_voice_list)
        recyclerView?.layoutManager= LinearLayoutManager(context)
        recyclerView?.adapter=VoiceListAdapter(lists)
        recyclerView?.addItemDecoration(SpacesItemDecoration(10))

    }

    override fun onResume() {
        super.onResume()
        path=context?.getExternalFilesDir("").toString()+"/voice/"
        var files= File(path).listFiles()
        val lists=ArrayList<String>()
        for(file in files){
            System.out.println(file.absolutePath)
            lists.add(file.absolutePath)
        }
        val recyclerView= activity?.findViewById<RecyclerView>(R.id.recycler_view_voice_list)
        recyclerView?.layoutManager= LinearLayoutManager(context)
        recyclerView?.adapter=VoiceListAdapter(lists)

    }

    override fun onPause() {
        super.onPause()
    }


    class SpacesItemDecoration(val value:Int)  : RecyclerView.ItemDecoration() {
        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDraw(c, parent, state)
        }

        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDrawOver(c, parent, state)
        }

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.bottom=value
        }
    }

}