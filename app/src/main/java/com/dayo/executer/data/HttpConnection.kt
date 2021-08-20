package com.dayo.executer.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HttpConnection {
    companion object {
        suspend fun DownloadString(urlString: String): String = withContext(Dispatchers.IO) {
            var returnText: String? = null
            var url: URL? = null
            var conn: HttpURLConnection? = null
            var jsonData: String? = ""
            var br: BufferedReader? = null
            var sb: StringBuffer? = null

            try {
                url = URL(urlString)
                conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("Accept", "application/json")
                conn.requestMethod = "GET"
                conn.connect()
                br = BufferedReader(InputStreamReader(conn.getInputStream(), "UTF-8"))
                sb = StringBuffer()
                while (br.readLine().also { jsonData = it } != null) {
                    sb.append(jsonData)
                }
                returnText = sb.toString()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    br?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            while (returnText == null) {}
            return@withContext returnText.toString()
        }
        fun PreParseJson(json: String): String = json.removePrefix("\"").removeSuffix("\"").replace("\\\"", "\"")
    }
}