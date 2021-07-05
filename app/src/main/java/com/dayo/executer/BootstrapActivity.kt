package com.dayo.executer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dayo.executer.data.DataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BootstrapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bootstrap)
    }
    override fun onStart() {
        super.onStart()
        Toast.makeText(this, "버전 정보를 불러오고 있습니다.", Toast.LENGTH_SHORT).show()
        val alert = AlertDialog.Builder(this@BootstrapActivity)
            .setTitle("연결 오류")
            .setMessage("인터넷이 연결되있지 않습니다.\n재시도할까요?")
            .setPositiveButton("재시도") { _, _ -> tryInit() }
            .setNeutralButton("앱 종료하기") { _, _ -> finishAndRemoveTask() }
            .create()

        CoroutineScope(Dispatchers.Default).launch {
            delay(1500)
            while(true) {
                var tick = 0
                while(alert.isShowing) {
                    delay(100)
                }
                while(!DataManager.online) {
                    tick++
                    if(tick == 50){
                        CoroutineScope(Dispatchers.Main).launch {
                            try {
                                alert.show()
                            } catch(e: WindowManager.BadTokenException) { }
                        }
                        break
                    }
                    delay(100)
                }
                delay(500)
                if(tick != 50 && tryInit())
                    break
            }
        }
    }

    private fun tryInit(): Boolean {
        return if(DataManager.online && DataManager.loadSettings()){
            startActivity(Intent(this@BootstrapActivity, MainActivity::class.java))
            Log.e("asdf", "asdf")
            finish()
            true
        } else false
    }
}