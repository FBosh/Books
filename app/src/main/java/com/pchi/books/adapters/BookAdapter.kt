package com.pchi.books.adapters

import com.pchi.books.models.Illus
import com.pchi.books.models.Page
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.URL

class BookAdapter : BaseAdapter() {
    var latestPage = Page.defaultPage
        private set

    private var reconnectCount = 0

    var latestBookURL = ""
        private set

    fun connectHTTP(path: String, listener: OnConnectListener): Boolean {
        var result: String

        try {
            URL(path)
        } catch (e: MalformedURLException) {
            e.printStackTrace()

            result = "Malformed URL!!"
            listener.onFail(result)

            printBoshLog("path= $path")

            return false
        }

        Thread {
            (URL(path).openConnection() as HttpURLConnection).apply {
                try {
                    connectTimeout = 3000
                    printBoshLog("url= $url")
                    printBoshLog("connectTimeout= $connectTimeout")
                    printBoshLog("responseCode= $responseCode")

                    connect()

                    result = inputStream.bufferedReader().readText()

                    disconnect()

                    printBoshLog("result= $result")

                    if (result.contains("result\":1")) {
                        (JSONObject(result).get("data") as JSONObject).also { jso ->
                            val illustrations = mutableListOf<Illus>().also { list ->
                                (jso.get("content") as JSONArray).also { arrJS ->
                                    for (i in 0 until arrJS.length()) {
                                        (arrJS[i] as JSONObject).also { o ->
                                            list.add(Illus(
                                                    o.get("no").toString().toInt(),
                                                    o.get("url").toString()
                                            ))
                                        }
                                    }
                                }
                            }

                            latestPage = Page(
                                    jso.get("no").toString().toInt(),
                                    jso.get("size").toString().toInt(),
                                    jso.get("totol").toString().toInt(),
                                    jso.get("pages").toString().toInt(),
                                    illustrations,
                                    jso.get("edition").toString()
                            )

                            if (path.contains("list") && path.endsWith('/')) {
                                latestBookURL = path
                            }
                        }
                    } else if (result.contains("result\":0")) {
                        latestPage = Page.defaultPage
                        latestBookURL = ""
                    }

                    printBoshLog("latestPage= $latestPage")

                    listener.onComplete(result)
                } catch (e: Exception) {
                    e.printStackTrace()

                    result = when (e) {
                        is SocketTimeoutException -> "Timeout (${connectTimeout / 1000} seconds)!!"
                        is FileNotFoundException -> "File not found!!"
                        else -> "Unknown failure!!"
                    }

                    if (result == "File not found!!" && reconnectCount < 3) {
                        reconnectCount++
                        connectHTTP(path, listener)
                    } else if (reconnectCount >= 3) {
                        reconnectCount = 0
                    }

                    printBoshLog("reconnectCount= $reconnectCount")

                    listener.onFail(result)
                }
            }
        }.start()

        return true
    }

    interface OnConnectListener {
        fun onComplete(msg: String)
        fun onFail(msg: String)
    }
}
