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
