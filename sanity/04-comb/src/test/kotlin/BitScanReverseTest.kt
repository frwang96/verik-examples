/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Verik

import io.verik.core.*

@SimTop
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
        if (error)
            fatal()
        else
            finish()
    }
}
