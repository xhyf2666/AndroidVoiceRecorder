package com.example.voicerecord.Activity

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import com.example.voicerecord.R
import com.example.voicerecord.databinding.ActivityVoicePlayBinding
import me.bogerchan.niervisualizer.NierVisualizerManager
import me.bogerchan.niervisualizer.renderer.IRenderer
import me.bogerchan.niervisualizer.renderer.circle.CircleBarRenderer
import me.bogerchan.niervisualizer.renderer.circle.CircleRenderer
import me.bogerchan.niervisualizer.renderer.circle.CircleSolidRenderer
import me.bogerchan.niervisualizer.renderer.circle.CircleWaveRenderer
import me.bogerchan.niervisualizer.renderer.columnar.*
import me.bogerchan.niervisualizer.renderer.line.LineRenderer
import me.bogerchan.niervisualizer.renderer.other.ArcStaticRenderer
import me.bogerchan.niervisualizer.util.NierAnimator
import java.io.File
import kotlin.concurrent.thread

class VoicePlayActivity : AppCompatActivity() {
    private lateinit var binding:ActivityVoicePlayBinding
    var filename:String?=""
    var duration:Int=0
    var mMediaPlayer:MediaPlayer?=null
    var isPlaying:Boolean=false
    var isLoop:Boolean=false
    val updateTimeNow=1
    val updateSeekBar=2
    val playOver=3
    var isOver=false
    var speed=Array<Float>(8){i->0f}
    var speedLevel=3
    lateinit var effect: Array<IRenderer>
    val visualizerManager = NierVisualizerManager()
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
        if (getSupportActionBar() != null){
            getSupportActionBar()?.hide();
        }
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
        mMediaPlayer=MediaPlayer()
        mMediaPlayer?.setDataSource(file.absolutePath)
        mMediaPlayer?.prepare()

        getEffectType()

        val state = mMediaPlayer?.audioSessionId?.let { visualizerManager.init(it) }
        if (NierVisualizerManager.SUCCESS != state) {
            // 可以进行一些错误处理...
        }
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

    //获取用户选择的可视化特效
    fun getEffectType(){
        val preferences=getSharedPreferences("config", Context.MODE_PRIVATE)
        val effect_type=preferences.getInt("effect_type",7)
        when(effect_type){
            1->{
                effect= arrayOf(ColumnarType1Renderer())
            }
            2->{
                effect= arrayOf(ColumnarType2Renderer())
            }
            3->{
                effect= arrayOf(ColumnarType3Renderer())
            }
            4->{
                effect= arrayOf(ColumnarType4Renderer())
            }
            5->{
                effect= arrayOf(LineRenderer(true))
            }
            6->{
                effect= arrayOf(CircleBarRenderer())
            }
            7->{
                effect= arrayOf(CircleRenderer(true))
            }
            8->{
                effect= arrayOf(CircleRenderer(true),CircleBarRenderer())
            }
            9->{
                effect= arrayOf(CircleRenderer(true),CircleBarRenderer(),LineRenderer(true))
            }
            10->{
                effect= arrayOf(
                    ArcStaticRenderer(
                        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = Color.parseColor("#cfa9d0fd")
                        }),
                    ArcStaticRenderer(
                        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = Color.parseColor("#dad2eafe")
                        },
                        amplificationOuter = .83f,
                        startAngle = -90f,
                        sweepAngle = 225f
                    ),
                    ArcStaticRenderer(
                        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = Color.parseColor("#7fa9d0fd")
                        },
                        amplificationOuter = .93f,
                        amplificationInner = 0.8f,
                        startAngle = -45f,
                        sweepAngle = 135f
                    ),
                    CircleSolidRenderer(
                        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = Color.parseColor("#d2eafe")
                        },
                        amplification = .45f
                    ),
                    CircleBarRenderer(
                        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            strokeWidth = 4f
                            color = Color.parseColor("#efe3f2ff")
                        },
                        modulationStrength = 1f,
                        type = CircleBarRenderer.Type.TYPE_A_AND_TYPE_B,
                        amplification = 1f, divisions = 8
                    ),
                    CircleBarRenderer(
                        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            strokeWidth = 5f
                            color = Color.parseColor("#e3f2ff")
                        },
                        modulationStrength = 0.1f,
                        amplification = 1.2f,
                        divisions = 8
                    ),
                    CircleWaveRenderer(
                        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            strokeWidth = 6f
                            color = Color.WHITE
                        },
                        modulationStrength = 0.2f,
                        type = CircleWaveRenderer.Type.TYPE_B,
                        amplification = 1f,
                        animator = NierAnimator(
                            interpolator = LinearInterpolator(),
                            duration = 20000,
                            values = floatArrayOf(0f, -360f)
                        )
                    ),
                    CircleWaveRenderer(
                        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            strokeWidth = 6f
                            color = Color.parseColor("#7fcee7fe")
                        },
                        modulationStrength = 0.2f,
                        type = CircleWaveRenderer.Type.TYPE_B,
                        amplification = 1f,
                        divisions = 8,
                        animator = NierAnimator(
                            interpolator = LinearInterpolator(),
                            duration = 20000,
                            values = floatArrayOf(0f, -360f)
                        )
                    )

                )
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
            visualizerManager.start(binding.svWave, effect)
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
        binding.buttonDeletePlay.setOnClickListener(){
            showDeleteDialog()
        }
        binding.buttonBackPlay.setOnClickListener(){
            finish()
        }
        binding.voicePlayButtonReset.setOnClickListener(){
            rePlay()
        }
        binding.voicePlayButtonLoop.setOnClickListener(){
            changeLoop()
        }
        binding.voicePlayButtonLoopNot.setOnClickListener(){
            changeLoop()
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
        visualizerManager.release()
        isOver=true;
        isPlaying=false;
    }

    fun showDeleteDialog(){
        val builder= AlertDialog.Builder(this)
        builder.setTitle("提示")
        builder.setMessage("您确定要删除当前录音吗？")

        builder.setPositiveButton(
            "确定") { dialog, id ->
            mMediaPlayer?.release()
            mMediaPlayer=null
            val file=File(filename)
            file.delete()
            finish()
        }
        builder.setNegativeButton("取消",null)
        val dialog=builder.create()
        dialog.show()
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

    //修改循环播放状态
    fun changeLoop(){
        if(binding.voicePlayButtonLoop.visibility.equals(View.VISIBLE)){
            binding.voicePlayButtonLoop.visibility=View.INVISIBLE
            binding.voicePlayButtonLoopNot.visibility=View.VISIBLE
            isLoop=false
        }
        else if(binding.voicePlayButtonLoopNot.visibility.equals(View.VISIBLE)){
            binding.voicePlayButtonLoop.visibility=View.VISIBLE
            binding.voicePlayButtonLoopNot.visibility=View.INVISIBLE
            isLoop=true
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