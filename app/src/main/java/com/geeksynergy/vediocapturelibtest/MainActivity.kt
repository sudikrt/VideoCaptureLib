package com.geeksynergy.vediocapturelibtest

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import life.knowledge4.videotrimmer.utils.FileUtils

class MainActivity : AppCompatActivity() {

    val TAG  = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        capture.setOnClickListener {v: View? -> captureVT(v)  }

        pick_from_gallery.setOnClickListener { view -> pickFromGallery (view) }

    }
    private fun checkStoragePermission () : Boolean{
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED
                ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED  ) {
            return true
        }
        return false
    }

    private fun captureVT(v: View?) {
        if (checkStoragePermission()) {
            requestPermission()
        } else {
            var captureIntent: Intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            startActivityForResult(captureIntent, VTConstants.REQUEST_VIDEO_TRIMMER)
        }
    }

    private fun pickFromGallery(view: View?) {

        if (checkStoragePermission()) {
            requestPermission()
        } else {
            var trimIntent : Intent = Intent()
            trimIntent.setTypeAndNormalize("video/*")
            trimIntent.action = Intent.ACTION_GET_CONTENT
            trimIntent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(Intent.createChooser(trimIntent, getString(R.string.choose_video)), VTConstants.REQUEST_VIDEO_TRIMMER)
        }
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.permission_title_rationale))
            builder.setMessage(getString(R.string.alert))
            builder.setPositiveButton(getString(R.string.label_ok), DialogInterface.OnClickListener{dialog: DialogInterface?, which: Int ->
                ActivityCompat
                        .requestPermissions(this@MainActivity,
                                arrayOf<String>(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE), VTConstants.REQUEST_CODE)
            })
            builder.setNegativeButton(getString(R.string.label_cancel), null)
            builder.show()
        } else {
            ActivityCompat
                    .requestPermissions(this@MainActivity,
                            arrayOf<String>(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE), VTConstants.REQUEST_CODE)

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == VTConstants.REQUEST_CODE) {
            var isAllPermissionGranted = false;
            for (result in grantResults) {
                if (!(result == PackageManager.PERMISSION_GRANTED)) {
                    isAllPermissionGranted = true
                }
            }
            if (isAllPermissionGranted) {
                Log.d (TAG, "All permissions are granted")
            } else {
                Log.e (TAG, "Some permissions are missing")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == VTConstants.REQUEST_VIDEO_TRIMMER) {
            val uri = data?.data

            if (uri != null) {
                startTrimActivity (uri)
            } else {
                Log.e (TAG, getString(R.string.improper_uri))
            }

        }
    }

    private fun  startTrimActivity(uri: Uri?) {
        val trimeIntent : Intent = Intent (this, TrimActivity::class.java)
        trimeIntent.putExtra(VTConstants.EXTRA_VIDEO_PATH, FileUtils.getPath(this, uri))
        startActivity(trimeIntent)
    }
}
