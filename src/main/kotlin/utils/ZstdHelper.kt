package ru.eaglorn.Utils

import com.github.luben.zstd.Zstd

object ZstdHelper {
    private const val COMPRESSION_LEVEL = 3 //1-22

    fun compress(data: ByteArray): ByteArray {
        return Zstd.compress(data, COMPRESSION_LEVEL)
    }

    fun decompress(compressedData: ByteArray): ByteArray {
        return Zstd.decompress(compressedData, Zstd.getFrameContentSize(compressedData).toInt())
    }
}