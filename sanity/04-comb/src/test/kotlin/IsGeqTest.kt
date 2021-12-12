import io.verik.core.*

@SimTop
class IsGeqTest : Module() {

    @Run
    fun test() {
        var a: Ubit<`4`> = u0()
        var b: Ubit<`4`> = u0()
        var error = false
        do {
            do {
                val actual = isGeq(a, b)
                val expected = (a >= b)
                if (actual != expected) {
                    println("isGeq($a, $b) = $actual (ERROR)")
                    error = true
                } else {
                    println("isGeq($a, $b) = $actual")
                }
                b++
            } while (b != u0<`*`>())
            a++
        } while (a != u0<`*`>())
        println()
        println("isGeq: ${if (error) "FAILED" else "PASS"}")
        if (error)
            fatal()
        else
            finish()
    }
}
