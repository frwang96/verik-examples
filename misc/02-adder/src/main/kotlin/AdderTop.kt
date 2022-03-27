/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

typealias WIDTH = `8`

@Entry
object AdderTop : Module() {

    var a: Ubit<WIDTH> = nc()
    var b: Ubit<WIDTH> = nc()
    var x: Ubit<WIDTH> = nc()

    @Make
    val adder = Adder<WIDTH>(a, b, x)

    @Run
    fun test() {
        repeat(64) {
            a = randomUbit()
            b = randomUbit()
            delay(1)
            val expected = a + b
            if (x == expected) print("PASS ")
            else print("FAIL ")
            println("$a + $b = $x")
        }
    }
}
