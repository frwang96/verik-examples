import io.verik.core.*

@SimTop
object PopulationCountTest : Module() {

    fun populationCountReference(a: Ubit<`4`>): Ubit<`3`> {
        return u(0b000) + a.slice<`1`>(0) + a.slice<`1`>(1) + a.slice<`1`>(2) + a.slice<`1`>(3)
    }

    @Run
    fun test() {
        var a: Ubit<`4`> = u0()
        var error = false
        do {
            val actual = populationCount(a)
            val expected = populationCountReference(a)
            if (actual != expected) {
                println("populationCount($a) = $actual (ERROR)")
                error = true
            } else {
                println("populationCount($a) = $actual")
            }
            a++
        } while (a != u0<`*`>())
        println()
        println("populationCount: ${if (error) "FAILED" else "PASS"}")
        if (error)
            fatal()
        else
            finish()
    }
}
