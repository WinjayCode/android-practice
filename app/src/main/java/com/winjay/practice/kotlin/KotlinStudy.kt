package com.winjay.practice.kotlin

import android.content.Context
import android.util.AttributeSet
import android.view.View
import org.junit.Test
import java.io.FileWriter
import java.lang.IllegalArgumentException

fun main() {
//    println("Hello world!")

//    println("Enter any word: ") // 打印请求输入的提示信息
//    val yourWord = readln() // 读取并存储用户输入。例如：Happiness
//    print("You entered the word: ") // 打印包含输入内容的提示信息
//    print(yourWord) // You entered the word: Happiness

//    print(sum(1, 2))

//    println(sum2(2, 3))

//    printSum(3, 2)

//    vars(1, 2, 3, 4, 5)

    // lambda表达式
//    val sumLambda: (Int, Int) -> Int = { x, y -> x + y }
//    print(sumLambda(1, 2))

//    var c: Int // 使用 var 关键字声明可以重新赋值的变量。这些是可变变量，你可以在初始化后更改它们的值
//    c = 1


//    val x = 5 // 在声明时初始化变量 x；无需指定类型
//    val c: Int // 声明变量 c 但不初始化；需要指定类型
//    c = 3 // 在声明后初始化变量 c

//    val x = 5 // 声明变量 x 并将其初始化为值 5；`Int` 类型被推断

//    var a = 1
//    val s1 = "a is $a" // 使用 val 关键字声明只赋值一次的变量。这些是不可变的、只读的局部变量，初始化后不能重新赋值为不同的值
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

    // == 用于比较两个对象的值是否相等。对于基本类型（如 int、String 等），它会直接比较值的相等性。对于对象，它会比较对象的引用地址是否相同，但这种情况下通常不建议使用。
    // === 专门用于比较 引用 相等，即它们指向同一个对象在内存中的位置。

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

    // 字符替换
//    val text = """
//        @UnityMessage@{
//      "id": 1,
//      "seq": "",
//      "name": "Test",
//      "method": "init",
//      "data": null
//    }
//        """
//    println(text.replace("@UnityMessage@", ""))
}

/**
 * 返回Int类型
 */
fun sum(a: Int, b: Int): Int {
    return a + b
}

/**
 * 函数体可以是表达式。
 */
fun sum2(a: Int, b: Int) = a + b

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

// 可空值与 null 检测
// 当可能存在 null 值时，引用必须显式标记为可空。可空类型名称的末尾带有 ?
// 类型后面加?表示可为空
var age: String? = "23"

//抛出空指针异常
val ages = age!!.toInt()

//不做处理返回 null
val ages1 = age?.toInt()

//age为空返回-1
val ages2 = age?.toInt() ?: -1

// 类型检测与自动类型转换
// is 操作符检测表达式是否是某个类型的实例。 如果对不可变局部变量或属性进行了特定类型检测，则无需显式转换它
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
    return c.code - '0'.code
}

// 要定义一个类，请使用 class 关键字
class Shape

// 类的属性可以列在其声明或类体中
class Rectangle(val height: Double, val length: Double) {
    val perimeter = (height + length) * 2
}

// 类之间的继承通过冒号 (:) 声明。类默认是 final 的；要使一个类可继承，请将其标记为 open
open class Shape2

class Rectangle2(val height: Double, val length: Double): Shape2() {
    val perimeter = (height + length) * 2
}

// 字符串模板
fun str_() {
    var a = 1
    // 模板中的简单名称：
    val s1 = "a is $a"

    a = 2
    // 模板中的任意表达式：
    val s2 = "${s1.replace("is", "was")}, but now is $a"
    println(s2)
}

// 条件表达式
fun maxOf(a: Int, b: Int): Int {
    if (a > b) {
        return a
    } else {
        return b
    }
}

// 在 Kotlin 中，if 也可以用作表达式
fun maxOf2(a: Int, b: Int) = if (a > b) a else b

// for 循环
fun for_() {
    val items = listOf("apple", "banana", "kiwifruit")
    for (item in items) {
        println(item)
    }
}
// 或者
fun for_2() {
    val items = listOf("apple", "banana", "kiwifruit")
    for (index in items.indices) {
        println("item at $index is ${items[index]}")
    }
}

// while 循环
fun while_() {
    val items = listOf("apple", "banana", "kiwifruit")
    var index = 0
    while (index < items.size) {
        println("item at $index is ${items[index]}")
        index++
    }
}

// when 表达式
fun when_(obj: Any): String =
    when (obj) {
        1          -> "One"
        "Hello"    -> "Greeting"
        is Long    -> "Long"
        !is String -> "Not a string"
        else       -> "Unknown"
    }

// 区间
// 使用 in 操作符检测数字是否在区间内
fun in_() {
    val x = 10
    val y = 9
    if (x in 1 .. y+1) {
        println("fits in range")
    }
}
// 检测数字是否超出区间
fun in_2() {
    val list = listOf("a", "b", "c")

    if (-1 !in 0..list.lastIndex) {
        println("-1 is out of range")
    }
    if (list.size !in list.indices) {
        println("list size is out of valid list indices range, too")
    }
}
// 迭代区间
fun in_3() {
    for (x in 1..5) {
        print(x)
    }
}
// 或迭代数列
fun in_4() {
    for (x in 1..10 step 2) {
        print(x)
    }
    println()
    for (x in 9 downTo 0 step 3) {
        print(x)
    }
}

// 集合
// 迭代集合
fun listof_() {
    val items = listOf("apple", "banana", "kiwifruit")
    for (item in items) {
        println(item)
    }
}
// 使用 in 操作符检测集合是否包含对象
fun listof_2() {
    val items = setOf("apple", "banana", "kiwifruit")
    when {
        "orange" in items -> println("juicy")
        "apple" in items -> println("apple is fine too")
    }
}
// 使用 lambda 表达式 过滤和映射集合
fun listof_3() {
    val fruits = listOf("banana", "avocado", "apple", "kiwifruit")
    fruits
        .filter { it.startsWith("a") }
        .sortedBy { it }
        .map { it.uppercase() }
        .forEach { println(it) }
}

// 位操作列表：
// shl(bits) – 有符号左移
// shr(bits) – 有符号右移
// ushr(bits) – 无符号右移
// and(bits) – 按位 与
// or(bits) – 按位 或
// xor(bits) – 按位 异或
// inv() – 按位反转


// || 和 && 操作符以惰性方式工作，这意味着：
// 如果第一个操作数为 true，|| 操作符不会对第二个操作数求值。
// 如果第一个操作数为 false，&& 操作符不会对第二个操作数求值。


// 特殊字符以转义反斜杠 \ 开头。 支持以下转义序列：
//
// \t – 制表符
// \b – 退格符
// – 换行符 (LF)
// \r – 回车符 (CR)
// \' – 单引号
// \" – 双引号
// \\ – 反斜杠
// \# – 美元符号


// 字符串
// 字符串的元素是字符，你可以通过索引操作 s[i] 访问它们。 你可以使用 for 循环迭代这些字符：
fun main_str() {
    val str = "abcd"
    for (c in str) {
        println(c)
    }
}
// 要连接字符串，请使用 + 操作符。只要表达式中的第一个元素是字符串，此操作符也适用于连接字符串与其他类型的值：
fun main_str2() {
    val s = "abc" + 1
    println(s + "def")
    // abc1def
}
// 字符串字面值
// Kotlin 有两种类型的字符串字面值：
// 转义字符串
// 多行字符串

