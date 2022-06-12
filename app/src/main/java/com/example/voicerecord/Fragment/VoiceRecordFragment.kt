package com.example.voicerecord.Fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.voicerecord.R
import com.example.voicerecord.View.WaveView
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.ShortBuffer
import java.util.*
import kotlin.concurrent.thread

class VoiceRecordFragment : Fragment() {
    var isRecord=false
    var isPause=false
    var mMediaRecorder: MediaRecorder?=null
    var path:String=""
    lateinit var fileName:String
    val updateTimeText=1
    val updateWaveView=2
    val clearWaveView=3
    var button_start_record:ImageButton?=null
    var button_delete:ImageButton?=null
    var button_pause:ImageButton?=null
    var button_stop:ImageButton?=null
    var button_continue:ImageButton?=null
    var mAudioRecord:AudioRecord?=null
    var audioSize:Int=0
    var default_format="aac"
    var waveview:WaveView?=null
    val handler=object:Handler(Looper.getMainLooper()){
        override fun handleMessage(msg:Message){
            when(msg.what){
                updateTimeText->{
                    activity?.findViewById<TextView>(R.id.record_time_text)?.setText(msg.obj as String)
                }
                updateWaveView->{
                    System.out.println("set waveview "+msg.obj)
                    waveview?.putValue(msg.obj as Int)
                }
                clearWaveView->{
                    waveview?.clearValue()
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_voice_record, container, false)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button_start_record= activity?.findViewById<ImageButton>(R.id.button_start_record)
        button_delete=activity?.findViewById<ImageButton>(R.id.button_delete_record)
        button_pause=activity?.findViewById<ImageButton>(R.id.button_pause_record)
        button_stop=activity?.findViewById<ImageButton>(R.id.button_stop_record)
        button_continue=activity?.findViewById<ImageButton>(R.id.button_continue_record)
        waveview=activity?.findViewById<WaveView>(R.id.voice_wave_view)
        button_start_record?.setOnClickListener(){
            startRecord()
        }

        button_stop?.setOnClickListener(){
            showSaveDialog()
        }

        button_pause?.setOnClickListener(){
            pauseRecord()
        }

        button_continue?.setOnClickListener(){
            continueRecord()
        }
        button_delete?.setOnClickListener(){
            showDeleteDialog()
        }
    }

    //开始录音
    fun startRecord(){
        isRecord=true
        button_pause?.visibility=View.VISIBLE
        button_start_record?.visibility=View.INVISIBLE
        checkPermission()

        mMediaRecorder=MediaRecorder()
        path=context?.getExternalFilesDir("").toString()+"/voice/"
        var file= File(path)
        if(!file.exists())
            file.mkdirs()
        mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)// 设置麦克风
        mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
        mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        fileName = DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA)).toString()+ "."+default_format
        //注意文件夹要创建之后才能使用
        var filePath =  path+fileName
        /* ③准备 */
        mMediaRecorder?.setOutputFile(filePath)
        mMediaRecorder?.prepare()
        mMediaRecorder?.start()


        audioSize=(44100/40)*2
        mAudioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            audioSize
        )
        mAudioRecord!!.startRecording()
        countTime()
        showVoiceWave()
    }

    //暂停录音
    fun pauseRecord(){
        isPause=true
        button_pause?.visibility=View.INVISIBLE
        button_continue?.visibility=View.VISIBLE
        button_delete?.visibility=View.VISIBLE
        button_stop?.visibility=View.VISIBLE
        mMediaRecorder?.pause()
    }

    //停止录音
    fun stopRecord(){
        isRecord=false
        isPause=false
        button_delete?.visibility=View.INVISIBLE
        button_stop?.visibility=View.INVISIBLE
        button_pause?.visibility=View.INVISIBLE
        button_start_record?.visibility=View.VISIBLE
        button_continue?.visibility=View.INVISIBLE
        mMediaRecorder?.release()
        mMediaRecorder=null
        activity?.findViewById<TextView>(R.id.record_time_text)?.setText("")

        val msg=Message()
        msg.what=clearWaveView
        handler.sendMessage(msg)
    }

    //继续录音
    fun continueRecord(){
        isPause=false
        button_pause?.visibility=View.VISIBLE
        button_continue?.visibility=View.INVISIBLE
        button_delete?.visibility=View.INVISIBLE
        button_stop?.visibility=View.INVISIBLE
        mMediaRecorder?.resume()
    }

    //删除当前录音结果
    fun deleteRecord(){
        stopRecord()
        var file=File(path+fileName)
        if(file.exists())
            file.delete()
    }

    //检查权限，获取权限
    fun checkPermission(){
        val permissions= arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE)
        val requireList=ArrayList<String>()
        for (permission in permissions){
            if(ContextCompat.checkSelfPermission(requireContext(),permission)!= PackageManager.PERMISSION_GRANTED)
                requireList.add(permission)
        }
        if(!requireList.isEmpty()){
            activity?.let { ActivityCompat.requestPermissions(it, permissions, 1) }
        }
    }

    //计时线程
    fun countTime(){
        thread{
            var value=0;//单位 10 ms
            while(isRecord){

                if(!isPause){
                    value++
                    val msg=Message()
                    val str=""+value/100/60+":"+(if(value/100%60<10)"0" else "")+value/100%60+":"+(if(value%100<10)"0" else "")+value%100
                    msg.what=updateTimeText
                    msg.obj=str
                    handler.sendMessage(msg)
                    Thread.sleep(10)
                }
            }
        }
    }

    //显示删除录音提示框
    fun showDeleteDialog(){
        val builder=AlertDialog.Builder(context)
        builder.setTitle("提示")
        builder.setMessage("您确定要删除当前录音吗？")

        builder.setPositiveButton(
            "确定") { dialog, id ->
            deleteRecord()

        }
        builder.setNegativeButton("取消",null)
        val dialog=builder.create()
        dialog.show()
    }

    //显示保存录音提示框，可修改文件名
    fun showSaveDialog(){
        val builder=AlertDialog.Builder(context)
        builder.setTitle("保存录音文件")
        val input=EditText(context)
        input.setText(fileName.substring(0,fileName.lastIndexOf(".")))
        builder.setView(input)

        builder.setPositiveButton(
            "确定") { dialog, id ->
            var fileName_new=input.text.toString()
            fileName_new+="."+default_format
            stopRecord()
            if(!fileName_new.equals(fileName)){
                val file=File(path+fileName)
                val file_new=File(path+fileName_new)
                file.copyTo(file_new)
                file.delete()
            }

        }
        builder.setNegativeButton("取消",null)
        val dialog=builder.create()
        dialog.show()
    }

    //实时显示声音波形的线程
    fun showVoiceWave(){
        thread{
            while(isRecord){
                if(!isPause){
                    val buffer=ByteArray(audioSize)
                    mAudioRecord?.read(buffer,0,buffer.size)
                    var value_sum=0;
                    for(i in 0..audioSize-2 step 2){
                        if((buffer[i+1].toInt()and 0xff)<0x80){//正数
                        val value:Int=(buffer[i].toInt() and 0xff) or ((buffer[i+1].toInt() and 0xff)shl 8);
                        value_sum+=value
                        }
                        else{//负数求补码
                            var value:Int=(buffer[i].toInt() and 0xff) or ((buffer[i+1].toInt() and 0xff)shl 8);
                            value=0xffff-value+1
                            value_sum+=value
                        }
                    }
                    //计算出一次采样的总振幅
                    val msg=Message()
                    msg.what=updateWaveView;
                    msg.obj=value_sum;
                    handler.sendMessage(msg)
                }
            }
        }
    }
}