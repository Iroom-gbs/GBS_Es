package com.dayo.executer.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.dayo.executer.App
import com.dayo.executer.MainActivity
import com.dayo.executer.data.AblrData
import com.dayo.executer.data.DataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

class AblrService : Service() {

    companion object {
        var res = ""
    }
    var deleteResult = 1
    var rigResult = 1
    var reExec = true
    var isFinished = false
    var ablrData = listOf<AblrData>()

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    var elist = mutableListOf<String>()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, "ablr") //오레오 부터 channelId가 반드시 필요하다.

        ablrData = AblrData.stringToAblrData(intent?.extras!!["ablr"] as String)
        Log.d("asdf", AblrData.ablrDataToString(ablrData))

        builder.setContentTitle("포그라운드 서비스")
        builder.setContentText("포그라운 서비스 실행중")

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        builder.setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                    NotificationChannel(
                            "ablr",
                            "학습실 신청",
                            NotificationManager.IMPORTANCE_NONE
                    )
            )
        }
        startForeground(1, builder.build())

        val webView = BackgroundWebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies { }
        cookieManager.flush()
        webView.settings.domStorageEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("asdf", view?.url.toString())
                if (view == null)
                    return
                if (view.url == null)
                    return

                Log.d("asdf", view.url!!)
                if (elist.contains(view.url))
                    return
                if (!reExec) return
                elist.add(view.url!!)
                when (view.url) {
                    "http://isds.kr/sdm/source/LOGIN/login.php" ->
                        view.loadUrl("javascript:(function () { document.getElementsByName(\"UserPW\")[0].value = \"${DataManager.ablrPW}\";document.getElementsByName(\"UserID\")[0].value = \"${DataManager.ablrID}\";document.getElementsByName(\"dormitory_code\")[0].value = \"gbs\";document.getElementsByTagName(\"button\")[0].click()})()");
                    "http://isds.kr/sdm/index.php" ->
                        view.loadUrl("http://isds.kr/sdm/source/SSH/sh_approve_manage.php")
                    "http://isds.kr/sdm/source/SSH/sh_approve_manage.php" -> {
                        reExec = false
                        getHTML(view)
                        CoroutineScope(Dispatchers.Default).launch {
                            while (res == "")
                                delay(1)
                            var doc = Jsoup.parse(res)
                            var ele = doc.getElementsByTag("tr")
                            while (ele.size < 1) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    getHTML(view)
                                    doc = Jsoup.parse(res)
                                    ele = doc.getElementsByTag("tr")
                                }
                                delay(100)
                            }
                            val usqLst = mutableListOf<String>()
                            val rLst = mutableListOf<String>()
                            for (x in ele) {
                                Log.d("ele", x.attr("data-user_seq"))
                                Log.d("ele", "r: ${x.attr("data-r_seq")}")
                                usqLst.add(x.attr("data-user_seq").replace("\\", ""))
                                rLst.add(x.attr("data-r_seq").replace("\\", ""))
                            }
                            for (x in usqLst.indices) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    view.loadUrl(
                                        "javascript:(function () { \$(\"#h_dormitory_code\").val(\"gbs\");\$(\"#h_user_id\").val(\"${DataManager.ablrID}\");" +
                                                "\$(\"#h_user_seq\").val(${usqLst[x]});\$(\"#h_r_seq\").val(${rLst[x]});loadDetailData();loadTableData2();})()"
                                    )
                                    Log.d("js", "javascript:(function () { \$(\"#h_dormitory_code\").val(\"gbs\");\$(\"#h_user_id\").val(\"${DataManager.ablrID}\");" +
                                            "\$(\"#h_user_seq\").val(${usqLst[x]});\$(\"#h_r_seq\").val(${rLst[x]});loadDetailData();loadTableData2();})()")
                                }
                                deleteResult = 0
                                delay(100)
                                CoroutineScope(Dispatchers.Main).launch {
                                    view.loadUrl("javascript:document.getElementById(\"btnDelete\").click()")
                                }
                                delay(1000)
                                while (deleteResult == 0) delay(100)
                            }
                            isFinished = true
                        }
                    }
                }
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                Log.d("asdf", message!!)
                return if (message == "정상적으로 처리되었습니다.") {
                    result?.confirm()
                    deleteResult = 1
                    true
                } else if (message == "상세 정보가 없습니다.") {
                    result?.confirm()
                    deleteResult = -1
                    true
                } else if (message == "undefined") {
                    result?.confirm()
                    deleteResult = -2
                    true
                } else {
                    Toast.makeText(App.appContext, message, Toast.LENGTH_LONG).show()
                    result?.confirm()
                    stopSelf()
                    true
                }
            }
        }
        webView.loadUrl("http://isds.kr")

        var hit = 0
        CoroutineScope(Dispatchers.Default).launch {
            while (!isFinished) delay(100)
            CoroutineScope(Dispatchers.Main).launch {
                val mWebView = BackgroundWebView(this@AblrService)
                mWebView.webViewClient = object : WebViewClient() {}
                reExec = true
                elist = mutableListOf()
                mWebView.webChromeClient = object : WebChromeClient() {
                    override fun onJsAlert(
                        view: WebView?,
                        url: String?,
                        message: String?,
                        result: JsResult?
                    ): Boolean {
                        Log.d("asdf", message!!)
                        return if (message == "정상적으로 처리되었습니다.") {
                            result?.confirm()
                            hit++
                            rigResult = 1
                            true
                        } else {
                            rigResult = -1
                            result?.confirm()
                            Toast.makeText(App.appContext, message, Toast.LENGTH_LONG).show()
                            true
                        }
                    }

                    override fun onJsConfirm(
                        view: WebView?,
                        url: String?,
                        message: String?,
                        result: JsResult?
                    ): Boolean {
                        Log.d("asdf", message!!)
                        result?.confirm()
                        if (message != "해당 내용을 신청하시겠습니까?") {
                            rigResult = -2
                            Toast.makeText(App.appContext, message, Toast.LENGTH_LONG).show()
                        }
                        return true
                    }

                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)

                        if (view!!.url == null)
                            return
                        if (newProgress == 100) {
                            if (elist.contains(view.url))
                                return
                            elist.add(view.url!!)
                            Log.d("asdf", view.url!!)
                            if (!reExec) return
                            when (view.url) {
                                "http://isds.kr/sdm/source/LOGIN/login.php" ->
                                    view.loadUrl("javascript:(function () { document.getElementsByName(\"UserPW\")[0].value = \"${DataManager.ablrPW}\";document.getElementsByName(\"UserID\")[0].value = \"${DataManager.ablrID}\";document.getElementsByName(\"dormitory_code\")[0].value = \"gbs\";document.getElementsByTagName(\"button\")[0].click()})()");
                                "http://isds.kr/sdm/index.php" ->
                                    view.loadUrl("javascript:(function () { document.getElementsByTagName(\"button\")[0].click();document.getElementsByTagName(\"a\")[5].click();document.getElementsByTagName(\"a\")[6].click()})()");
                                "http://isds.kr/sdm/source/SSH/sh_apply_manage.php" -> {
                                    reExec = false
                                    CoroutineScope(Dispatchers.Default).launch {
                                        for (x in ablrData) {
                                            delay(100)
                                            while (rigResult == 0) delay(100)
                                            rigResult = 0
                                            CoroutineScope(Dispatchers.Main).launch {
                                                view.loadUrl("javascript:document.getElementById(\"btnDataAdd\").click()")
                                            }
                                            delay(1000)
                                            CoroutineScope(Dispatchers.Main).launch {
                                                view.loadUrl("javascript:(function () { document.getElementById(\"popup_out_reason\").value = \"${x.locationInfo}\";document.getElementById(\"popup_out_start_time1\").value = \"${x.sth}\";document.getElementById(\"popup_out_start_time2\").value = \"${x.stm}\";document.getElementById(\"popup_out_end_time1\").value = \"${x.eth}\";document.getElementById(\"popup_out_end_time2\").value = \"${x.etm}\";document.getElementById(\"btnConfirmOut\").click()})()");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                mWebView.settings.javaScriptEnabled = true
                mWebView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
                mWebView.loadUrl("http://isds.kr")
                CoroutineScope(Dispatchers.Default).launch {
                    while (hit != DataManager.todayAblrTableData.size) Thread.sleep(100)
                    Log.d("asdf", "stop")
                    stopSelf()
                }
            }
        }
        return START_STICKY
    }

    fun getHTML(view: WebView){
        //res = ""
        view.evaluateJavascript( "(function() { return (document.getElementsByTagName('html')[0].innerHTML); })();") {
            res = it.replace("\\u003C", "<")
            Log.d("asdf", res)
        }
        Thread.sleep(100)
    }

    class BackgroundWebView : WebView {
        constructor(context: Context?) : super(context!!)
        constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
        constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context!!,
            attrs,
            defStyleAttr
        )

        override fun onWindowVisibilityChanged(visibility: Int) {
            if (visibility != View.GONE) super.onWindowVisibilityChanged(View.VISIBLE)
        }
    }
}