package org.whatever

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import io.github.cdimascio.dotenv.dotenv
import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.URI
import java.net.URISyntaxException
import java.net.URLConnection

val dotenv = dotenv()
val port = dotenv["PORT_OVERRIDE"]?.toIntOrNull() ?: 7272
val defaultUrl = dotenv["INITIAL_PATH"] ?: "index.html"

fun main() {
    //server setup and endpoint definitions
    val server = HttpServer.create(InetSocketAddress(port), 0)
    server.createContext("/\$env/", EnvGet())
    server.createContext("/", FilesystemRedirectHandler())
    server.executor = null
    server.start()
    println("Server running on port $port")
    println("Opening $defaultUrl")
    Browser.browse("http://localhost:$port/$defaultUrl")
}

//main server endpoint, just serves files
class FilesystemRedirectHandler : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        try {
            //CORS stuff
            if (exchange.requestMethod == "OPTIONS") {
                exchange.responseHeaders.set("Access-Control-Allow-Origin", "*")
                exchange.responseHeaders.set("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                exchange.responseHeaders.set("Access-Control-Allow-Headers", "Content-Type")
                exchange.sendResponseHeaders(200, 0)
                exchange.responseBody.close()
                return
            }

            val path = exchange.requestURI.path!!
            val file = File(check("./${path.removePrefix("/")}"))

            if (!file.exists()) {
                println("File not found: $path")
                exchange.sendResponseHeaders(404, 0)
                exchange.responseBody.close()
                return
            }

            println("Returning file $path")

            val fileStream = file.inputStream()

            val responseBody = exchange.responseBody
            exchange.responseHeaders.set("Access-Control-Allow-Origin", "*")
            exchange.responseHeaders.set("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
            exchange.responseHeaders.set("Access-Control-Allow-Headers", "Content-Type")
            exchange.responseHeaders.set("Content-Type", URLConnection.guessContentTypeFromName(file.name) ?: "")
            exchange.sendResponseHeaders(200, file.length())

            fileStream.copyTo(responseBody)
            responseBody.close()
            fileStream.close()

        } catch (e: NullPointerException) {
            e.printStackTrace()
            exchange.sendResponseHeaders(500, 0)
            exchange.responseBody.close()
        } catch (e: InsecurePathException) {
            exchange.sendResponseHeaders(400, 0)
            exchange.responseBody.close()
        }
    }

    //some sort of security check? Just putting it in there
    private fun check(path: String): String {
        if (path.split("/../").size > 1) {
            throw InsecurePathException()
        }
        return path
    }
}

class InsecurePathException : Exception()

//environment variable endpoint, designed to be used to get the URL for the Neuro API websocket
class EnvGet : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        try {
            //more CORS stuff
            if (exchange.requestMethod == "OPTIONS") {
                exchange.responseHeaders.set("Access-Control-Allow-Origin", "*")
                exchange.responseHeaders.set("Access-Control-Allow-Methods", "GET,POST,OPTIONS")
                exchange.responseHeaders.set("Access-Control-Allow-Headers", "Content-Type")
                exchange.sendResponseHeaders(200, 0)
                exchange.responseBody.close()
                return
            }

            val path = exchange.requestURI.path!!

            val envResult = System.getenv(path.removePrefix("/\$env/"))

            if (envResult == null) {
                println("Environment variable not found: ${path.removePrefix("/\$env/")}")
                exchange.sendResponseHeaders(404, 0)
                exchange.responseBody.close()
                return
            }

            exchange.responseHeaders.set("Access-Control-Allow-Origin", "*")
            exchange.sendResponseHeaders(200, envResult.length.toLong())
            exchange.responseBody.write(envResult.toByteArray())
            exchange.responseBody.close()

        } catch (e: NullPointerException) {
            e.printStackTrace()
            exchange.sendResponseHeaders(500, 0)
            exchange.responseBody.close()
        }
    }
}

//simple way to open the initial file in the browser automatically so that it doesn't have to be manually done
object Browser {
    @JvmStatic
    fun browse(url: String) {
        if (Desktop.isDesktopSupported()) {
            val desktop = Desktop.getDesktop()
            try {
                desktop.browse(URI(url))
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        } else {
            val runtime = Runtime.getRuntime()
            try {
                runtime.exec("xdg-open $url")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}