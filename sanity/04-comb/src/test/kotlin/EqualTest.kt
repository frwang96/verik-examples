import io.verik.core.*

@SimTop
object EqualTest : Module() {

    @Run
    fun test() {
        var a: Ubit<`4`> = u0()
        var b: Ubit<`4`> = u0()
        var error = false
        do {
            do {
                val actual = equal(a, b)
                val expected = (a == b)
                if (actual != expected) {
                    println("equal($a, $b) = $actual (ERROR)")
                    error = true
                } else {
                    println("equal($a, $b) = $actual")
                }
                b++
            } while (b != u0<`*`>())
            a++
        } while (a != u0<`*`>())
        println()
        println("equal: ${if (error) "FAILED" else "PASS"}")
        if (error)
            fatal()
        else
            finish()
    }
}
