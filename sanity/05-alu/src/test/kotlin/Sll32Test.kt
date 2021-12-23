import io.verik.core.*

@SimTop
object Sll32Test : Module() {

    @Run
    fun test() {
        println("sll32: Running logical single bit shifts")
        var a: Ubit<`32`> = u("32'h1")
        var b: Ubit<`5`> = u0()
        for (i in 0 until 32) {
            val actual = sll32(a, b)
            val expected = a shl b
            if (actual != expected) {
                println("sll32: FAILED sll32($a, $b) = $actual")
                fatal()
            }
            b++
        }

        println("sll32: Running random 32-bit inputs")
        repeat(1024) {
            a = randomUbit()
            b = randomUbit()
            val actual = sll32(a, b)
            val expected = a shl b
            if (actual != expected) {
                println("sll32: FAILED sll32($a, $b) = $actual")
                fatal()
            }
        }

        println("sll32: PASSED")
        finish()
    }
}
