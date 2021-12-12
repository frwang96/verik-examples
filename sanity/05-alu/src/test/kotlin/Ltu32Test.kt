import io.verik.core.*

@SimTop
class Ltu32Test : Module() {

    @Run
    fun test() {
        println("ltu32: Running exhaustive 4-bit inputs")
        var x: Ubit<`8`> = u0()
        for (i in 0 until 256) {
            val a: Ubit<`32`> = u(x.slice<`4`>(0).sext())
            val b: Ubit<`32`> = u(x.slice<`4`>(4).sext())
            val actual = ltu32(a, b)
            val expected = (a < b)
            if (actual != expected) {
                println("ltu32: FAILED ltu32($a, $b) = $actual")
                fatal()
            }
            x++
        }

        println("ltu32: Running random 32-bit inputs")
        repeat(1024) {
            val a: Ubit<`32`> = randomUbit()
            val b: Ubit<`32`> = randomUbit()
            val actual = ltu32(a, b)
            val expected = (a < b)
            if (actual != expected) {
                println("ltu32: FAILED ltu32($a, $b) = $actual")
                fatal()
            }
        }

        println("ltu32: PASSED")
        finish()
    }
}
