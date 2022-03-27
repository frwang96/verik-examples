/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

@Entry
object BitScanReverseTest : Module() {

    fun bitScanReverseReference(a: Ubit<`4`>): Ubit<`2`> {
        var ret = u(0b00)
        if (a[1]) ret = u(0b01)
        if (a[2]) ret = u(0b10)
        if (a[3]) ret = u(0b11)
        return ret
    }

    @Run
    fun test() {
        var a: Ubit<`4`> = u0()
        var error = false
        do {
            val actual = bitScanReverse(a)
            val expected = bitScanReverseReference(a)
            if (actual != expected) {
                println("bitScanReverse($a) = $actual (ERROR)")
                error = true
            } else {
                println("bitScanReverse($a) = $actual")
            }
            a++
        } while (a != u0<`*`>())
        println()
        println("bitScanReverse: ${if (error) "FAILED" else "PASS"}")
        if (error) fatal()
        else finish()
    }
}