// 转义字符串
// 转义字符串可以包含转义字符。 这是一个转义字符串的例如：
// val s = "Hello, world!
// "
// 转义以常规方式进行，使用反斜杠 (\)。

// 多行字符串
// 多行字符串可以包含换行符和任意文本。它由三引号 (""") 分隔，不包含转义，并且可以包含换行符和任何其他字符：
val text = """
    for (c in "foo")
        print(c)
    """
// 要移除多行字符串中的前导空白，请使用 trimMargin() 函数：
val text2 = """
    |Tell me and I forget.
    |Teach me and I remember.
    |Involve me and I learn.
    |(Benjamin Franklin)
    """.trimMargin()
// 默认情况下，管道符号 | 用作边距前缀，但你可以选择另一个字符并将其作为形参传入，例如 trimMargin(">")。

// 字符串模板
//字符串字面值可以包含模板表达式——一些被求值并将其结果连接到字符串中的代码片段。
//当处理模板表达式时，Kotlin 会自动调用表达式结果上的 .toString() 函数，将其转换为字符串。模板表达式以美元符号 ($) 开头，并由变量名组成：
fun main_str3() {
    val i = 10
    println("i = $i")
    // i = 10

    val letters = listOf("a","b","c","d","e")
    println("Letters: $letters")
    // Letters: [a, b, c, d, e]
}
// 或花括号中的表达式：
fun main_str4() {
    val s = "abc"
    println("$s.length is ${s.length}")
    // abc.length is 3
}
// 你可以在多行字符串和转义字符串中使用模板。但是，多行字符串不支持反斜杠转义。 要在多行字符串中，在 标识符 开头允许的任何符号之前插入美元符号 ($) 作为字面字符， 请使用以下语法：
val price = """
${'$'}9.99
"""

// 字符串格式化
// 要根据你的特定要求格式化字符串，请使用 String.format() 函数。
//
// String.format() 函数接受一个格式字符串和一个或多个实参。格式字符串包含一个给定实参的占位符（由 % 指示），后跟格式说明符。
// 格式说明符是相应实参的格式化指令，由标志、宽度、精度和转换类型组成。总的来说，格式说明符决定了输出的格式。
// 常见的格式说明符包括用于整数的 %d、用于浮点数的 %f 和用于字符串的 %s。你还可以使用 argument_index$ 语法 在格式字符串中以不同格式多次引用同一个实参。
// 让我们看一个例如：
fun main_str5() {
    // Formats an integer, adding leading zeroes to reach a length of seven characters
    val integerNumber = String.format("%07d", 31416)
    println(integerNumber)
    // 0031416

    // Formats a floating-point number to display with a + sign and four decimal places
    val floatNumber = String.format("%+.4f", 3.141592)
    println(floatNumber)
    // +3.1416

    // Formats two strings to uppercase, each taking one placeholder
    val helloString = String.format("%S %S", "hello", "world")
    println(helloString)
    // HELLO WORLD

    // Formats a negative number to be enclosed in parentheses, then repeats the same number in a different format (without parentheses) using `argument_index$` syntax.
//    val negativeNumberInParentheses = String.format("%(d means %1$d", -31416)
//    println(negativeNumberInParentheses)
    //(31416) means -31416
}


// 与数组相比，集合具有以下优势：
// 集合可以是只读的，这为你提供了更多控制权，并允许你编写意图清晰的健壮代码。
// 集合易于添加或移除元素。相比之下，数组的大小是固定的。从数组中添加或移除元素的唯一方法是每次都创建一个新数组，这效率非常低
// 你可以使用相等操作符 (==) 来检测集合是否结构相等。你不能将此操作符用于数组。
fun main_array() {
    // 数组
    var riversArray = arrayOf("Nile", "Amazon", "Yangtze")

    // 使用 += 赋值操作会创建一个新的 riversArray，
    // 复制原有元素并添加 "Mississippi"
    riversArray += "Mississippi"
    println(riversArray.joinToString())
    // Nile, Amazon, Yangtze, Mississippi
}

// 创建数组
// 例如 arrayOf()、arrayOfNulls()(函数创建一个给定大小且填充有 null 元素的数组) 或 emptyArray()(函数创建一个空数组)
// Array 构造函数接受数组大小和一个函数，该函数根据给定索引返回数组元素的值
fun main_array2() {
    // 创建一个 Array<Int>，并用零初始化为 [0, 0, 0]
    val initArray = Array<Int>(3) { 0 }
    println(initArray.joinToString())
    // 0, 0, 0

    // 创建一个包含值 ["0", "1", "4", "9", "16"] 的 Array<String>
    val asc = Array(5) { i -> (i * i).toString() }
    asc.forEach { print(it) }
    // 014916
}

// 嵌套数组
// 数组可以相互嵌套以创建多维数组
fun main_array3() {
    // 创建一个二维数组
    val twoDArray = Array(2) { Array<Int>(2) { 0 } }
    println(twoDArray.contentDeepToString())
    // [[0, 0], [0, 0]]

    // 创建一个三维数组
    val threeDArray = Array(3) { Array(3) { Array<Int>(3) { 0 } } }
    println(threeDArray.contentDeepToString())
    // [[[0, 0, 0], [0, 0, 0], [0, 0, 0]], [[0, 0, 0], [0, 0, 0], [0, 0, 0]], [[0, 0, 0], [0, 0, 0], [0, 0, 0]]]
}

// 比较数组
// 要比较两个数组是否包含相同且顺序一致的元素，请使用 .contentEquals() 和 .contentDeepEquals() 函数
fun main_array4() {
    val simpleArray = arrayOf(1, 2, 3)
    val anotherArray = arrayOf(1, 2, 3)

    // 比较数组内容
    println(simpleArray.contentEquals(anotherArray))
    // true

    // 使用中缀表示法，在元素更改后比较数组内容
    simpleArray[0] = 10
    println(simpleArray contentEquals anotherArray)
    // false
}
// "不要使用相等 (==) 和不等 (!=)操作符来比较数组内容。这些操作符检测被赋值的变量是否指向同一个对象。"

// 求和
// 要返回数组中所有元素的总和，请使用 .sum() 函数
fun main_array5() {
    val sumArray = arrayOf(1, 2, 3)

    // 对数组元素求和
    println(sumArray.sum())
    // 6
}

// 乱序
//要随机打乱数组中的元素，请使用 .shuffle() 函数
fun main_array6() {
    val simpleArray = arrayOf(1, 2, 3)

    // 打乱元素 [3, 2, 1]
    simpleArray.shuffle()
    println(simpleArray.joinToString())

    // 再次打乱元素 [2, 3, 1]
    simpleArray.shuffle()
    println(simpleArray.joinToString())
}

// 转换为 List 或 Set
// 要将数组转换为 List 或 Set，请使用 .toList() 和 .toSet() 函数。
fun main_array7() {
    val simpleArray = arrayOf("a", "b", "c", "c")

    // 转换为 Set
    println(simpleArray.toSet())
    // [a, b, c]

    // 转换为 List
    println(simpleArray.toList())
    // [a, b, c, c]
}
// 转换为 Map
// 要将数组转换为 Map，请使用 .toMap() 函数。
// 只有 Pair<K,V> 数组可以转换为 Map。Pair 实例的第一个值成为键，第二个值成为值。此示例使用中缀表示法调用 to 函数来创建 Pair 元组
fun main_array8() {
    val pairArray = arrayOf("apple" to 120, "banana" to 150, "cherry" to 90, "apple" to 140)

    // 转换为 Map
    // 键是水果，值是其卡路里数量
    // 请注意，键必须是唯一的，因此 "apple" 的最新值会覆盖第一个值
    println(pairArray.toMap())
    // {apple=140, banana=150, cherry=90}
}

