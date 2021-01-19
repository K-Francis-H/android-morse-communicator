package com.kyle.clandestinecommunications

import android.annotation.TargetApi
import android.content.Context
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.VibrationEffect.createWaveform
import android.os.Vibrator
import java.util.stream.IntStream.range
import kotlin.concurrent.thread

//constants for alphabet -> morse


/*const val ZERO = "-----"
const val ONE = ".----"
const val TWO = "..---"
const val THREE =*/
val NUMBERS = listOf(
    "-----",    //0
    ".----",    //1
    "..---",    //2
    "...--",    //3
    "....-",    //4
    ".....",    //5
    "-....",    //6
    "--...",    //7
    "---..",    //8
    "----."     //9
)

//const val

//TODO constants for non-english, punctuation

//non-english follow several different systems

const val DIT = 100L
const val DAH = 300L
const val PAUSE = 100L

//char pauses
const val INTRAPAUSE = 100L
const val INTERPAUSE = 300L
//word pauses
const val INTERWORDPAUSE = 700L

const val SPACE = " "

//val SOS = listOf(S,O,S)

class MorseTranslator {

    companion object Static{
        const val A = ".--"
        const val B = "-..."
        const val C = "-.-."
        const val D = "-.."
        const val E = "."
        const val F = "..-."
        const val G = "--."
        const val H = "...."
        const val I = ".."
        const val J = ".---"
        const val K = "-.-"
        const val L = ".-.."
        const val M = "--"
        const val N = "-."
        const val O = "---"
        const val P = ".--."
        const val Q = "--.-"
        const val R = ".-."
        const val S = "..."
        const val T = "-"
        const val U = "..-"
        const val V = "...-"
        const val W = ".--"
        const val X = "-..-"
        const val Y = "-.--"
        const val Z = "--.."

        private lateinit var flashThread: Thread

        fun stringToMorse(msg: String): List<String>{
            val l = mutableListOf<String>()
            for(word in msg.split(" ")){
                var morseWord = ""
                for(ch in word){
                    morseWord += charToMorse(ch)
                }
                l.add(morseWord)
            }
            return l
        }

        fun stringToMorseWords(msg: String): List<List<String>>{
            val l = mutableListOf<List<String>>()
            for(word in msg.split(" ")){
                var morseWord = mutableListOf<String>()
                for(ch in word){
                    morseWord.add(charToMorse(ch))
                    //morseWord += charToMorse(ch)
                }
                l.add(morseWord)
            }
            return l
        }

        fun charToMorse(ch: Char): String{
            if(ch.equals('A', true)) return A
            if(ch.equals('B', true)) return B
            if(ch.equals('C', true)) return C
            if(ch.equals('D', true)) return D
            if(ch.equals('E', true)) return E
            if(ch.equals('F', true)) return F
            if(ch.equals('G', true)) return G
            if(ch.equals('H', true)) return H
            if(ch.equals('I', true)) return I
            if(ch.equals('J', true)) return J
            if(ch.equals('K', true)) return K
            if(ch.equals('L', true)) return L
            if(ch.equals('M', true)) return M
            if(ch.equals('N', true)) return N
            if(ch.equals('O', true)) return O
            if(ch.equals('P', true)) return P
            if(ch.equals('Q', true)) return Q
            if(ch.equals('R', true)) return R
            if(ch.equals('S', true)) return S
            if(ch.equals('T', true)) return T
            if(ch.equals('U', true)) return U
            if(ch.equals('V', true)) return V
            if(ch.equals('W', true)) return W
            if(ch.equals('X', true)) return X
            if(ch.equals('Y', true)) return Y
            if(ch.equals('Z', true)) return Z
            //default:
            return ""
        }

        @TargetApi(21)
        fun SOS(ctx: Context, opt: Options){
            val arr = morseToLongArray(listOf(S,O,S), true)

            val vb = (ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)

            val repeat = if(opt.isRepeated) 0 else -1

            //vb.vibrate(arr, -1)
            if(opt.isVibrate){
                vibrateCompat(vb, arr, repeat)
            }
            if(opt.isFlash){
                //TODO need to use the old method for controlling the camera as well
                val camMan = (ctx.getSystemService(Context.CAMERA_SERVICE) as CameraManager)
                morseFlash(camMan, camMan.cameraIdList[0], arr, opt.isRepeated)
                /*for(cam in camMan.cameraIdList){
                    //camMan.getCameraCharacteristics(cam).h

                }*/
            }
        }

        @TargetApi(23)
        fun morseFlash(camMan: CameraManager, camId: String, a: LongArray, isRepeat: Boolean){
            flashThread = thread {
                try {
                    var toggle = true
                    do {//guarantee it happens once
                        for (time in a) {
                            camMan.setTorchMode(camId, toggle)
                            Thread.sleep(time)
                            toggle = !toggle
                        }
                    } while (isRepeat)
                    //ensure we shut it down after
                    camMan.setTorchMode(camId, false)
                }catch(exception: Exception){
                    exception.printStackTrace()
                    flashThread.join()
                }
            }
        }

        fun morseFlash_pre23(a: LongArray, isRepeat: Boolean){
            flashThread = thread {
                try {
                    val camera = Camera.open()
                    var toggle = true
                    do {//guarantee it happens once
                        for (time in a) {
                            if (toggle) {
                                camera.parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                                camera.startPreview()
                            } else {
                                camera.parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
                                camera.stopPreview()
                            }
                            Thread.sleep(time)
                            toggle = !toggle
                        }
                    } while (isRepeat)
                    camera.parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
                    camera.stopPreview()
                    camera.release()
                }catch(exception: Exception){
                    exception.printStackTrace()
                    flashThread.join()
                }
            }
        }

        //TODO flash method needs  to support deprecated ways
        @TargetApi(21)
        fun send(ctx: Context, msg: String, opt: Options){
            /*val arr = morseToLongArray(
                stringToMorse(msg),
                true
            )*/
            val arr = morseWordsToLongArray(
                    stringToMorseWords(msg),
                    true
            )
            val vb = (ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
            //vb.vibrate(arr, -1)

            //convert to index of repeat, -1 -> no repeat
            val repeat = if(opt.isRepeated) 0 else -1

            //vb.vibrate(arr, -1)
            if(opt.isVibrate){
                vibrateCompat(vb, arr, repeat)
            }
            if(opt.isFlash){
                if(Build.VERSION.SDK_INT >= 23) {
                    val camMan = (ctx.getSystemService(Context.CAMERA_SERVICE) as CameraManager)
                    morseFlash(camMan, camMan.cameraIdList[0], arr, opt.isRepeated)
                }else{
                    morseFlash_pre23(arr, opt.isRepeated)
                }
            }
        }


        fun cancel(ctx: Context){
            val vb = (ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
            vb.cancel()

            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    cancelFlash23(ctx)
                } else {
                    cancelFlash_pre23()
                }
            }catch(exception: Exception){
                exception.printStackTrace()
                //probably uninitialized flash thread, not a problem
            }
        }

        @TargetApi(23)
        fun cancelFlash23(ctx: Context){
            //stop flashing too if it is going on
            flashThread.interrupt()
            //then make sure torch mode is off in case interruption leaves it on
            val camMan = (ctx.getSystemService(Context.CAMERA_SERVICE) as CameraManager)
            camMan.setTorchMode(camMan.cameraIdList[0], false)
        }

        fun cancelFlash_pre23(){
            flashThread.interrupt()
            val camera = Camera.open()
            camera.parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
            camera.stopPreview()
            camera.release()
        }

        fun vibrateCompat(vb: Vibrator, a: LongArray, isRepeat: Int){
            if(Build.VERSION.SDK_INT >= 26) {
                val effect = VibrationEffect.createWaveform(a, isRepeat)
                vb.vibrate(effect)
            }else{
                vb.vibrate(a, isRepeat)
            }
        }

        //for pre api 26
        fun morseToLongArray(morse: List<String>, initWithZero: Boolean): LongArray{
            val l = mutableListOf<Long>()
            var index = 0

            if(initWithZero){
                l.add(index++, 0L)
            }

            for(word in morse){
                var chIndex = 0
                for(ch in word){
                    //encode dots and dashes
                    if(ch == '.'){
                        l.add(index++, DIT)
                    }else{//is '_'
                        l.add(index++, DAH)
                    }

                    //encode interchar pause or inter word long pause
                    if(chIndex == word.length-1){
                        //big pause
                        l.add(index++, PAUSE*4)
                    }else{
                        //short pause
                        l.add(index++, PAUSE)
                    }
                    chIndex++
                }
            }
            return l.toLongArray()
        }

        fun morseWordsToLongArray(morse: List<List<String>>, initWithZero: Boolean): LongArray{
            val l = mutableListOf<Long>()

            if(initWithZero){
                l.add(0L)
            }

            for(word in morse){
                var chIndex = 0
                for(ch in word){
                    var ddIndex = 0
                    for(dd in ch){//dit dahs in character
                        //encode dots and dashes
                        if(dd == '.'){
                            l.add(DIT)
                        }else{//is '_'
                            l.add(DAH)
                        }

                        if(chIndex == word.size-1 && ddIndex == ch.length-1){//pause between words
                            l.add(INTERWORDPAUSE)
                        }
                        else if(ddIndex == ch.length-1){//pause between characters
                            l.add(INTERPAUSE)
                        }
                        else{//ditdahs in same character
                            l.add(INTRAPAUSE)
                        }
                        ddIndex++
                    }
                    chIndex++
                }
            }
            return l.toLongArray()
        }
    }

    fun textToVibration(ctx: Context, text: String){
        //create array of long pairs

        val vb = (ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)


        if(Build.VERSION.SDK_INT >= 26) {
            val sosVibration = VibrationEffect.createWaveform(
                longArrayOf(50, 50, 100, 50, 50),
                intArrayOf(DEFAULT_AMPLITUDE, 0, DEFAULT_AMPLITUDE, 0, DEFAULT_AMPLITUDE),
                -1
            )
        }
        else{
            vb.vibrate(
                longArrayOf(0, 50, 50, 100, 50, 50)
                ,-1
            )
        }
    }

    fun sosToVibratorEffect(){

    }

    @TargetApi(26)
    fun morseToVibratorEffect(morse: List<String>): VibrationEffect{
        val longArr = morseToLongArray(morse, false)

        return VibrationEffect.createWaveform(
            longArr,
            -1
        )

    }



    fun textToFlash(text: String){

    }

}