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
object Sll32Test : Module() {

    @Run
    fun test() {
        println("sll32: Running logical single bit shifts")
        var a: Ubit<`32`> = u("32'h1")
        var b: Ubit<`5`> = u0()
        for (i in 0 until 32) {
            val actual = sll32(a, b)
            val expected = a shl b
            if (actual != expected) {
                println("sll32: FAILED sll32($a, $b) = $actual")
                fatal()
            }
            b++
        }

        println("sll32: Running random 32-bit inputs")
        repeat(1024) {
            a = randomUbit()
            b = randomUbit()
            val actual = sll32(a, b)
            val expected = a shl b
            if (actual != expected) {
                println("sll32: FAILED sll32($a, $b) = $actual")
                fatal()
            }
        }

        println("sll32: PASSED")
        finish()
    }
}
