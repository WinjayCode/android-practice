package com.winjay.practice.test_kotlin

import java.lang.IllegalArgumentException

fun main() {
//    print(sum(1, 2))

//    printSum(3, 2)

//    vars(1, 2, 3, 4, 5)

    // lambda表达式
//    val sumLambda: (Int, Int) -> Int = { x, y -> x + y }
//    print(sumLambda(1, 2))

//    var c: Int
//    c = 1

//    var a = 1
//    val s1 = "a is $a"
//    a = 2
//    val s2 = "${s1.replace("is", "was")}, but now is $a"
//    print(s2)

    // 输出“1234”
//    for (i in 1..4)
//        println(i)


    // 什么都不输出
//    for (i in 4..1)
//        println(i)

    // 输出“4321”
//    for (i in 4 downTo 1)
//        println(i)

    // 等同于 1 <= i && i <= 10
//    val i = 3
//    if (i in 1..10) {
//        println(i)
//    }

    // 使用 step 指定步长(一次走2步)
//    for (i in 1..4 step 2)
//        println(i)

    // downTo降序
//    for (i in 4 downTo 1 step 2)
//        println(i)

    // 使用 until 函数排除结束元素
//    for (i in 1 until 10)
//        println(i)

    // true，值相等，对象地址相等
//    val a: Int = 10000
//    println(a === a)
//
//    val boxedA: Int? = a
//    val anotherBoxedA: Int? = a
//    println(boxedA === anotherBoxedA) // a=100时为true,a=10000是为false，因为Integer的装箱操作的内部处理逻辑范围为[-128, 127]
//    println(boxedA == anotherBoxedA)

    // 数组
//    val a = arrayOf(1, 2, 3)
//    val b = Array(3, { i -> (i * 2) })
//    println(a[0])
//    println(b[1])
//    val x: IntArray = intArrayOf(1, 2, 3)
//    x[0] = x[1] + x[2]
//    println(x[0])

    // 遍历字符串
//    val str = "hello"
//    for (c in str) {
//        println(c)
//    }

    val text = """
        @UnityMessage@{
      "id": 1,
      "seq": "",
      "name": "Test",
      "method": "init",
      "data": null
    }
        """
    println(text.replace("@UnityMessage@", ""))
}

/**
 * 返回Int类型
 */
fun sum(a: Int, b: Int): Int {
    return a + b
}

/**
 * 返回void类型
 */
public fun printSum(a: Int, b: Int) {
    print(a + b)
}

/**
 * 可变长参数
 */
fun vars(vararg v: Int) {
    for (vt in v) {
        print(vt)
    }
}

val a: Int = 1
val b = 1

var x = 5

// 类型后面加?表示可为空
var age: String? = "23"

//抛出空指针异常
val ages = age!!.toInt()

//不做处理返回 null
val ages1 = age?.toInt()

//age为空返回-1
val ages2 = age?.toInt() ?: -1

fun getStringLength(obj: Any): Int? {
    if (obj is String) {
        // 做过类型判断以后，obj会被系统自动转换为String类型
        return obj.length
    }

    //在这里还有一种方法，与Java中instanceof不同，使用!is
    // if (obj !is String){
    //   // XXX
    // }

    // 这里的obj仍然是Any类型的引用
    return null
}

fun getStringLength2(obj: Any): Int? {
    if (obj !is String)
        return null
    // 在这个分支中, `obj` 的类型会被自动转换为 `String`
    return obj.length
}

fun getStringLength3(obj: Any): Int? {
    // 在 `&&` 运算符的右侧, `obj` 的类型会被自动转换为 `String`
    if (obj is String && obj.length > 0)
        return obj.length
    return null
}

/**
 * 显式把字符转换为 Int 数字
 */
fun decimalDigitValue(c: Char): Int {
    if (c !in '0'..'9')
        throw IllegalArgumentException("Out of range")
    return c.toInt() - '0'.toInt()
}