// 原生类型数组
//如果将 Array 类与原生类型值一起使用，这些值会被装箱为对象。作为替代方案，你可以使用原生类型数组，它们允许你在数组中存储原生类型，而不会产生装箱开销的副作用
// 原生类型数组	Java 中的等效类型
// BooleanArray	 boolean[]
// ByteArray	 byte[]
// CharArray	 char[]
// DoubleArray	 double[]
// FloatArray	 float[]
// IntArray	     int[]
// LongArray	 long[]
// ShortArray	 short[]
fun main_array9() {
    // 创建一个大小为 5 的 Int 数组，值初始化为零
    val exampleArray = IntArray(5)
    println(exampleArray.joinToString())
    // 0, 0, 0, 0, 0
}


// 类型检测与类型转换
// is 与 !is 操作符
// 要执行运行时检测，以判断对象是否符合给定类型，请使用 is 操作符或其否定形式 !is
fun main_is() {
    val obj = ""
    if (obj is String) {
        print(obj.length)
    }

    if (obj !is String) { // Same as !(obj is String)
        print("Not a String")
    } else {
        print(obj.length)
    }
}

// 智能类型转换
//在大多数情况下，你不需要使用显式类型转换操作符，因为编译器会自动为你进行对象类型转换。这被称为智能类型转换。编译器会跟踪不可变值的类型检测和显式类型转换，并在必要时自动插入隐式（安全）类型转换
fun main_is2(x: Any) {
    if (x is String) {
        print(x.length) // x 被自动转换为 String
    }
}
// 如果一个否定检测导致返回，编译器甚至足够智能，能够识别这种类型转换是安全的
fun main_is3(x: Any) {
    if (x !is String) return
    print(x.length) // x 被自动转换为 String
}

// 控制流
// 智能类型转换不仅适用于 if 条件表达式，也适用于when 表达式和while 循环
fun main_is4(x: Any) {
    when (x) {
        is Int -> print(x + 1)
        is String -> print(x.length + 1)
        is IntArray -> print(x.sum())
    }
}

// 如果你在使用 if、when 或 while 条件之前声明了一个 Boolean 类型的变量，那么编译器收集到的关于该变量的任何信息都将在对应的代码块中可访问，用于智能类型转换。
// 当你希望将布尔条件提取到变量中时，这会很有用。然后，你可以给变量一个有意义的名称，这将提高你代码的可读性，并使其能够在代码中稍后重用。例如
class Cat {
    fun purr() {
        println("Purr purr")
    }
}

fun petAnimal(animal: Any) {
    val isCat = animal is Cat
    if (isCat) {
        // 编译器可以访问关于
        // isCat 的信息，因此它知道 animal 被智能类型转换
        // 为 Cat 类型。
        // 因此，可以调用 purr() 函数。
//        animal.purr()
    }
}

fun main_is5(){
    val kitty = Cat()
    petAnimal(kitty)
    // Purr purr
}

// 逻辑操作符
// 如果 && 或 || 操作符的左侧存在类型检测（常规或否定），编译器可以对右侧执行智能类型转换：
fun main_is6(x: Any) {
    // x 被自动转换为 String，在 || 的右侧
    if (x !is String || x.length == 0) return

    // x 被自动转换为 String，在 && 的右侧
    if (x is String && x.length > 0) {
        print(x.length) // x 被自动转换为 String
    }
}
// 如果你使用 or 操作符（||）结合对象的类型检测，会进行智能类型转换到它们最近的公共超类型：
interface Status {
    fun signal() {}
}

interface Ok : Status
interface Postponed : Status
interface Declined : Status

fun signalCheck(signalStatus: Any) {
    if (signalStatus is Postponed || signalStatus is Declined) {
        // signalStatus 被智能类型转换到公共超类型 Status
//        signalStatus.signal()
    }
}

// 不安全”类型转换操作符
// 要将对象显式类型转换为非空类型，请使用不安全类型转换操作符 as：
// val x: String = y as String
// 如果类型转换不可能，编译器会抛出异常。这就是它被称为不安全的原因。

// 安全”（可空）类型转换操作符
// 为了避免异常，请使用安全类型转换操作符 as?，它在失败时返回 null。
// val x: String? = y as? String
// 请注意，尽管 as? 的右侧是非空类型 String，但类型转换的结果是可空的。


// 迭代器
// for 循环遍历任何提供迭代器的事物。集合默认提供迭代器，而区间和数组会被编译为基于索引的循环。
// 你可以通过提供一个名为 iterator() 的成员函数或扩展函数来创建你自己的迭代器，该函数返回一个 Iterator<T>。
// iterator() 函数必须拥有一个 next() 函数和一个返回 Boolean 的 hasNext() 函数。
// 为类创建自己的迭代器最简单的方法是继承 Iterable<T> 接口并覆盖已有的 iterator()、next() 和 hasNext() 函数。例如：
class Booklet(val totalPages: Int) : Iterable<Int> {
    override fun iterator(): Iterator<Int> {
        return object : Iterator<Int> {
            var current = 1
            override fun hasNext() = current <= totalPages
            override fun next() = current++
        }
    }
}

fun main_iterator() {
    val booklet = Booklet(3)
    for (page in booklet) {
        println("Reading page $page")
    }
    // Reading page 1
    // Reading page 2
    // Reading page 3
}


// 异常
//使用前置条件函数抛出异常
//Kotlin 提供了使用前置条件函数自动抛出异常的额外方式。 前置条件函数包括：
// 前置条件函数	    用例	                    抛出的异常
// require()	检测用户输入有效性	        IllegalArgumentException
// check()	    检测对象或变量状态有效性    IllegalStateException
// error()	    指示非法状态或条件	        IllegalStateException
//这些函数适用于程序流在不满足特定条件时无法继续的情况。 这能精简您的代码并使这些检测的处理变得高效。

// require() 函数
//使用 require() 函数来验证输入实参，当这些实参对函数的运算至关重要，且函数在实参无效时无法继续时。
//如果 require() 中的条件不满足，它将抛出 IllegalArgumentException：
fun getIndices(count: Int): List<Int> {
    require(count >= 0) { "Count must be non-negative. You set count to $count." }
    return List(count) { it + 1 }
}

fun main_require() {
    // This fails with an IllegalArgumentException
    println(getIndices(-1))

    // Uncomment the line below to see a working example
    // println(getIndices(3))
    // [1, 2, 3]
}

// check() 函数
//使用 check() 函数来检测对象或变量的状态有效性。 如果检测失败，则表示需要解决的逻辑错误。
//如果 check() 函数中指定的条件为 false，它将抛出 IllegalStateException：
fun main_check() {
    var someState: String? = null

    fun getStateValue(): String {

        val state = checkNotNull(someState) { "State must be set beforehand!" }
        check(state.isNotEmpty()) { "State must be non-empty!" }
        return state
    }
    // 如果取消注释下面这行，程序将因 IllegalStateException 而失败
    // getStateValue()

    someState = ""

    // 如果取消注释下面这行，程序将因 IllegalStateException 而失败
    // getStateValue()
    someState = "non-empty-state"

    // This prints "non-empty-state"
    println(getStateValue())
}

