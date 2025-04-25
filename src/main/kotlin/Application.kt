package ru.eaglorn

import com.github.luben.zstd.Zstd
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
import ru.eaglorn.Message.ChatMessage

fun main(args: Array<String>) {
    runBlocking {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", 9002)
        println("Server is listening at ${serverSocket.localAddress}")
        while (true) {
            val socket = serverSocket.accept()
            println("Accepted $socket")
            launch {
                val receiveChannel = socket.openReadChannel()
                val sendChannel = socket.openWriteChannel(autoFlush = true)

                val welcomeMessage = ChatMessage.newBuilder()
                    .setName("Server")
                    .setMessage("Please enter your name")
                    .build()

                val compressedWelcome = Zstd.compress(welcomeMessage.toByteArray())
                sendChannel.writeFully(compressedWelcome)

                try {
                    while (true) {
                        val size = receiveChannel.readInt()
                        val compressedData = ByteArray(size)
                        receiveChannel.readFully(compressedData)

                        val decompressedData = Zstd.decompress(compressedData, 1024)
                        val receivedMessage = ChatMessage.parseFrom(decompressedData)

                        println("Received: ${receivedMessage.name}: ${receivedMessage.message}")

                        val responseMessage = ChatMessage.newBuilder()
                            .setName("Server")
                            .setMessage("Hello, ${receivedMessage.name}!")
                            .build()

                        val compressedResponse = Zstd.compress(responseMessage.toByteArray())
                        sendChannel.writeFully(compressedResponse)
                    }
                } catch (e: Throwable) {
                    socket.close()
                }
            }
        }
    }
}
