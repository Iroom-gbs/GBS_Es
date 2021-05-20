package com.dayo.executer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import com.dayo.executer.data.AblrData
import com.dayo.executer.data.DataManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class EditAblrActivity : AppCompatActivity() {

    var nEdtDat = -1
    var nAblrData = AblrData()
    var info = "main"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_ablr)

        val spinner = findViewById<Spinner>(R.id.spinner)
        val locItem = resources.getStringArray(R.array.place_list)
        val locDatItem = resources.getStringArray(R.array.place_data_list)
        val sthEditText = findViewById<EditText>(R.id.sth)
        val stmEditText = findViewById<EditText>(R.id.stm)
        val ethEditText = findViewById<EditText>(R.id.eth)
        val etmEditText = findViewById<EditText>(R.id.etm)

        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locItem)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                nAblrData.locationInfo = locDatItem[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Nothing
            }
        }
        if (intent.extras != null && intent.extras!!["edt"] != null && intent.extras!!["dataInfo"] != "tmp") {
            nEdtDat = intent.extras!!["edt"] as Int
            nAblrData = DataManager.todayAblrTableData[nEdtDat]
            spinner.setSelection(locDatItem.indexOf(nAblrData.locationInfo))
            sthEditText.setText(nAblrData.sth)
            stmEditText.setText(nAblrData.stm)
            ethEditText.setText(nAblrData.eth)
            etmEditText.setText(nAblrData.etm)
        } else if (intent.extras != null && intent.extras!!["edt"] != null && intent.extras!!["dataInfo"] == "tmp") {
            nEdtDat = intent.extras!!["edt"] as Int
            info = "tmp"
            nAblrData = DataManager.tmpAblrData[nEdtDat]
            spinner.setSelection(locDatItem.indexOf(nAblrData.locationInfo))
            sthEditText.setText(nAblrData.sth)
            stmEditText.setText(nAblrData.stm)
            ethEditText.setText(nAblrData.eth)
            etmEditText.setText(nAblrData.etm)
        } else if (intent.extras != null && intent.extras!!["edt"] == null && intent.extras!!["dataInfo"] == "tmp") {
            info = "tmp"
        }

        findViewById<FloatingActionButton>(R.id.saveButton).setOnClickListener {
            nAblrData.sth = sthEditText.text.toString()
            nAblrData.stm = stmEditText.text.toString()
            nAblrData.eth = ethEditText.text.toString()
            nAblrData.etm = etmEditText.text.toString()
            try {
                val nsth = nAblrData.sth.toInt()
                val neth = nAblrData.eth.toInt()
                val nstm = nAblrData.stm.toInt()
                val netm = nAblrData.etm.toInt()

                if (nsth > neth || (nsth == neth && nstm >= netm)) {
                    AlertDialog.Builder(this@EditAblrActivity)
                        .setTitle("요류")
                        .setMessage("시작시간이 끝날시간보다 클 수 없습니다.")
                        .setPositiveButton("OK") { _, _ -> }
                        .create().show()
                } else if (neth >= 24 || nsth >= 24 || nstm >= 60 || netm >= 60) {
                    AlertDialog.Builder(this@EditAblrActivity)
                        .setTitle("오류")
                        .setMessage("올바른 시간을 입력해주세요.")
                        .setPositiveButton("OK") { _, _ -> }
                        .create().show()
                } else {
                    if (nEdtDat == -1) {
                        if (info == "main")
                            DataManager.todayAblrTableData.add(nAblrData)
                        else if (info == "tmp")
                            DataManager.tmpAblrData.add(nAblrData)
                    } else if (info == "main") DataManager.todayAblrTableData[nEdtDat] = nAblrData
                    else DataManager.tmpAblrData[nEdtDat] = nAblrData
                    DataManager.saveSettings()
                    finish()
                }
            } catch (e: Exception) {
                AlertDialog.Builder(this@EditAblrActivity)
                    .setTitle("오류")
                    .setMessage("올바르지 않은 데이터를 입력했습니다.")
                    .setPositiveButton("OK") { _, _ -> }
                    .create().show()
            }
        }
    }
}