//error() 函数
//error() 函数用于指示代码中逻辑上不应发生的非法状态或条件。 它适用于您想在代码中有意抛出异常的场景，例如当代码遇到意外状态时。
//此函数在 when 表达式中特别有用，提供了一种清晰的方式来处理逻辑上不应发生的情况。
//在以下示例中，error() 函数用于处理未定义的用户角色。 如果角色不是预定义的角色之一，则会抛出 IllegalStateException：
class User(val name: String, val role: String)

fun processUserRole(user: User) {
    when (user.role) {
        "admin" -> println("${user.name} is an admin.")
        "editor" -> println("${user.name} is an editor.")
        "viewer" -> println("${user.name} is a viewer.")
        else -> error("Undefined role: ${user.role}")
    }
}

fun main_error() {
    // This works as expected
    val user1 = User("Alice", "admin")
    processUserRole(user1)
    // Alice is an admin.

    // This throws an IllegalStateException
    val user2 = User("Bob", "guest")
    processUserRole(user2)
}


// finally 代码块
// 在 Kotlin 中，管理实现 AutoClosable 接口的资源（例如 FileInputStream 或 FileOutputStream 等文件流）的惯用方式是使用 .use() 函数。
// 此函数在代码块完成时自动关闭资源，无论是否抛出异常，从而消除了 finally 代码块的需要。 因此，Kotlin 不需要像 Java 的 try-with-resources 那样的特殊语法来管理资源。
fun main_finally() {
    FileWriter("test.txt").use { writer ->
        writer.write("some text")
        // 在此代码块之后，.use 函数会自动调用 writer.close()，类似于 finally 代码块
    }
}


// 类
// Kotlin 提供了声明类的简洁语法。要声明一个类，请使用 class 关键字后跟类名：
class Person { /*...*/ }
// 类声明由以下部分组成：
//
// 类头，包括但不限于：
//  class 关键字
//  类名
//  类型形参（如果有）
//  主构造函数（可选）
// 类体（可选），由花括号 {} 包围，并包含以下类成员：
//  次构造函数
//  初始化块
//  函数
//  属性
//  嵌套类和内部类
//  对象声明

// 你可以将类头和类体都保持在最简状态。如果类没有类体，则可以省略花括号 {}：
// 具有主构造函数但没有类体的类
class Person2(val name: String, var age: Int)

// 创建实例
// 要创建类的实例，请使用类名后跟圆括号 ()，类似于调用函数：
// 创建 Person 类的实例
val anonymousUser = Person()
// 在 Kotlin 中，与其他面向对象编程语言不同，创建类实例时无需使用 new 关键字。

// 主构造函数
//主构造函数在创建实例时设置实例的初始状态。
//要声明主构造函数，请将其放在类名后的类头中：
class Person3 constructor(name: String) { /*...*/ }
//如果主构造函数没有任何注解或可见性修饰符，你可以省略 constructor 关键字：
class Person4(name: String) { /*...*/ }

//主构造函数可以将形参声明为属性。在实参名前使用 val 关键字声明只读属性，使用 var 关键字声明可变属性：
class Person5(val name: String, var age: Int) { /*...*/ }
//这些构造函数形参属性作为实例的一部分存储，并且可以在类外部访问。

//也可以声明非属性的主构造函数形参。这些形参前面没有 val 或 var，因此它们不会存储在实例中，并且只在类体内部可用：
// 也是属性的主构造函数形参
class PersonWithProperty(val name: String) {
    fun greet() {
        println("Hello, $name")
    }
}

// 仅为主构造函数形参（不作为属性存储）
class PersonWithAssignment(name: String) {
    // 必须赋值给一个属性才能稍后使用
    val displayName: String = name

    fun greet() {
        println("Hello, $displayName")
    }
}

//在主构造函数中声明的属性可以通过类的成员函数访问：
// 带有声明属性的主构造函数的类
class Person6(val name: String, var age: Int) {
    // 访问类属性的成员函数
    fun introduce(): String {
        return "Hi, I'm $name and I'm $age years old."
    }
}

//你还可以在主构造函数中为属性赋值默认值：
class Person7(val name: String = "John", var age: Int = 30) { /*...*/ }

//初始化块
//主构造函数初始化类并设置其属性。在大多数情况下，你可以用简单的代码来处理这一点。
//如果在实例创建期间需要执行更复杂的操作，请将该逻辑放在类体内部的_初始化块_中。这些代码块在主构造函数执行时运行。
//在Kotlin中，init代码块的回调时机是在类的主构造函数执行完毕后立即执行。
//使用 init 关键字后跟花括号 {} 声明初始化块。将你希望在初始化期间运行的任何代码写入花括号内：
// 带有主构造函数用于初始化 name 和 age 的类
class Person8(val name: String, var age: Int) {
    init {
        // 实例创建时初始化块运行
        println("Person created: $name, age $age.")
    }
}

fun main_class() {
    // 创建 Person 类的实例
    Person8("John", 30)
    // Person created: John, age 30.
}

//根据需要添加任意数量的初始化块（init {}）。它们按照在类体中出现的顺序执行，并与属性初始化器交错执行：
// 带有主构造函数用于初始化 name 和 age 的类
class Person9(val name: String, var age: Int) {
    // 第一个初始化块
    init {
        // 实例创建时首先运行
        println("Person created: $name, age $age.")
    }

    // 第二个初始化块
    init {
        // 在第一个初始化块之后运行
        if (age < 18) {
            println("$name is a minor.")
        } else {
            println("$name is an adult.")
        }
    }
}

fun main_class2() {
    // 创建 Person 类的实例
    Person9("John", 30)
    // Person created: John, age 30.
    // John is an adult.
}

//次构造函数
//在 Kotlin 中，次构造函数是类除了主构造函数之外可以拥有的额外构造函数。当你需要多种方式来初始化类，或为了Java 互操作性时，次构造函数会很有用。
//要声明次构造函数，请在类体内部使用 constructor 关键字，并在圆括号 () 中包含构造函数形参。将构造函数逻辑添加到花括号 {} 内：
// 带有主构造函数用于初始化 name 和 age 的类头
class Person10(val name: String, var age: Int) {

    // 次构造函数，它接受 age 作为 String 并将其转换为 Int
    // 次构造函数通过 this 关键字委托给主构造函数，传递 name 和转换为整数的 age 值。
    constructor(name: String, age: String) : this(name, age.toIntOrNull() ?: 0) {
        println("$name created with converted age: $age")
    }
}

fun main_class3() {
    // 使用次构造函数，其中 age 为 String 类型
    Person10("Bob", "8")
    // Bob created with converted age: 8
}
//在上面的代码中，次构造函数通过 this 关键字委托给主构造函数，传递 name 和转换为整数的 age 值。
//在 Kotlin 中，次构造函数必须委托给主构造函数。这种委托机制确保了所有主构造函数的初始化逻辑在任何次构造函数逻辑运行之前执行。
//构造函数委托可以是：
// 直接的，即次构造函数立即调用主构造函数。
// 间接的，即一个次构造函数调用另一个次构造函数，后者再委托给主构造函数。
//以下示例演示了直接和间接委托的工作方式：
// 带有主构造函数用于初始化 name 和 age 的类头
class Person11(
    val name: String,
    var age: Int
) {
    // 带有直接委托给主构造函数的次构造函数
    constructor(name: String) : this(name, 0) {
        println("Person created with default age: $age and name: $name.")
    }

    // 带有间接委托的次构造函数：this("Bob") -> constructor(name: String) -> 主构造函数
    constructor() : this("Bob") {
        println("New person created with default age: $age and name: $name.")
    }
}

