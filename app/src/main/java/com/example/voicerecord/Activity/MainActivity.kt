package com.example.voicerecord.Activity

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager2.widget.ViewPager2
import com.example.voicerecord.Adapter.ViewPager2Adapter
import com.example.voicerecord.Fragment.SettingFragment
import com.example.voicerecord.Fragment.VoiceListFragment
import com.example.voicerecord.Fragment.VoiceRecordFragment
import com.example.voicerecord.R
import com.example.voicerecord.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val  adapter=ViewPager2Adapter(this)
        adapter.addFragment(VoiceRecordFragment())
        adapter.addFragment(VoiceListFragment())
        adapter.addFragment(SettingFragment())
        binding.viewPager.adapter=adapter
        binding.viewPager.setUserInputEnabled(false);
        //绑定ViewPager2，实现三个主页面跳转
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0->{
                        binding.radioGroupBottom.check(binding.buttonVoiceRecord.id)
                    }
                    1->{
                        binding.radioGroupBottom.check(binding.buttonVoiceList.id)
                    }
                    2->{
                        binding.radioGroupBottom.check(binding.buttonSetting.id)

                    }
                }
            }
        })
        //点击按钮时变化UI
        binding.radioGroupBottom.setOnCheckedChangeListener{
            group,checkID->
            when(checkID){
                binding.buttonVoiceRecord.id->{
                    binding.viewPager.currentItem=0

                    binding.buttonVoiceRecord.setTextColor(Color.GREEN)
                    binding.buttonVoiceList.setTextColor(Color.BLACK)
                    binding.buttonSetting.setTextColor(Color.BLACK)
                    var drawable = AppCompatResources.getDrawable(baseContext,R.drawable.voice_record)
                    drawable?.setBounds(0, 0, drawable!!.minimumWidth, drawable!!.minimumHeight)
                    binding.buttonVoiceRecord.setCompoundDrawables(null, drawable, null, null)
                    drawable = AppCompatResources.getDrawable(baseContext,R.drawable.icon_voice_list_notcheck)
                    drawable?.setBounds(0, 0, drawable!!.minimumWidth, drawable!!.minimumHeight)
                    binding.buttonVoiceList.setCompoundDrawables(null, drawable, null, null)
                    drawable = AppCompatResources.getDrawable(baseContext,R.drawable.icon_setting_notcheck)
                    drawable?.setBounds(0, 0, drawable!!.minimumWidth, drawable!!.minimumHeight)
                    binding.buttonSetting.setCompoundDrawables(null, drawable, null, null)
                }
                binding.buttonVoiceList.id->{
                    binding.viewPager.currentItem=1

                    binding.buttonVoiceRecord.setTextColor(Color.BLACK)
                    binding.buttonVoiceList.setTextColor(Color.GREEN)
                    binding.buttonSetting.setTextColor(Color.BLACK)

                    var drawable = AppCompatResources.getDrawable(baseContext,R.drawable.icon_record_voice_notcheck)
                    drawable?.setBounds(0, 0, drawable!!.minimumWidth, drawable!!.minimumHeight)
                    binding.buttonVoiceRecord.setCompoundDrawables(null, drawable, null, null)
                    drawable = AppCompatResources.getDrawable(baseContext,R.drawable.icon_voice_list)
                    drawable?.setBounds(0, 0, drawable!!.minimumWidth, drawable!!.minimumHeight)
                    binding.buttonVoiceList.setCompoundDrawables(null, drawable, null, null)
                    drawable = AppCompatResources.getDrawable(baseContext,R.drawable.icon_setting_notcheck)
                    drawable?.setBounds(0, 0, drawable!!.minimumWidth, drawable!!.minimumHeight)
                    binding.buttonSetting.setCompoundDrawables(null, drawable, null, null)
                }
                binding.buttonSetting.id->{
                    binding.viewPager.currentItem=2

                    binding.buttonVoiceRecord.setTextColor(Color.BLACK)
                    binding.buttonVoiceList.setTextColor(Color.BLACK)
                    binding.buttonSetting.setTextColor(Color.GREEN)

                    var drawable = AppCompatResources.getDrawable(baseContext,R.drawable.icon_record_voice_notcheck)
                    drawable?.setBounds(0, 0, drawable!!.minimumWidth, drawable!!.minimumHeight)
                    binding.buttonVoiceRecord.setCompoundDrawables(null, drawable!!, null, null)
                    drawable = AppCompatResources.getDrawable(baseContext,R.drawable.icon_voice_list_notcheck)
                    drawable?.setBounds(0, 0, drawable!!.minimumWidth, drawable!!.minimumHeight)
                    binding.buttonVoiceList.setCompoundDrawables(null, drawable, null, null)
                    drawable = AppCompatResources.getDrawable(baseContext,R.drawable.icon_setting)
                    drawable?.setBounds(0, 0, drawable!!.minimumWidth, drawable!!.minimumHeight)
                    binding.buttonSetting.setCompoundDrawables(null, drawable, null, null)
                }
            }
        }
    }
}