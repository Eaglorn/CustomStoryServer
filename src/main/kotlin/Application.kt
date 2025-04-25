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
import ru.eaglorn.Message.Wrapper
import ru.eaglorn.Utils.ZstdHelper

fun processMessage(data: ByteArray) {
    val wrapper = Wrapper.parseFrom(data)

    when (val payload = wrapper.payloadCase) {
        Wrapper.PayloadCase.CHATMESSAGE -> handleUser(wrapper.chatMessage)
        else -> throw IllegalStateException("Unknown type: $payload")
    }
}

fun handleUser (wrapper: ChatMessage) {
    println("Received: ${wrapper.name}: ${wrapper.message}")
}

fun main() {
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
