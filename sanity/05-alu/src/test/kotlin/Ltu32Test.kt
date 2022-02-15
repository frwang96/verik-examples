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
object Ltu32Test : Module() {

    @Run
    fun test() {
        println("ltu32: Running exhaustive 4-bit inputs")
        var x: Ubit<`8`> = u0()
        for (i in 0 until 256) {
            val a: Ubit<`32`> = x[3, 0].sext()
            val b: Ubit<`32`> = x[7, 4].sext()
            val actual = ltu32(a, b)
            val expected = (a < b)
            if (actual != expected) {
                println("ltu32: FAILED ltu32($a, $b) = $actual")
                fatal()
            }
            x++
        }

        println("ltu32: Running random 32-bit inputs")
        repeat(1024) {
            val a: Ubit<`32`> = randomUbit()
            val b: Ubit<`32`> = randomUbit()
            val actual = ltu32(a, b)
            val expected = (a < b)
            if (actual != expected) {
                println("ltu32: FAILED ltu32($a, $b) = $actual")
                fatal()
            }
        }

        println("ltu32: PASSED")
        finish()
    }
}