fun main_class4() {
    // 基于直接委托创建实例
    Person11("Alice")
    // Person created with default age: 0 and name: Alice.

    // 基于间接委托创建实例
    Person11()
    // Person created with default age: 0 and name: Bob.
    // New person created with default age: 0 and name: Bob.
}
//在带有初始化块（init {}）的类中，这些块中的代码成为主构造函数的一部分。鉴于次构造函数首先委托给主构造函数，
//所有初始化块和属性初始化器都会在次构造函数体执行之前运行。即使类没有主构造函数，委托仍然会隐式发生：
// 没有主构造函数的类头
class Person12 {
    // 实例创建时初始化块运行
    init {
        // 在次构造函数之前运行
        println("1. First initializer block runs")
    }

    // 接受整数形参的次构造函数
    constructor(i: Int) {
        // 在初始化块之后运行
        println("2. Person $i is created")
    }
}

fun main_class5() {
    // 创建 Person 类的实例
    Person12(1)
    // 1. First initializer block runs
    // 2. Person 1 created
}

//没有构造函数的类
//未声明任何构造函数（主构造函数或次构造函数）的类会有一个不带形参的隐式主构造函数：
// 没有显式构造函数的类
class Person13 {
    // 未声明主构造函数或次构造函数
}

fun main_class6() {
    // 使用隐式主构造函数创建 Person 类的实例
    val person = Person13()
}
//这个隐式主构造函数的可见性是 public，这意味着它可以在任何地方访问。如果你不希望你的类拥有公有构造函数，请声明一个带有非默认可见性的空主构造函数：
class Person14 private constructor() { /*...*/ }

//继承
//Kotlin 中的类继承允许你从现有类（基类）创建新类（派生类），继承其属性和函数，同时添加或修改行为。
//Kotlin 中的所有类都有一个共同的超类 Any，它是未声明任何超类型的类的默认超类：
class Example // 隐式继承自 Any
//Any 有三个方法：equals()、hashCode() 和 toString()。因此，所有 Kotlin 类都定义了这些方法。
//默认情况下，Kotlin 类是 final 的，即它们不能被继承。要使一个类可继承，请用 open 关键字标记它：
open class Base // 类可用于继承
//要声明一个显式超类型，请在类头中的冒号后放置该类型：
open class Base2(p: Int)
class Derived(p: Int) : Base2(p)

//如果派生类有一个主构造函数，则基类可以在该主构造函数中根据其形参进行初始化（并且必须初始化）。
//如果派生类没有主构造函数，则每个次构造函数都必须使用 super 关键字初始化基类型，或者委托给另一个执行此操作的构造函数。
//请注意，在这种情况下，不同的次构造函数可以调用基类型的不同构造函数：
class MyView : View {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
}

//Open 关键字
//在 Kotlin 中，open 关键字表明一个类或成员（函数或属性）可以在子类中被覆盖。默认情况下，Kotlin 类及其成员是 final 的，
//这意味着它们不能被继承（对于类而言）或被覆盖（对于成员而言），除非你显式地将它们标记为 open：
// 带有 `open` 关键字的基类，允许被继承
open class Person18(
    val name: String
) {
    // 可以在子类中被覆盖的 `open` 函数
    open fun introduce() {
        println("Hello, my name is $name.")
    }
}

// 继承自 Person 并覆盖 introduce() 函数的子类
class Student2(
    name: String,
    val school: String
) : Person18(name) {
    override fun introduce() {
        println("Hi, I'm $name, and I study at $school.")
    }
}

//如果你覆盖基类的一个成员，该覆盖成员默认也是 open 的。如果你想改变这一点并禁止你的类的子类覆盖你的实现，你可以显式地将覆盖成员标记为 final：
// 带有 `open` 关键字的基类，允许被继承
open class Person19(val name: String) {
    // 可以在子类中被覆盖的 `open` 函数
    open fun introduce() {
        println("Hello, my name is $name.")
    }
}

// 继承自 Person 并覆盖 introduce() 函数的子类
class Student3(name: String, val school: String) : Person19(name) {
    // `final` 关键字可防止在子类中进一步覆盖
    final override fun introduce() {
        println("Hi, I'm $name, and I study at $school.")
    }
}

//覆盖方法
//Kotlin 要求对可覆盖成员和覆盖使用显式修饰符：
open class Shape3 {
    open fun draw() { /*...*/ }
    fun fill() { /*...*/ }
}

class Circle() : Shape3() {
    // 必须使用 override 修饰符。如果缺少，编译器会报错。
    override fun draw() { /*...*/ }
}
//如果函数没有 open 修饰符（例如 Shape.fill()），则不允许在子类中声明具有相同签名的方法，无论是否使用 override。
//open 修饰符添加到 final 类（即没有 open 修饰符的类）的成员时没有效果。
//标记为 override 的成员本身是 open 的，因此它可以在子类中被覆盖。如果你想禁止再次覆盖，请使用 final：
open class Rectangle3() : Shape3() {
    final override fun draw() { /*...*/ }
}

//覆盖属性
//覆盖机制对属性的作用方式与对方法的作用方式相同。在超类中声明，然后在派生类中重新声明的属性必须以 override 为前缀，
//并且它们必须具有兼容的类型。每个声明的属性都可以通过具有初始化器或具有 get 方法的属性来覆盖：
open class Shape4 {
    open val vertexCount: Int = 0
}

class Rectangle4 : Shape4() {
    override val vertexCount = 4
}
//你也可以使用 var 属性覆盖 val 属性，但不能反之。这是允许的，因为 val 属性本质上声明了一个 get 方法，而将其作为 var 覆盖会在派生类中额外声明一个 set 方法。
//请注意，你可以在主构造函数中使用 override 关键字作为属性声明的一部分：
interface Shape5 {
    val vertexCount: Int
}

class Rectangle5(override val vertexCount: Int = 4) : Shape5 // 始终有 4 个顶点

class Polygon : Shape5 {
    override var vertexCount: Int = 0  // 稍后可以设置为任何数字
}

//派生类的初始化顺序
//在构建派生类的新实例期间，基类初始化是第一步（仅在求值基类构造函数实参之后），这意味着它发生在派生类的初始化逻辑运行之前。
open class Base3(val name: String) {

    init { println("初始化基类") }

    open val size: Int =
        name.length.also { println("在基类中初始化 size: $it") }
}

class Derived2(
    name: String,
    val lastName: String,
) : Base3(name.replaceFirstChar { it.uppercase() }.also { println("基类的实参: $it") }) {

    init { println("初始化派生类") }

    override val size: Int =
        (super.size + lastName.length).also { println("在派生类中初始化 size: $it") }
}

fun main_extends() {
    println("构建派生类(\"hello\", \"world\")")
    Derived2("hello", "world")
}
//这意味着当基类构造函数执行时，在派生类中声明或覆盖的属性尚未初始化。在基类初始化逻辑中（无论是直接还是通过另一个被覆盖的 open 成员实现间接）
//使用任何这些属性都可能导致不正确的行为或运行时故障。因此，在设计基类时，应避免在构造函数、属性初始化器或 init 代码块中使用 open 成员。

//调用超类的实现
//派生类中的代码可以使用 super 关键字调用其超类函数和属性访问器实现：
open class Rectangle6 {
    open fun draw() { println("绘制矩形") }
    val borderColor: String get() = "black"
}

class FilledRectangle : Rectangle6() {
    override fun draw() {
        super.draw()
        println("填充矩形")
    }

    val fillColor: String get() = super.borderColor
}

