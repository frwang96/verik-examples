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
object IsGeqTest : Module() {

    @Run
    fun test() {
        var a: Ubit<`4`> = u0()
        var b: Ubit<`4`> = u0()
        var error = false
        do {
            do {
                val actual = isGeq(a, b)
                val expected = (a >= b)
                if (actual != expected) {
                    println("isGeq($a, $b) = $actual (ERROR)")
                    error = true
                } else {
                    println("isGeq($a, $b) = $actual")
                }
                b++
            } while (b != u0<`*`>())
            a++
        } while (a != u0<`*`>())
        println()
        println("isGeq: ${if (error) "FAILED" else "PASS"}")
        if (error)
            fatal()
        else
            finish()
    }
}
