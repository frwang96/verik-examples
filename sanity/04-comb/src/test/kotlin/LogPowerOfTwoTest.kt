import io.verik.core.*

@SimTop
object LogPowerOfTwoTest : Module() {

    fun logPowerOfTwoReference(a: Ubit<`4`>): Ubit<`2`> {
        return when (a) {
            u(0b0010) -> u(0b01)
            u(0b0100) -> u(0b10)
            u(0b1000) -> u(0b11)
            else -> u0<`2`>()
        }
    }

    @Run
    fun test() {
        var a: Ubit<`4`> = u0()
        var error = false
        do {
            val actual = logPowerOfTwo(a)
            val expected = logPowerOfTwoReference(a)
            if (actual != expected) {
                println("logPowerOfTwo($a) = $actual (ERROR)")
                error = true
            } else {
                println("logPowerOfTwo($a) = $actual")
            }
            a++
        } while (a != u0<`*`>())
        println()
        println("logPowerOfTwo: ${if (error) "FAILED" else "PASS"}")
        if (error)
            fatal()
        else
            finish()
    }
}
