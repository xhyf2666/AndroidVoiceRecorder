package com.example.voicerecord.Activity

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.example.voicerecord.databinding.ActivityVoicePlayBinding
import java.io.File
import kotlin.concurrent.thread

class VoicePlayActivity : AppCompatActivity() {
    private lateinit var binding:ActivityVoicePlayBinding
    var filename:String?=""
    var duration:Int=0
    var mMediaPlayer:MediaPlayer?=null
    var isPlaying:Boolean=false
    var isLoop:Boolean=true
    val updateTimeNow=1
    val updateSeekBar=2
    val playOver=3
    var isOver=false
    var speed=Array<Float>(8){i->0f}
    var speedLevel=3
    val handler=object: Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message){
            when(msg.what){
                updateTimeNow->{
                    binding.voicePlayTimeNow.setText(msg.obj as String)
                }
                updateSeekBar->{
                    binding.voicePlaySeekBar.setProgress(msg.obj as Int)
                }
                playOver->{
                    pausePlay()
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVoicePlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        filename=intent.getStringExtra("filename")
        val file= File(filename)
        val name=file.name
        binding.voicePlayName.setText(name)
        speed[1]=0.5f
        speed[2]=0.8f
        speed[3]=1.0f
        speed[4]=1.25f
        speed[5]=1.5f
        speed[6]=2.0f
        speed[7]=3.0f
//        speed.add(2,0.8f)
//        speed.add(3,1.0f)
//        speed.add(4,1.25f)
//        speed.add(5,1.5f)
//        speed.add(6,2f)
//        speed.add(7,3f)
        mMediaPlayer=MediaPlayer()
        mMediaPlayer?.setDataSource(file.absolutePath)
        mMediaPlayer?.prepare()
        duration= mMediaPlayer!!.duration//单位 ms
        binding.voicePlayTimeEnd.setText(timeFormat(duration/1000))
        playVoice()
        autoUpdate()
        setOnclickFun()
        mMediaPlayer?.setOnCompletionListener {
            System.out.println("播放完毕")
                if(isLoop){
                    rePlay()
                }
                else{
                    val msg=Message()
                    msg.what=playOver
                    handler.sendMessage(msg)
                }
        }
    }

    fun playVoice(){
        if(mMediaPlayer?.currentPosition==duration){
            rePlay()
        }
        else {
            setSpeed()
            mMediaPlayer?.start()
            isPlaying = true
            binding.voicePlayButtonPause.visibility = View.VISIBLE
            binding.voicePlayButtonPlay.visibility = View.INVISIBLE
        }
    }

    fun pausePlay(){
            mMediaPlayer?.pause()
            isPlaying=false
            binding.voicePlayButtonPause.visibility= View.INVISIBLE
            binding.voicePlayButtonPlay.visibility=View.VISIBLE
    }


    fun resetPlay(){
        mMediaPlayer?.reset()
        mMediaPlayer?.setDataSource(filename)
        mMediaPlayer?.prepare()
    }

    fun rePlay(){
        resetPlay()
        playVoice()
    }

    fun setOnclickFun() {
        binding.voicePlayButtonPlay.setOnClickListener() {
            playVoice()
        }
        binding.voicePlayButtonPause.setOnClickListener() {
            pausePlay()
        }
        binding.voicePlaySpeedUp.setOnClickListener(){
            changeSpeedLevel(1)
        }
        binding.voicePlaySpeedDown.setOnClickListener(){
            changeSpeedLevel(-1)
        }

        binding.voicePlaySeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                System.out.println("startTrack")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                System.out.println("stopTrack")
                val current_position:Int=binding.voicePlaySeekBar.progress*duration/100
                mMediaPlayer?.seekTo(current_position)
                updateTimeNow(binding.voicePlaySeekBar.progress*duration/100/1000)
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer?.release()
        isOver=true;
        isPlaying=false;
    }

    fun changeSpeedLevel(value: Int){
        speedLevel+=value
        if(speedLevel>7)
            speedLevel=7
        else if(speedLevel<1)
            speedLevel=1
        else{
            setSpeed()
        }

        Log.v("speedLevel",speedLevel.toString())
        var text:String="%.1f".format(speed[speedLevel])
        if(speedLevel==4)
            text="%.2f".format(speed[speedLevel])

        binding.voicePlaySpeed.setText(text)

    }

    fun setSpeed(){

        mMediaPlayer?.playbackParams=mMediaPlayer?.playbackParams!!.setSpeed(speed[speedLevel])
        Log.v("speed", mMediaPlayer?.playbackParams!!.speed.toString())
    }

    fun autoUpdate(){
        thread{
            while(!isOver){
                if(isPlaying){
                    var position:Int=(mMediaPlayer!!.currentPosition)
                    var seek_positon:Int=position*100/duration
                    updateTimeNow(position/1000)
                    updateSeekBar(seek_positon)
                    Thread.sleep(200)
                }
            }
        }
    }

    fun updateTimeNow(value:Int){
        var msg=Message()
        msg.what=updateTimeNow
        msg.obj=timeFormat(value)
        handler.sendMessage(msg)
    }

    fun updateSeekBar(value:Int){
        var msg=Message()
        msg.what=updateSeekBar
        msg.obj=value
        handler.sendMessage(msg)
    }

    fun timeFormat(value:Int): String {
        val str=(if(value/60<10)"0" else "")+value/60+":"+(if(value%60<10)"0" else "")+value%60
        return str
    }
}