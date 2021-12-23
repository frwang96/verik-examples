import io.verik.core.*

@SimTop
object SevenSegmentDecoderTest : Module() {

    @Run
    fun test() {
        val expected: Ubit<`112`> = u("112'hfce7fe22fdb4cf6ee8a9c46a98b8")
        var actual: Ubit<`112`> = u0()
        var count: Ubit<`4`> = u0()
        do {
            val decoded = sevenSegmentDecoder(count)
            val a = decoded[6]
            val b = decoded[5]
            val c = decoded[4]
            val d = decoded[3]
            val e = decoded[2]
            val f = decoded[1]
            val g = decoded[0]
            println("sevenSegmentDecoder($count)")
            print(if (a || f) "+" else " ")
            print(if (a) "--" else "  ")
            println(if (a || b) "+" else " ")
            print(if (f) "|  " else "   ")
            println(if (b) "|" else " ")
            print(if (e || f || g) "+" else " ")
            print(if (g) "--" else "  ")
            println(if (b || c || g) "+" else " ")
            print(if (e) "|  " else "   ")
            println(if (c) "|" else " ")
            print(if (d || e) "+" else " ")
            print(if (d) "--" else "  ")
            println(if (c || d) "+" else " ")
            println()

            actual = (actual shl 7) or (decoded xor (cat(u(0b000), count) shl 3) xor count.ext<`*`>()).ext<`*`>()
            count++
        } while (count != u0<`*`>())
        println()
        println("sevenSegmentDecoder: ${if (actual != expected) "FAILED" else "PASS"}")
        if (actual != expected)
            fatal()
        else
            finish()
    }
}