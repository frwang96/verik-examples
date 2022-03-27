/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

@Entry
object FaTest : Module() {

    @Run
    fun test() {
        println("fa: Running tests")
        var a: Ubit<`3`> = u0()
        for (i in 0 until 8) {
            val actual = fa(a[0], a[1], a[2])
            val expected = cat(
                (a[0] && a[1]) || (a[0] && a[2]) || (a[1] && a[2]),
                a[0] xor a[1] xor a[2]
            )
            if (actual != expected) {
                println("fa: FAILED fa(${a[0]}, ${a[1]}, ${a[2]}) = $actual")
                fatal()
            }
            a++
        }
        println("fa: PASSED")
    }
}
