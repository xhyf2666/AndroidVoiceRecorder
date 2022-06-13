package com.example.voicerecord.Fragment

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.format.DateFormat
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
import java.io.File
import java.util.*
import kotlin.concurrent.thread

class VoiceRecordFragment : Fragment() {
    private var isRecord = false
    private var isPause = false
    private var mMediaRecorder: MediaRecorder? = null
    private var path: String = ""
    private lateinit var fileName: String
    val updateTimeText = 1
    val updateWaveView = 2
    val clearWaveView = 3
    private var buttonStartRecord: ImageButton? = null
    private var buttonDelete: ImageButton? = null
    private var buttonPause: ImageButton? = null
    private var buttonStop: ImageButton? = null
    private var buttonContinue: ImageButton? = null
    private var mAudioRecord: AudioRecord? = null
    private var audioSize: Int = 0
    private var defaultFormat = "aac"
    var waveView: WaveView? = null

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                updateTimeText -> {
                    activity?.findViewById<TextView>(R.id.record_time_text)?.text =
                        msg.obj as String
                }
                updateWaveView -> {
                    println("set waveView " + msg.obj)
                    waveView?.putValue(msg.obj as Int)
                }
                clearWaveView -> {
                    waveView?.clearValue()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_voice_record, container, false)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        buttonStartRecord = activity?.findViewById(R.id.button_start_record)
        buttonDelete = activity?.findViewById(R.id.button_delete_record)
        buttonPause = activity?.findViewById(R.id.button_pause_record)
        buttonStop = activity?.findViewById(R.id.button_stop_record)
        buttonContinue = activity?.findViewById(R.id.button_continue_record)
        waveView = activity?.findViewById(R.id.voice_wave_view)
        buttonStartRecord?.setOnClickListener {
            startRecord()
        }

        buttonStop?.setOnClickListener {
            showSaveDialog()
        }

        buttonPause?.setOnClickListener {
            pauseRecord()
        }

        buttonContinue?.setOnClickListener {
            continueRecord()
        }
        buttonDelete?.setOnClickListener {
            showDeleteDialog()
        }
    }

    //开始录音
    private fun startRecord() {
        isRecord = true
        buttonPause?.visibility = View.VISIBLE
        buttonStartRecord?.visibility = View.INVISIBLE
        checkPermission()

        path = context?.getExternalFilesDir("").toString() + "/voice/"
        val file = File(path)
        if (!file.exists()) file.mkdirs()
        fileName = DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA))
            .toString() + "." + defaultFormat
        val filePath = path + fileName

        mMediaRecorder = MediaRecorder()
        mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mMediaRecorder?.setOutputFile(filePath)
        mMediaRecorder?.prepare()
        mMediaRecorder?.start()

        audioSize = (44100 / 40) * 2
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
    private fun pauseRecord() {
        isPause = true
        buttonPause?.visibility = View.INVISIBLE
        buttonContinue?.visibility = View.VISIBLE
        buttonDelete?.visibility = View.VISIBLE
        buttonStop?.visibility = View.VISIBLE
        mMediaRecorder?.pause()
    }

    //停止录音
    private fun stopRecord() {
        isRecord = false
        isPause = false
        buttonDelete?.visibility = View.INVISIBLE
        buttonStop?.visibility = View.INVISIBLE
        buttonPause?.visibility = View.INVISIBLE
        buttonStartRecord?.visibility = View.VISIBLE
        buttonContinue?.visibility = View.INVISIBLE
        mMediaRecorder?.release()
        mMediaRecorder = null
        activity?.findViewById<TextView>(R.id.record_time_text)?.text = ""
    }

    //继续录音
    private fun continueRecord() {
        isPause = false
        buttonPause?.visibility = View.VISIBLE
        buttonContinue?.visibility = View.INVISIBLE
        buttonDelete?.visibility = View.INVISIBLE
        buttonStop?.visibility = View.INVISIBLE
        mMediaRecorder?.resume()
    }

    //删除当前录音结果
    private fun deleteRecord() {
        stopRecord()
        val file = File(path + fileName)
        if (file.exists())
            file.delete()
    }

    //检查权限，获取权限
    private fun checkPermission() {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val requireList = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) requireList.add(permission)
        }
        if (requireList.isNotEmpty()) {
            activity?.let { ActivityCompat.requestPermissions(it, permissions, 1) }
        }
    }

    //计时线程
    private fun countTime() {
        thread {
            var value = 0//单位 10 ms
            while (isRecord) {

                if (!isPause) {
                    value++
                    val msg = Message()
                    val str =
                        "" + value / 100 / 60 + ":" + (if (value / 100 % 60 < 10) "0" else "") + value / 100 % 60 + ":" + (if (value % 100 < 10) "0" else "") + value % 100
                    msg.what = updateTimeText
                    msg.obj = str
                    handler.sendMessage(msg)
                    Thread.sleep(10)
                }
            }
        }
    }

    //显示删除录音提示框
    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("提示")
        builder.setMessage("您确定要删除当前录音吗？")

        builder.setPositiveButton(
            "确定"
        ) { _, _ ->
            deleteRecord()
        }
        builder.setNegativeButton("取消", null)
        val dialog = builder.create()
        dialog.show()
    }

    //显示保存录音提示框，可修改文件名
    private fun showSaveDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("保存录音文件")
        val input = EditText(context)
        input.setText(fileName.substring(0, fileName.lastIndexOf(".")))
        builder.setView(input)

        builder.setPositiveButton(
            "确定"
        ) { _, _ ->
            var filenameNew = input.text.toString()
            filenameNew += ".$defaultFormat"
            stopRecord()
            if (filenameNew != fileName) {
                val file = File(path + fileName)
                val fileNew = File(path + filenameNew)
                file.copyTo(fileNew)
                file.delete()
            }
        }
        builder.setNegativeButton("取消", null)
        val dialog = builder.create()
        dialog.show()
    }

    //实时显示声音波形的线程
    private fun showVoiceWave() {
        thread {
            while (isRecord) {
                if (!isPause) {
                    val buffer = ByteArray(audioSize)
                    mAudioRecord?.read(buffer, 0, buffer.size)
                    var valueSum = 0
                    for (i in 0..audioSize - 2 step 2) {
                        if ((buffer[i + 1].toInt() and 0xff) < 0x80) {
                            val value: Int =
                                (buffer[i].toInt() and 0xff) or ((buffer[i + 1].toInt() and 0xff) shl 8)
                            valueSum += value
                        } else {
                            var value: Int =
                                (buffer[i].toInt() and 0xff) or ((buffer[i + 1].toInt() and 0xff) shl 8)
                            value = 0xffff - value + 1
                            valueSum += value
                        }
                    }
                }
            }
        }
    }
}