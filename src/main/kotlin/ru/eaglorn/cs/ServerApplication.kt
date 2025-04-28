package ru.eaglorn.cs

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext

@SpringBootApplication(scanBasePackages = ["ru.eaglorn.cs"])
open class Application {
    init {
        runBlocking {
            val selectorManager = SelectorManager(Dispatchers.IO)
            val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", 9002)
            while (true) {
                val socket = serverSocket.accept()
                launch {
                    val receiveChannel = socket.openReadChannel()
                    val buffer = ByteArray(1024)
                    val bytesRead = receiveChannel.readAvailable(buffer)
                    if (bytesRead > 0) {
                        val message = String(buffer, 0, bytesRead)
                        println("Client: $message")
                    }
                    socket.close()
                }
            }
        }
    }

    companion object {
        lateinit var applicationContext: ApplicationContext
    }
}

fun main() {
    Application.Companion.applicationContext = runApplication<Application>()
}
