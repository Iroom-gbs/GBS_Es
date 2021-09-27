package com.dayo.executer

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dayo.executer.data.HttpConnection
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LostAndFoundInsert : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost_and_found_insert)

        findViewById<FloatingActionButton>(R.id.lost_and_found_apply).setOnClickListener {
            var name = findViewById<EditText>(R.id.lost_and_found_name).text.toString()
            var info = findViewById<EditText>(R.id.lost_and_found_dsc).text.toString()
            if(name == "" || info == ""){
                AlertDialog.Builder(this)
                    .setTitle("안내")
                    .setMessage("빈칸이 있습니다!")
                    .setPositiveButton("OK"){ _, _ -> }
                    .create().show()
            }
            else {
                CoroutineScope(Dispatchers.IO).launch {
                    HttpConnection.DownloadString(
                        "http://20.41.76.129/lostandfound/insert?name=${name}&info=${info}"
                    )
                }
            }
        }
    }
}