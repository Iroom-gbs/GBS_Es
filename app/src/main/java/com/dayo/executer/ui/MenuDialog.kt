package com.dayo.executer.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.dayo.executer.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MenuDialog(context: Context, private val listener: MenuDialogOnClickListener): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_fab_dialog)

        super.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        super.getWindow()?.setLayout(MATCH_PARENT, MATCH_PARENT)
        findViewById<FloatingActionButton>(R.id.exit_fab).setOnClickListener {
            super.cancel()
        }
        findViewById<FloatingActionButton>(R.id.asckFab).setOnClickListener {
            listener.OnAsckBtnClick()
            super.cancel()
        }
        findViewById<FloatingActionButton>(R.id.ablrFab).setOnClickListener {
            listener.OnAblrBtnClick()
            super.cancel()
        }
        findViewById<FloatingActionButton>(R.id.mapFab).setOnClickListener {
            listener.OnMapBtnClick()
            super.cancel()
        }
    }
}
//, android.R.style.Theme_DeviceDefault_Wallpaper_NoTitleBar