//在内部类中，访问外部类的超类是使用 super 关键字并用外部类名限定来完成的：super@Outer：
open class Rectangle7 {
    open fun draw() { println("绘制矩形") }
    val borderColor: String get() = "black"
}

class FilledRectangle2: Rectangle7() {
    override fun draw() {
        val filler = Filler()
        filler.drawAndFill()
    }

    inner class Filler {
        fun fill() { println("填充") }
        fun drawAndFill() {
            super@FilledRectangle2.draw() // 调用 Rectangle 的 draw() 实现
            fill()
            println("Drawn a filled rectangle with color ${super@FilledRectangle2.borderColor}") // 使用 Rectangle 的 borderColor 的 get() 实现
        }
    }
}

fun main_extends2() {
    val fr = FilledRectangle()
    fr.draw()
}

//覆盖规则
//在 Kotlin 中，实现继承受以下规则约束：如果一个类从其直接超类继承了同一成员的多个实现，它必须覆盖该成员并提供其自身的实现（或许可以使用其中一个继承的实现）。
//要指明继承实现来自的超类型，请使用 super 关键字，并用尖括号中的超类型名限定，例如 super<Base>：
open class Rectangle8 {
    open fun draw() { /* ... */ }
}

interface Polygon2 {
    fun draw() { /* ... */ } // 接口成员默认为 'open'
}

class Square() : Rectangle8(), Polygon2 {
    // 编译器要求覆盖 draw()：
    override fun draw() {
        super<Rectangle8>.draw() // 调用 Rectangle.draw()
        super<Polygon2>.draw() // 调用 Polygon.draw()
    }
}
//同时继承 Rectangle 和 Polygon 是允许的，但它们都有各自的 draw() 实现，因此你需要在 Square 中覆盖 draw() 并为其提供单独的实现以消除歧义。

//属性
//在 Kotlin 中，属性允许你存储和管理数据，而无需编写函数来访问或更改数据。你可以在类、接口、对象、伴生对象中使用属性，甚至可以在这些结构之外作为顶层属性使用。
//每个属性都有一个名称、一个类型，以及一个自动生成的名为 getter 的 get() 函数。你可以使用 getter 读取属性的值。如果属性是可变的，它还有一个名为 setter 的 set() 函数，允许你更改属性的值。
//getter 和 setter 被称为 访问器。

//声明属性
//属性可以是可变的 (var) 或只读的 (val)。 你可以在 .kt 文件中将它们声明为顶层属性。可以将顶层属性视为属于某个包的全局变量：
// File: Constants.kt
//package my.app
val pi = 3.14159
var counter = 0

// 你也可以在类、接口或对象中声明属性：
// Class with properties
class Address {
    var name: String = "Holmes, Sherlock"
    var street: String = "Baker"
    var city: String = "London"
}

// Interface with a property
interface ContactInfo {
    val email: String
}

// Object with properties
object Company {
    var name: String = "Detective Inc."
    val country: String = "UK"
}

// Class implementing the interface
class PersonContact : ContactInfo {
    override val email: String = "sherlock@example.com"
}

//要使用属性，只需按其名称引用：
class Address2 {
    var name: String = "Holmes, Sherlock"
    var street: String = "Baker"
    var city: String = "London"
}

interface ContactInfo2 {
    val email: String
}

object Company2 {
    var name: String = "Detective Inc."
    val country: String = "UK"
}

class PersonContact2 : ContactInfo2 {
    override val email: String = "sherlock@example.com"
}

fun copyAddress(address: Address2): Address2 {
    val result = Address2()
    // 访问 result 实例中的属性
    result.name = address.name
    result.street = address.street
    result.city = address.city
    return result
}

fun main_prop() {
    val sherlockAddress = Address2()
    val copy = copyAddress(sherlockAddress)
    // 访问 copy 实例中的属性
    println("Copied address: ${copy.name}, ${copy.street}, ${copy.city}")
    // Copied address: Holmes, Sherlock, Baker, London

    // 访问 Company 对象中的属性
    println("Company: ${Company2.name} in ${Company2.country}")
    // Company: Detective Inc. in UK

    val contact = PersonContact2()
    // 访问 contact 实例中的属性
    println("Email: ${contact.email}")
    // Email: sherlock@email.com
}
//在 Kotlin 中，我们建议在声明属性时初始化它们，以确保代码安全且易于阅读。但是，在特殊情况下，你可以稍后初始化它们。

//如果编译器可以从初始化器或 getter 的返回类型中推断出来，则属性类型是可选的：
var initialized = 1 // 推断类型为 Int
//var allByDefault    // 错误：属性必须初始化。

//自定义 getter 和 setter
//默认情况下，Kotlin 会自动生成 getter 和 setter。当你需要额外的逻辑时，例如验证、格式化或基于其他属性的计算，可以定义自己的自定义访问器。
//每次访问属性时，都会运行自定义 getter：
class Rectangle9(val width: Int, val height: Int) {
    val area: Int
        get() = this.width * this.height
}
fun main_prop2() {
    val rectangle = Rectangle9(3, 4)
    println("Width=${rectangle.width}, height=${rectangle.height}, area=${rectangle.area}")
}
//如果编译器可以从 getter 中推断出类型，则可以省略它：
class Rectangle10(val width: Int, val height: Int) {
    val area get() = this.width * this.height
}
//每次你赋值给属性时，都会运行自定义 setter，除了其初始化期间。按照惯例，setter 形参的名称是 value，但如果你愿意，可以选择一个不同的名称：
class Point(var x: Int, var y: Int) {
    var coordinates: String
        get() = "$x,$y"
        set(value) {
            val parts = value.split(",")
            x = parts[0].toInt()
            y = parts[1].toInt()
        }
}

fun main_prop3() {
    val location = Point(1, 2)
    println(location.coordinates)
    // 1,2

    location.coordinates = "10,20"
    println("${location.x}, ${location.y}")
    // 10, 20
}

//更改可见性或添加注解
//在 Kotlin 中，你可以在不替换默认实现的情况下更改访问器可见性或添加注解。你无需在 {} 块中进行这些更改。
//要更改访问器的可见性，请在 get 或 set 关键字之前使用修饰符：
class BankAccount(initialBalance: Int) {
    var balance: Int = initialBalance
        // 只有类可以修改 balance
        private set

    fun deposit(amount: Int) {
        if (amount > 0) balance += amount
    }

    fun withdraw(amount: Int) {
        if (amount > 0 && amount <= balance) balance -= amount
    }
}

fun main_prop4() {
    val account = BankAccount(100)
    println("Initial balance: ${account.balance}")
    // 100

    account.deposit(50)
    println("After deposit: ${account.balance}")
    // 150

    account.withdraw(70)
    println("After withdrawal: ${account.balance}")
    // 80

    // account.balance = 1000
    // 错误：无法赋值，因为 setter 是私有的
}
//要注解访问器，请在 get 或 set 关键字之前使用注解：
// 定义一个可以应用于 getter 的注解
@Target(AnnotationTarget.PROPERTY_GETTER)
annotation class Inject

class Service {
    var dependency: String = "Default Service"
        // 注解 getter
        @Inject get
}

fun main_prop5() {
    val service = Service()
    println(service.dependency)
    // Default service
    println(service::dependency.getter.annotations)
    // [@Inject()]
    println(service::dependency.setter.annotations)
    // []
}

