package com.example.damian.fouriertransform

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.support.annotation.NonNull
import android.Manifest.permission
import android.Manifest.permission.RECORD_AUDIO
import android.graphics.*
import android.support.v4.app.ActivityCompat
import android.media.MediaRecorder.AudioSource
import android.os.Build
import java.nio.ByteBuffer
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.TextView
import java.lang.Math.*
import java.sql.Time
import java.util.*
import kotlin.coroutines.experimental.EmptyCoroutineContext.plus
import kotlin.experimental.and


class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_listen -> {
                this.setTitle(R.string.title_listen)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_plot -> {
                this.setTitle(R.string.title_plot)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_chat -> {
                this.setTitle(R.string.title_chat)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    var text: String = "Listening..."
    var message = "222666082611101111*11111"
    var encoded = false
    private val scaleListener: ScaleListener = ScaleListener()

    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private var permissionToRecordAccepted = false
    private val permissions = arrayOf<String>(Manifest.permission.RECORD_AUDIO)
    var isRecording = false
    var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    val sampleRate = 8000
    public var scaleX = 1f

    var bufferSize: Int = AudioRecord.getMinBufferSize(sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT)


    var mRecord: AudioRecord = AudioRecord(MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize)

    var lastSecond = System.currentTimeMillis()
    var seconds : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        val mScaleDetector = ScaleGestureDetector(applicationContext, scaleListener)

        draw_view.setZOrderOnTop(true)
        draw_view.holder.setFormat(PixelFormat.TRANSLUCENT)

        draw_view.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                mScaleDetector.onTouchEvent(m)
                scaleX = scaleListener.scaleX
                return true
            }
        })
        encodeButton.setOnClickListener {
            if (!encoded) {
                stopRecording()
                message = NumberToText.convert(message)
                message_text.setText(message)
                encodeButton.setText(R.string.clear)
                encoded = true
            } else {
                message = ""
                message_text.setText(message)
                encoded = false
                encodeButton.setText(R.string.encode)
            }
        }
        listen_button.setOnClickListener {
            startRecord()
        }
    }

    fun stopRecording(){
        isRecording = false
        listen_button.setText(R.string.title_listen)
    }



    fun startRecord() {

        if (!isRecording) {
            isRecording = true
            Snackbar.make(listen_button, R.string.listening, Snackbar.LENGTH_SHORT).show()
            listen_button.setText(R.string.listening)

            Thread(Runnable {
                try {

                    mRecord.startRecording()
                    while (isRecording)
                        recordData()

                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }).start()

        } else {
            stopRecording()
        }
    }

    fun recordData() {
        if (isRecording) {
            val mBuffer = ShortArray(bufferSize)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mRecord.read(mBuffer, 0, bufferSize)
                val w = draw_view.width
                val h = draw_view.height

                val holder = draw_view.holder
                var canvas = holder.lockCanvas()
                var signalArray = FloatArray(256)

                signalArray.indices
                        .forEach { signalArray[it] = mBuffer[it] / 32768f }

                canvas.drawColor(Color.GREEN, PorterDuff.Mode.CLEAR)
                //border's properties
                paint = Paint()
                paint.style = Paint.Style.STROKE

                val width = 2
                paint.setStrokeWidth(width.toFloat())

                draw_view.drawGraph2D(signalArray)


                signalArray.indices
                        .filter { it % 3 == 0 }
                        .forEach {
                            paint.color = getColor(R.color.material_blue_grey_900)
                            canvas.drawLine(
                                    w.toFloat() / signalArray.size * (it) * scaleX,
                                    10f + h / 2,
                                    w.toFloat() / signalArray.size * (it) * scaleX,
                                    -10f + h / 2, paint)

                        }

                val spectArrOryg = FFT.fft(signalArray)


                spectArrOryg.indices
                        .filter { spectArrOryg[it] < 0 }
                        .forEach { spectArrOryg[it] = abs(spectArrOryg[it]) }

                val spectrumArray = spectArrOryg.clone()
                spectrumArray.indices
                        .filter { it != 44 && it != 51 && it != 54 && it != 61 &&
                                it != 79 && it != 86 && it != 94 }
                        .forEach {
                            spectrumArray[it] = 0f
                        }
                spectrumArray.indices
                        .filter { it == 44 && it == 51 && it == 54 && it == 61 &&
                                it == 79 && it == 86 && it == 94 }
                        .forEach {
                            spectrumArray[it] = spectrumArray[it] * spectrumArray[it]
                        }

                var level1 = spectrumArray.indices
                        .filter { it < 62 }
                        .sumByDouble { spectArrOryg[it].toDouble() }
                var level2 = spectrumArray.indices
                        .filter { it > 62 }
                        .sumByDouble { spectArrOryg[it].toDouble() }

                level1 /= 30
                level1 += 0.01
                level2 /= 30
                level2 += 0.01


                var signals = IntArray(2)
                signals[0] = 0
                signals[1] = 0

                for (i in spectrumArray.indices) {
                    if (i < 62) {
                        if (spectrumArray[i] > signals[0] && spectrumArray[i] > level1)
                            signals[0] = i
                    }
                    if (i > 62) {
                        if (spectrumArray[i] > signals[1] && spectrumArray[i] > level2)
                            signals[1] = i
                    }
                }

                spectArrOryg.indices
                        .filter { spectArrOryg[it] > level1 && it < 100 && it > 20 }
                        .forEach {
                            System.out.println("num: " + it +
                                    " with power " + 20 * log10(spectArrOryg[it].toDouble()))
                        }

                paint.color = getColor(R.color.colorPrimaryDark)
                spectArrOryg.indices
                        .filter { it > 0 }
                        .forEach {
                            canvas.drawLine(
                                    w.toFloat() / spectArrOryg.size * it * scaleX,
                                    h.toFloat(),
                                    w.toFloat() / spectArrOryg.size * (it) * scaleX,
                                    (-spectArrOryg[it] * h + h - 8), paint)
                        }

                paint.color = getColor(android.R.color.holo_orange_light)
                spectrumArray.indices
                        .filter { it > 0 }
                        .forEach {
                            canvas.drawLine(
                                    w.toFloat() / spectrumArray.size * it * scaleX,
                                    h.toFloat(),
                                    w.toFloat() / spectrumArray.size * (it) * scaleX,
                                    (-spectrumArray[it] * h + h - 8), paint)
                        }

                holder.unlockCanvasAndPost(canvas)

                val lastText = text

                when {
                    signals[0] == 61 && signals[1] == 94 -> text = "#"
                    signals[0] == 61 && signals[1] == 79 -> text = "*"
                    signals[0] == 61 && signals[1] == 86 -> text = "0"
                    signals[0] == 44 && signals[1] == 79 -> text = "1"
                    signals[0] == 44 && signals[1] == 86 -> text = "2"
                    signals[0] == 44 && signals[1] == 94 -> text = "3"
                    signals[0] == 51 && signals[1] == 79 -> text = "4"
                    signals[0] == 51 && signals[1] == 86 -> text = "5"
                    signals[0] == 51 && signals[1] == 94 -> text = "6"
                    signals[0] == 54 && signals[1] == 79 -> text = "7"
                    signals[0] == 54 && signals[1] == 86 -> text = "8"
                    signals[0] == 54 && signals[1] == 94 -> text = "9"
                }


                if (seconds >= 1000) {
                    text = ""
                    seconds = 0
                    lastSecond = 0
                }
                else {
                    val accSecond = System.currentTimeMillis()
                    if (accSecond - lastSecond >= 1000)
                        lastSecond = accSecond

                    val deltaSecond : Long = accSecond - lastSecond
                    lastSecond = System.currentTimeMillis()
                    seconds += deltaSecond
                }

                if (text != lastText) {
                    message += text
                }


                runOnUiThread {
                    message_text.setText(message)
                    text_input.setText(text)
                }
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        if (!permissionToRecordAccepted) finish()
    }

    private class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener {
        constructor() : super()

        var scaleX = 1f
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleX *= 0.9f * detector.getScaleFactor()

            // Don't let the object get too small or too large.
            scaleX = Math.max(1f, Math.min(scaleX, 10.0f))
            return super.onScale(detector)
        }
    }
}