package com.kyle.clandestinecommunications

import android.annotation.TargetApi
import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ManualFlashlightActivity : AppCompatActivity() {

    var toggle = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_flashlight)




        val flashToggle = findViewById<Button>(R.id.flashlight)
        flashToggle.setOnClickListener {

        }

    }

    @TargetApi(23)
    private fun toggleCam(){
        val camMan = (getSystemService(Context.CAMERA_SERVICE) as CameraManager)
        val camId = camMan.cameraIdList[0]
        toggle = !toggle
        camMan.setTorchMode(camId, toggle)
    }
}