//幕后字段
//在 Kotlin 中，访问器使用幕后字段在内存中存储属性的值。当你想要向 getter 或 setter 添加额外逻辑时，或者当你想要在属性更改时触发附加操作时，幕后字段会很有用。
//你不能直接声明幕后字段。Kotlin 只在必要时生成它们。你可以使用 field 关键字在访问器中引用幕后字段。
//如果属性使用至少一个访问器的默认实现，或者自定义访问器通过 field 标识符引用它，则会为该属性生成一个幕后字段。
//例如，isEmpty 属性没有幕后字段，因为它使用了不带 field 关键字的自定义 getter：
//val isEmpty: Boolean
//    get() = this.size == 0
//在此示例中，score 属性有一个幕后字段，因为 setter 使用了 field 关键字：
class Scoreboard {
    var score: Int = 0
        set(value) {
            field = value
            // 在更新值时添加日志
            println("Score updated to $field")
        }
}

fun main_prop6() {
    val board = Scoreboard()
    board.score = 10
    // Score updated to 10
    board.score = 20
    // Score updated to 20
}

//幕后属性
//有时你可能需要比使用幕后字段所能提供的更强的灵活性。例如，如果你有一个 API，你希望能够在内部修改属性，但在外部不可修改。在这种情况下，你可以使用一种称为 幕后属性 的编码模式。
//在以下示例中，ShoppingCart 类有一个 items 属性，表示购物车中的所有物品。你希望 items 属性在类外部是只读的，
//但仍允许用户通过一种“批准”的方式直接修改 items 属性。为实现此目的，你可以定义一个名为 _items 的私有幕后属性，以及一个名为 items 的公共属性，该属性委托给幕后属性的值。
class ShoppingCart {
    // 幕后属性
    private val _items = mutableListOf<String>()

    // 公共只读视图
    val items: List<String>
        get() = _items

    fun addItem(item: String) {
        _items.add(item)
    }

    fun removeItem(item: String) {
        _items.remove(item)
    }
}

fun main_prop7() {
    val cart = ShoppingCart()
    cart.addItem("Apple")
    cart.addItem("Banana")

    println(cart.items)
    // [Apple, Banana]

    cart.removeItem("Apple")
    println(cart.items)
    // [Banana]
}
//在此示例中，用户只能通过 addItem() 函数向购物车添加物品，但仍可以访问 items 属性来查看其中内容。
//在命名幕后属性时使用前导下划线，以遵循 Kotlin 编码约定。
//在 JVM 上，编译器会优化对具有默认访问器的私有属性的访问，以避免函数调用开销。
//当你希望多个公共属性共享一个状态时，幕后属性也很有用。例如：
class Temperature {
    // 存储摄氏温度的幕后属性
    private var _celsius: Double = 0.0

    var celsius: Double
        get() = _celsius
        set(value) { _celsius = value }

    var fahrenheit: Double
        get() = _celsius * 9 / 5 + 32
        set(value) { _celsius = (value - 32) * 5 / 9 }
}

fun main_prop8() {
    val temp = Temperature()
    temp.celsius = 25.0
    println("${temp.celsius}°C = ${temp.fahrenheit}°F")
    // 25.0°C = 77.0°F

    temp.fahrenheit = 212.0
    println("${temp.celsius}°C = ${temp.fahrenheit}°F")
    // 100.0°C = 212.0°F
}
//在此示例中，_celsius 幕后属性被 celsius 和 fahrenheit 属性共同访问。这种设置提供了一个单一的真相来源，并带有两个公共视图。

//编译期常量
//如果只读属性的值在编译期已知，请使用 const 修饰符将其标记为编译期常量。编译期常量在编译期内联，因此每个引用都会被其实际值替换。它们被更高效地访问，因为没有调用 getter：
// File: AppConfig.kt
//package com.example
// 编译期常量
const val MAX_LOGIN_ATTEMPTS = 3
//编译期常量必须满足以下要求：
// 必须是顶层属性，或者是 object 声明或伴生对象的成员。
// 必须使用 String 类型或原生类型的值进行初始化。
// 不能是自定义 getter。

//延迟初始化属性和变量
//通常，你必须在构造函数中初始化属性。然而，这样做通常并不方便。例如，属性可以通过依赖注入进行初始化，或者在单元测试的设置方法中进行初始化。
//为了处理这些情况，你可以使用 lateinit 修饰符标记属性：
class OrderService {
    fun processOrder() {}
}
public class OrderServiceTest {
    lateinit var orderService: OrderService

//    @SetUp
    fun setup() {
        orderService = OrderService()
    }

    @Test
    fun processesOrderSuccessfully() {
        // 直接调用 orderService，不检测 null
        // 或初始化
        orderService.processOrder()
    }
}
//你可以在以下声明的 var 属性上使用 lateinit 修饰符：
// 顶层属性。
// 局部变量。
// 类体内部的属性。
//对于类属性：
// 你不能在主构造函数中声明它们。
// 它们不能有自定义 getter 或 setter。
//在所有情况下，属性或变量必须是非空的，并且不能是原生类型。
//在 lateinit 属性初始化之前访问它，Kotlin 会抛出一个特殊异常，该异常清楚地标识了被访问的未初始化属性：
class ReportGenerator {
    lateinit var report: String

    fun printReport() {
        // 由于在初始化之前访问，会抛出异常
        println(report)
    }
}

fun main_prop9() {
    val generator = ReportGenerator()
    generator.printReport()
    // Exception in thread "main" kotlin.UninitializedPropertyAccessException: lateinit property report has not been initialized
}
//要检测 lateinit var 是否已初始化，请在对该属性的引用上使用 isInitialized 属性：
class WeatherStation {
    lateinit var latestReading: String

    fun printReading() {
        // 检测属性是否已初始化
        if (this::latestReading.isInitialized) {
            println("Latest reading: $latestReading")
        } else {
            println("No reading available")
        }
    }
}

fun main_prop10() {
    val station = WeatherStation()

    station.printReading()
    // No reading available
    station.latestReading = "22°C, sunny"
    station.printReading()
    // Latest reading: 22°C, sunny
}
//此检测仅适用于在相同类型、其中一个外部类型或同一文件的顶层声明时词法可访问的属性。


//抽象类
//在 Kotlin 中，抽象类是不能直接实例化的类。它们旨在被其他类继承，这些类定义了它们的实际行为。这种行为称为_实现_。
//抽象类可以声明抽象属性和函数，这些属性和函数必须由子类实现。
//抽象类也可以有构造函数。这些构造函数初始化类属性并为子类强制执行所需的形参。使用 abstract 关键字声明抽象类：
abstract class Person15(val name: String, val age: Int)
//抽象类可以同时拥有抽象成员和非抽象成员（属性和函数）。要将成员声明为抽象的，你必须显式使用 abstract 关键字。
//你不需要用 open 关键字注解抽象类或函数，因为它们默认是隐式可继承的。有关 open 关键字的更多详细信息，请参见继承。
//抽象成员在抽象类中没有实现。你可以在子类或继承类中使用 override 函数或属性来定义实现：
// 带有主构造函数用于声明 name 和 age 的抽象类
abstract class Person16(
    val name: String,
    val age: Int
) {
    // 抽象成员
    // 不提供实现，
    // 并且必须由子类实现
    abstract fun introduce()

    // 非抽象成员（有实现）
    fun greet() {
        println("Hello, my name is $name.")
    }
}

// 为抽象成员提供实现的子类
class Student(
    name: String,
    age: Int,
    val school: String
) : Person16(name, age) {
    override fun introduce() {
        println("I am $name, $age years old, and I study at $school.")
    }
}

