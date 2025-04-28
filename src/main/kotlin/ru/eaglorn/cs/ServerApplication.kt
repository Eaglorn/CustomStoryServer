package ru.eaglorn.cs

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.readFully
import io.ktor.utils.io.readInt
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import ru.eaglorn.Message
import ru.eaglorn.Utils.ZstdHelper

@SpringBootApplication(scanBasePackages = ["ru.eaglorn.cs"])
open class Application {
    @Suppress("unused")
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    init {
        runBlocking {
            val selectorManager = SelectorManager(Dispatchers.IO)
            val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", 9002)
            while (true) {
                val socket = serverSocket.accept()
                launch {
                    val receiveChannel = socket.openReadChannel()
                    val sendChannel = socket.openWriteChannel(autoFlush = true)

                    val welcomeMessage = Message.ChatMessage.newBuilder()
                        .setName("Server")
                        .setMessage("Please enter your name")
                        .build()

                    sendChannel.writeFully(ZstdHelper.compress(welcomeMessage.toByteArray()))

                    try {
                        while (true) {
                            val size = receiveChannel.readInt()
                            val compressedData = ByteArray(size)
                            receiveChannel.readFully(compressedData)

                            val decompressedData = ZstdHelper.decompress(compressedData)

                            processMessage(decompressedData)
                        }
                    } catch (e: Throwable) {
                        println(e.message)
                        socket.close()
                    }
                }
            }
        }
    }

    fun processMessage(data: ByteArray) {
        val wrapper = Message.Wrapper.parseFrom(data)

        when (val payload = wrapper.payloadCase) {
            Message.Wrapper.PayloadCase.CHATMESSAGE -> handleUser(wrapper.chatMessage)
            else -> throw IllegalStateException("Unknown type: $payload")
        }
    }

    fun handleUser (wrapper: Message.ChatMessage) {
        println("Received: ${wrapper.name}: ${wrapper.message}")
    }

    companion object {
        lateinit var applicationContext : ApplicationContext
    }
}

val logger : Logger = LoggerFactory.getLogger("Main")

fun main() {
    Application.Companion.applicationContext = runApplication<Application>()
}