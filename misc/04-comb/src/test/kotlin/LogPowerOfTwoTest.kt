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

@Entry
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
