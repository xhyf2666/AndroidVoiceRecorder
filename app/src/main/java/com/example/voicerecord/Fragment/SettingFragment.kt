package com.example.voicerecord.Fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import com.example.voicerecord.R

class SettingFragment : Fragment() {

    private var effectId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onStart() {
        super.onStart()
        val preferences = activity?.getSharedPreferences("config", Context.MODE_PRIVATE)
        when (preferences?.getInt("effect_type", 7)) {
            1 -> {
                effectId = R.id.visualize_effect_1
            }
            2 -> {
                effectId = R.id.visualize_effect_2
            }
            3 -> {
                effectId = R.id.visualize_effect_3
            }
            4 -> {
                effectId = R.id.visualize_effect_4
            }
            5 -> {
                effectId = R.id.visualize_effect_5
            }
            6 -> {
                effectId = R.id.visualize_effect_6
            }
            7 -> {
                effectId = R.id.visualize_effect_7
            }
            8 -> {
                effectId = R.id.visualize_effect_8
            }
            9 -> {
                effectId = R.id.visualize_effect_9
            }
            10 -> {
                effectId = R.id.visualize_effect_10
            }
        }
        //监听RadioGroup
        val effectRadioGroup = activity?.findViewById<RadioGroup>(R.id.visualize_effect_group)
        effectRadioGroup?.check(effectId)
        effectRadioGroup?.setOnCheckedChangeListener { _, i ->
            effectId = i
        }
        activity?.findViewById<Button>(R.id.button_setting_save)?.setOnClickListener {
            save()
        }

    }

    //保存用户配置
    private fun save() {
        var effectType = 0
        when (effectId) {
            R.id.visualize_effect_1 -> {
                effectType = 1
            }
            R.id.visualize_effect_2 -> {
                effectType = 2
            }
            R.id.visualize_effect_3 -> {
                effectType = 3
            }
            R.id.visualize_effect_4 -> {
                effectType = 4
            }
            R.id.visualize_effect_5 -> {
                effectType = 5
            }
            R.id.visualize_effect_6 -> {
                effectType = 6
            }
            R.id.visualize_effect_7 -> {
                effectType = 7
            }
            R.id.visualize_effect_8 -> {
                effectType = 8
            }
            R.id.visualize_effect_9 -> {
                effectType = 9
            }
            R.id.visualize_effect_10 -> {
                effectType = 10
            }
        }
        val preferences = activity?.getSharedPreferences("config", Context.MODE_PRIVATE)
        val editor = preferences?.edit()
        editor?.clear()
        editor?.putInt("effect_type", effectType)
        editor?.apply()
        Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show()
    }
}