package com.udstu.fraxinus.common.util

import java.security.*
import java.util.*

fun String.encodeMD5(): String {
    val instance = MessageDigest.getInstance("MD5")
    val digest = instance.digest(this.toByteArray())

    val stringBuffer = StringBuffer()

    digest.forEach {
        var hexString = Integer.toHexString(it.toInt() and 0xff)
        if (hexString.length < 2) {
            hexString = "0$hexString"
        }
        stringBuffer.append(hexString)
    }

    return stringBuffer.toString()
}

fun randomUnsignedUUID(): String {
    return UUID.randomUUID().toString().replace("-", "")
}
