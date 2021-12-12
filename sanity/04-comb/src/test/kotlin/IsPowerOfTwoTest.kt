import io.verik.core.*

@SimTop
class IsPowerOfTwoTest : Module() {

    fun ifPowerOfTwoReference(a: Ubit<`4`>): Boolean {
        return when (a) {
            u(0b0001) -> true
            u(0b0010) -> true
            u(0b0100) -> true
            u(0b1000) -> true
            else -> false
        }
    }

    @Run
    fun test() {
        var a: Ubit<`4`> = u0()
        var error = false
        do {
            val actual = isPowerOfTwo(a)
            val expected = ifPowerOfTwoReference(a)
            if (actual != expected) {
                println("isPowerOfTwo($a) = $actual (ERROR)")
                error = true
            } else {
                println("isPowerOfTwo($a) = $actual")
            }
            a++
        } while (a != u0<`*`>())
        println()
        println("isPowerOfTwo: ${if (error) "FAILED" else "PASS"}")
        if (error)
            fatal()
        else
            finish()
    }
}
