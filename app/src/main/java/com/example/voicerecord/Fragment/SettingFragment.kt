package com.example.voicerecord.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voicerecord.Adapter.VoiceListAdapter
import com.example.voicerecord.R
import java.io.File

class SettingFragment : Fragment() {

    var effect_id:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onStart() {
        super.onStart()
        val preferences=activity?.getSharedPreferences("config", Context.MODE_PRIVATE)
        val effect_type=preferences?.getInt("effect_type",7)
        when(effect_type){
            1->{effect_id=R.id.visualize_effect_1}
            2->{effect_id=R.id.visualize_effect_2}
            3->{effect_id=R.id.visualize_effect_3}
            4->{effect_id=R.id.visualize_effect_4}
            5->{effect_id=R.id.visualize_effect_5}
            6->{effect_id=R.id.visualize_effect_6}
            7->{effect_id=R.id.visualize_effect_7}
            8->{effect_id=R.id.visualize_effect_8}
            9->{effect_id=R.id.visualize_effect_9}
            10->{effect_id=R.id.visualize_effect_10}
        }
        val effectRadioGroup=activity?.findViewById<RadioGroup>(R.id.visualize_effect_group)
        effectRadioGroup?.check(effect_id)
        effectRadioGroup?.setOnCheckedChangeListener(
            { radioGroup, i ->
                effect_id=i
            }
        )
        activity?.findViewById<Button>(R.id.button_setting_save)?.setOnClickListener(){
            save()
        }

    }

    fun save(){
        var effect_type:Int=0;
        when(effect_id){
            R.id.visualize_effect_1->{effect_type=1}
            R.id.visualize_effect_2->{effect_type=2}
            R.id.visualize_effect_3->{effect_type=3}
            R.id.visualize_effect_4->{effect_type=4}
            R.id.visualize_effect_5->{effect_type=5}
            R.id.visualize_effect_6->{effect_type=6}
            R.id.visualize_effect_7->{effect_type=7}
            R.id.visualize_effect_8->{effect_type=8}
            R.id.visualize_effect_9->{effect_type=9}
            R.id.visualize_effect_10->{effect_type=10}
        }
        val preferences=activity?.getSharedPreferences("config", Context.MODE_PRIVATE)
        val editor = preferences?.edit()
        editor?.clear()
        editor?.putInt("effect_type",effect_type)
        editor?.apply()
    }
}