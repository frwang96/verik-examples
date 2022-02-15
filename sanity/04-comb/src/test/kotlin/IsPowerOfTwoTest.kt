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

@EntryPoint
object IsPowerOfTwoTest : Module() {

    fun ifPowerOfTwoReference(a: Ubit<`4`>): Boolean {
        return when (a) {
            u(0b0001) -> true
            u(0b0010) -> true
            u(0b0100) -> true
            u(0b1000) -> true
            else -> false
        }
    }

    @Run
    fun test() {
        var a: Ubit<`4`> = u0()
        var error = false
        do {
            val actual = isPowerOfTwo(a)
            val expected = ifPowerOfTwoReference(a)
            if (actual != expected) {
                println("isPowerOfTwo($a) = $actual (ERROR)")
                error = true
            } else {
                println("isPowerOfTwo($a) = $actual")
            }
            a++
        } while (a != u0<`*`>())
        println()
        println("isPowerOfTwo: ${if (error) "FAILED" else "PASS"}")
        if (error)
            fatal()
        else
            finish()
    }
}
