package com.winjay.practice.kotlin

/**
 *
 *
 * @author Winjay
 * @date 2025-07-17
 */
fun main() {
    println("hello kotlin!")

    // 1_000_000 和 1000000 相同
    var a = 1_000_000
    println(a)

    // 十六进制表示
    var b = 0xAF
    // 0x0b开头代表二进制
    var c = 0x0b1001
    // UInt开头代表无符号类型，数字结尾带 u 标志
    var d: UInt = 1u
}

// 二进制
// 1111 = 2^3 + 2^2 + 2^1 + 2^0 = 8 + 4 + 2 + 1 = 15
// 1101 = 2^3 + 2^2 + 0 + 2^0 = 8 + 4 + 0 + 1 = 13