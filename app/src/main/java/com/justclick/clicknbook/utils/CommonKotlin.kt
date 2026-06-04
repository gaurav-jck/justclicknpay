package com.justclick.clicknbook.utils

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.Scanner

class CommonKotlin {
    companion object{

        fun getIpAddress() : String{
            var ip: String? = null
            val thread = Thread {
                try {
                    val url = URL("https://api.ipify.org")
                    val connection = url.openConnection()
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0/Chrome") // Set a User-Agent to avoid HTTP 403 Forbidden error
                    val inputStream = connection.getInputStream()
                    val s = Scanner(inputStream, "UTF-8").useDelimiter("\\A")
                    ip = s.next()
                    inputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                    ip= "103.139.75.200"
                }
            }
            thread.start()
            return ip!!
        }

        fun fetchPublicIP() :String? {
            var publicIpp: String? = null
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val url = URL("https://api.ipify.org")
                    val connection = url.openConnection()
                    // Set User-Agent to avoid potential 403 Forbidden errors
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0")

                    val inputStream = connection.getInputStream()
                    val scanner = Scanner(inputStream, "UTF-8").useDelimiter("\\A")
                    val publicIp = if (scanner.hasNext()) scanner.next() else ""

                    withContext(Dispatchers.Main) {
                        // Use publicIp in your UI here
                        println("Your Public IP: $publicIp")
                        publicIpp=publicIp
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    publicIpp= "103.139.75.200"
                }
            }
            return publicIpp
        }

    }
}