fun main_class7() {
    // 创建 Student 类的实例
    val student = Student("Alice", 20, "Engineering University")

    // 调用非抽象成员
    student.greet()
    // Hello, my name is Alice.

    // 调用被覆盖的抽象成员
    student.introduce()
    // I am Alice, 20 years old, and I study at Engineering University.
}

//伴生对象
//在 Kotlin 中，每个类都可以有一个伴生对象。伴生对象是一种对象声明，它允许你使用类名访问其成员，而无需创建类实例。
//假设你需要编写一个无需创建类实例即可调用，但又与该类逻辑关联的函数（例如工厂函数）。在这种情况下，你可以将其声明在类内部的一个伴生对象声明中：
// 带有主构造函数用于声明 name 属性的类
class Person17(
    val name: String
) {
    // 带有伴生对象的类体
    companion object {
        fun createAnonymous() = Person17("Anonymous")
    }
}

fun main_class8() {
    // 无需创建类实例即可调用该函数
    val anonymous = Person17.createAnonymous()
    println(anonymous.name)
    // Anonymous
}
//如果你在类中声明了一个伴生对象，你可以仅使用类名作为限定符来访问其成员。


//接口
//Kotlin 中的接口可以包含抽象方法的声明，以及方法的实现。它们与抽象类的不同之处在于，接口不能存储状态。它们可以有属性，但这些属性需要是抽象的，或者提供访问器实现。
//接口使用 interface 关键字定义：
interface MyInterface {
    fun bar()
    fun foo() {
        // optional body
    }
}

//实现接口
//一个类或对象可以实现一个或多个接口：
class Child : MyInterface {
    override fun bar() {
        // body
    }
}

//接口中的属性
//你可以在接口中声明属性。在接口中声明的属性可以是抽象的，也可以提供访问器的实现。在接口中声明的属性不能有幕后字段，因此在接口中声明的访问器不能引用它们：
interface MyInterface2 {
    val prop: Int // abstract

    val propertyWithImplementation: String
        get() = "foo"

    fun foo() {
        print(prop)
    }
}

class Child2 : MyInterface2 {
    override val prop: Int = 29
}

//接口继承
//一个接口可以从其他接口派生，这意味着它既可以为其成员提供实现，也可以声明新的函数和属性。很自然地，实现此类接口的类只需定义缺失的实现即可：
interface Named {
    val name: String
}

interface Person20 : Named {
    val firstName: String
    val lastName: String

    override val name: String get() = "$firstName $lastName"
}

data class Employee(
    // implementing 'name' is not required
    override val firstName: String,
    override val lastName: String,
    val position: Int
) : Person20

//解决覆盖冲突
//当你在超类型列表中声明了多个类型时，你可能会继承同一个方法的多个实现：
interface A {
    fun foo() { print("A") }
    fun bar()
}

interface B {
    fun foo() { print("B") }
    fun bar() { print("bar") }
}

class C : A {
    override fun bar() { print("bar") }
}

class D : A, B {
    override fun foo() {
        super<A>.foo()
        super<B>.foo()
    }

    override fun bar() {
        super<B>.bar()
    }
}
//接口 A 和 B 都声明了函数 foo() 和 bar()。它们都实现了 foo()，但只有 B 实现了 bar()（bar() 在 A 中没有被标记为抽象，
//因为如果函数没有方法体，这是接口的默认行为）。现在，如果你从 A 派生一个具体类 C，你必须覆盖 bar() 并提供一个实现。
//然而，如果你从 A 和 B 派生 D，你需要实现你从多个接口继承的所有方法，并且你需要指定 D 应该如何精确地实现它们。
//这条规则既适用于你继承了单一实现的方法（bar()），也适用于你继承了多个实现的方法（foo()）。

//针对接口函数的 JVM 默认方法生成
//在 JVM 上，接口中声明的函数会被编译为默认方法。你可以使用 -jvm-default 编译器选项来控制此行为，其取值如下：
// enable (默认值)：在接口中生成默认实现，并在子类和 DefaultImpls 类中包含桥接函数。使用此模式可以维护与旧版 Kotlin 的二进制兼容性。
// no-compatibility：仅在接口中生成默认实现。此模式跳过兼容性桥接和 DefaultImpls 类，适用于新的 Kotlin 代码。
// disable：跳过默认方法，仅生成兼容性桥接和 DefaultImpls 类。
//要配置 -jvm-default 编译器选项，请在你的 Gradle Kotlin DSL 中设置 jvmDefault 属性：
//kotlin {
//    compilerOptions {
//        jvmDefault = JvmDefaultMode.NO_COMPATIBILITY
//    }
//}


//可见性修饰符
//类、对象、接口、构造函数和函数，以及属性及其 setter，都可以拥有可见性修饰符。 getter 总是与其属性拥有相同的可见性。
//Kotlin 中有四种可见性修饰符：private、protected、internal 和 public。 默认可见性为 public。

//包
//函数、属性、类、对象和接口可以直接在包内“顶层”声明：
// file name: example.kt
//package foo
fun baz() { }
class Bar { }
// 如果你不使用可见性修饰符，则默认使用 public，这意味着你的声明将在任何地方都可见。
// 如果你将声明标记为 private，它将只在包含该声明的文件内可见。
// 如果你将其标记为 internal，它将在同一模块内的任何地方可见。
// protected 修饰符不适用于顶层声明。
// file name: example.kt
//package foo
private fun foo() { } // visible inside example.kt
public var bar: Int = 5 // property is visible everywhere
    private set         // setter is visible only in example.kt
internal val baz = 6    // visible inside the same module

//类成员
//对于在类中声明的成员：
// private 意味着该成员仅在此类内部可见（包括其所有成员）。
// protected 意味着该成员与标记为 private 的成员具有相同的可见性，但它在子类中也可见。
// internal 意味着此模块内任何查看声明类的客户端都能看到其 internal 成员。
// public 意味着任何查看声明类的客户端都能看到其 public 成员。
//在 Kotlin 中，外部类无法看到其内部类的 private 成员。
//如果你覆盖一个 protected 或 internal 成员并且没有显式指定可见性，则覆盖成员也将拥有与原始成员相同的可见性。
open class Outer {
    private val a = 1
    protected open val b = 2
    internal open val c = 3
    val d = 4  // public by default

    protected class Nested {
        public val e: Int = 5
    }
}

class Subclass : Outer() {
    // a is not visible
    // b, c and d are visible
    // Nested and e are visible

    override val b = 5   // 'b' is protected
    override val c = 7   // 'c' is internal
}

class Unrelated(o: Outer) {
    // o.a, o.b are not visible
    // o.c and o.d are visible (same module)
    // Outer.Nested is not visible, and Nested::e is not visible either
}

//构造函数
//使用以下语法指定类主构造函数的可见性：
//你需要添加一个显式的 constructor 关键字。
class C2 private constructor(a: Int) {  }
//这里构造函数是 private。默认情况下，所有构造函数都是 public，这实际上意味着它们在类可见的任何地方都可见（这意味着 internal 类的构造函数仅在同一模块内可见）。
//对于密封类，构造函数默认是 protected。

//局部声明
//局部变量、函数和类不能拥有可见性修饰符。

//模块
//internal 可见性修饰符意味着该成员在同一模块内可见。更具体地说，模块是一组共同编译的 Kotlin 文件，例如：
// 一个 IntelliJ IDEA 模块。
// 一个 Maven 项目。
// 一个 Gradle 源代码集（例外情况是 test 源代码集可以访问 main 的 internal 声明）。
// 一组使用一次 <kotlinc> Ant 任务调用编译的文件。