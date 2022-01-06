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
object VectorEqualTest : Module() {

    @Run
    fun test() {
        var expected: Ubit<`4`> = u0()
        var error = false
        do {
            val a: Ubit<`16`> = randomUbit()
            val b: Ubit<`16`> = a

            if (!expected[0])
                b[0] = !b[0]
            if (!expected[1])
                b[4] = !b[4]
            if (!expected[2])
                b[8] = !b[8]
            if (!expected[3])
                b[12] = !b[12]

            val actual = vectorEqual(a, b)
            if (actual != expected) {
                println("vectorEqual($a, $b) = $actual (ERROR)")
                error = true
            } else {
                println("vectorEqual($a, $b) = $actual")
            }
            expected++
        } while (expected != u0<`*`>())
        println()
        println("vectorEqual: ${if (error) "FAILED" else "PASS"}")
        if (error)
            fatal()
        else
            finish()
    }
}
