import io.verik.core.*

@SimTop
class CmpTest : Module() {

    @Run
    fun test() {
        println("cmp: Running tests")
        var a: Ubit<`4`> = u0()
        for (i in 0 until 12) {
            val actual = cmp(a[0], a[1], a[2], a[3])
            val expected = cat(
                (a[0] == a[1]) && a[2],
                (!a[0] && a[1]) || ((a[0] == a[1]) && a[3])
            )
            if (actual != expected) {
                println("cmp: FAILED cmp(${a[0]}, ${a[1]}, ${a[2]}, ${a[3]}) = $actual")
                fatal()
            }
            a++
        }
        println("cmp: PASSED")
    }
}
