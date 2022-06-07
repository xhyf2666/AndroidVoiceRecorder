package com.example.voicerecord.Adapter


import android.content.Intent
import android.graphics.Canvas
import android.graphics.Rect
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.voicerecord.Activity.VoicePlayActivity
import com.example.voicerecord.R
import java.io.File

class VoiceListAdapter(val list:List<String>): RecyclerView.Adapter<VoiceListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val name:TextView =view.findViewById(R.id.item_voice_list_fileName)
        val time:TextView=view.findViewById(R.id.item_voice_list_fileInfo_time)
        val size:TextView=view.findViewById(R.id.item_voice_list_fileInfo_size)
        val format:TextView=view.findViewById(R.id.item_voice_list_fileInfo_format)
        val menu:TextView=view.findViewById(R.id.item_voice_list_todo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_voice_list, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.menu.setOnClickListener(){
            val position=viewHolder.adapterPosition
            Toast.makeText(parent.context,"menu "+list[position],Toast.LENGTH_SHORT).show()
        }
        viewHolder.itemView.setOnClickListener(){
            val position=viewHolder.adapterPosition
            val intent = Intent(parent.context,VoicePlayActivity::class.java)
            intent.putExtra("filename",list[position])
            parent.context.startActivity(intent)
            //Toast.makeText(parent.context,"play "+list[position],Toast.LENGTH_SHORT).show()
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        val file= File(data)
        val mediaplayer=MediaPlayer()
        mediaplayer.setDataSource(file.absolutePath)
        mediaplayer.prepare()
        val duration=mediaplayer.duration//单位 ms
        val size=file.length()//单位
        val format:String=data.substring(data.lastIndexOf(".")+1)
        var size_kb:Double=size.toDouble()/1024
        var duration_s:Int=duration/1000
        holder.name.setText(file.name)
        holder.time.setText(duration_s.toString())
        holder.size.setText("%.2f".format(size_kb)+"kb")
        holder.format.setText(format)
        if (mediaplayer != null) {
            mediaplayer.stop();
            mediaplayer.reset();
            mediaplayer.release();
        }
    }

    override fun getItemCount() = list.size

}
