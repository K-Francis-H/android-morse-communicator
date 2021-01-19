package com.kyle.clandestinecommunications

import android.annotation.TargetApi
import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged

class Options constructor(isVibrate: Boolean, isFlash: Boolean, isRepeated: Boolean){
    public val isVibrate = isVibrate
    public val isFlash = isFlash
    public val isRepeated = isRepeated
}

class MainActivity : AppCompatActivity() {

    private lateinit var vibrateSwitch: Switch
    private lateinit var flashSwitch: Switch
    private lateinit var repeatSwitch: Switch

    var toggle = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vibrateSwitch = findViewById<Switch>(R.id.vibrate_toggle)
        flashSwitch = findViewById<Switch>(R.id.flash_toggle)
        repeatSwitch = findViewById<Switch>(R.id.repeat_toggle)

        val editText = findViewById<EditText>(R.id.textbox)
        val morseView = findViewById<TextView>(R.id.morse_view)

        /*val manFlashToggle = findViewById<Button>(R.id.flah_man_toggle)
        manFlashToggle.setOnClickListener {
            toggleCam()
        }*/

        val sosButton = findViewById<Button>(R.id.sos_button)
        sosButton.setOnClickListener {
            //checkShowNoTransmissionMethodToast()
            MorseTranslator.SOS(this, getTransmissionOptions())
        }

        val sendButton = findViewById<Button>(R.id.send_button)
        sendButton.setOnClickListener {
            //checkShowNoTransmissionMethodToast()
            if(editText.text.toString().isNotEmpty()) {
                MorseTranslator.send(this, editText.text.toString(), getTransmissionOptions())
            }
        }

        val cancelButton = findViewById<Button>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            MorseTranslator.cancel(this)
        }

        editText.doOnTextChanged { text, start, count, after ->
            //translate as user types message
            morseView.text = MorseTranslator.stringToMorse(text.toString()).joinToString(" ")
        }
    }

    private fun getTransmissionOptions(): Options{
        return Options(
                vibrateSwitch.isChecked,
                flashSwitch.isChecked,
                repeatSwitch.isChecked
        )
    }

    private fun checkShowNoTransmissionMethodToast(){
        //val opt = getTransmissionOptions()
        //if( (vibrateSwitch.isChecked == false) && (flashSwitch.isChecked == false)) {
            Toast.makeText(baseContext, "Please enable vibration and/or flash to transmit", Toast.LENGTH_SHORT).show()
        //}
    }

    @TargetApi(23)
    private fun toggleCam(){
        val camMan = (getSystemService(Context.CAMERA_SERVICE) as CameraManager)
        val camId = camMan.cameraIdList[0]
        toggle = !toggle
        camMan.setTorchMode(camId, toggle)
    }
}