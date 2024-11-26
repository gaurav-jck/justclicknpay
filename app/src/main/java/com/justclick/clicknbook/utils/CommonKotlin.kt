package com.justclick.clicknbook.utils

import android.content.Context
import java.net.URL

class CommonKotlin {
    companion object{

        fun getIpAddress(context: Context) : String{
            var ip: String? = null
            val thread = Thread {
                try {
                    val url = URL("https://api.ipify.org")
                    val connection = url.openConnection()
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0/Chrome") // Set a User-Agent to avoid HTTP 403 Forbidden error
                    val inputStream = connection.getInputStream()
                    val s = java.util.Scanner(inputStream, "UTF-8").useDelimiter("\\A")
                    ip = s.next()
                    inputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                    ip= "11.11.0.0"
                }
            }

            thread.start()
            return ip!!
        }
    }
}