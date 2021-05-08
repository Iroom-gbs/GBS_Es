package com.dayo.executer

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MapActivity : AppCompatActivity(){
    override fun onCreate(savedInstantState: Bundle?)
    {
        super.onCreate(savedInstantState)
        setContentView(R.layout.activity_map)

        val backButton : Button = findViewById(R.id.backButton)

        backButton.setOnClickListener()
        {
            finish()
        }
    }
}