import io.verik.core.*

@SimTop
object AddSubTest : Module() {

    @Run
    fun test() {
        println("addSub: Running random 32-bit inputs")
        repeat(1024) {
            val a: Ubit<`32`> = randomUbit()
            val b: Ubit<`32`> = randomUbit()
            val isSub = randomBoolean()
            val actual = addSub(a, b, isSub)
            val expected = if (isSub) a - b else a + b
            if (actual != expected) {
                println("addSub: FAILED addSub($a, $b, $isSub) = $actual")
                fatal()
            }
        }
        println("addSub: PASSED")
    }
}
