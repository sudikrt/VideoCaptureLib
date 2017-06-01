package com.geeksynergy.vediocapturelibtest

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import life.knowledge4.videotrimmer.K4LVideoTrimmer
import life.knowledge4.videotrimmer.interfaces.OnK4LVideoListener
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener
import kotlinx.android.synthetic.main.activity_trim.timeLine
/**
 * Created by Foolish_Guy on 6/1/2017.
 */
class TrimActivity : AppCompatActivity (), OnTrimVideoListener, OnK4LVideoListener {

    var mTrimmer : K4LVideoTrimmer? = null
    var mProgress : ProgressDialog? = null
    val TAG = "TrimActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trim)

        var path : String = ""
        if (intent != null) {
            path = intent.getStringExtra(VTConstants.EXTRA_VIDEO_PATH)
        }
        mProgress = ProgressDialog(this)
        mProgress?.setCancelable(false)
        mProgress?.setMessage(getString(R.string.progress_text))

        if (timeLine != null) {
            timeLine.setMaxDuration(60)
            timeLine.setOnK4LVideoListener(this)
            timeLine.setOnTrimVideoListener(this)
            timeLine.setVideoURI(Uri.parse(path))
            timeLine.setVideoInformationVisibility(true)
        }

    }
    override fun onTrimStarted() {
        mProgress?.show()
    }

    override fun onError(p0: String?) {
        mProgress?.cancel()

        runOnUiThread { Runnable { Log.d (TAG, p0) } }

    }

    override fun onVideoPrepared() {
        runOnUiThread{Runnable {
            Log.d (TAG, "On Video prepared")
        }}
    }

    override fun getResult(p0: Uri?) {
        mProgress?.cancel()
        runOnUiThread { Runnable { Log.d (TAG, p0?.path) } }
        Toast.makeText(this, getString(R.string.trimmed_vd) + " " + p0?.path, Toast.LENGTH_LONG).show()
        finish()
    }

    override fun cancelAction() {
        mProgress?.cancel()
        mTrimmer?.destroy()
        finish()
    }

}
