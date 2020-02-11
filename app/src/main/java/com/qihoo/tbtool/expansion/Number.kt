package com.mm.red.expansion

import java.math.BigDecimal

fun BigDecimal.to2(): BigDecimal {
    return setScale(2, BigDecimal.ROUND_HALF_UP)
}


fun BigDecimal.trimZero(): String {
    var s = toString()
    if (s.indexOf(".") > 0) {
        // 去掉多余的0
        s = s.replace("0+?$".toRegex(), "")
        // 如最后一位是.则去掉
        s = s.replace("[.]$".toRegex(), "")
    }
    return s
}

fun Number.fillZero(): String {
    return if (toLong() > 9) {
        toString()
    } else {
        "0${toShort()}"
    }
}
