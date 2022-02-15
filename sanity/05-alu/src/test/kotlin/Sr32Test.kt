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
object Sr32Test : Module() {

    @Run
    fun test() {
        println("sr32: Running logical single bit shifts")
        var a: Ubit<`32`> = u("32'h8000_0000")
        var b: Ubit<`5`> = u0()
        for (i in 0 until 32) {
            val actual = sr32(a, b, false)
            val expected = a shr b
            if (actual != expected) {
                println("sr32: FAILED sr32($a, $b, 1'b0) = $actual")
                fatal()
            }
            b++
        }

        println("sr32: Running arithmetic single bit shifts")
        b = u0()
        for (i in 0 until 32) {
            val actual = sr32(a, b, true)
            val expected = a sshr b
            if (actual != expected) {
                println("sr32: FAILED sr32($a, $b, 1'b1) = $actual")
                fatal()
            }
            b++
        }

        println("sr32: Running random 32-bit inputs")
        repeat(1024) {
            a = randomUbit()
            b = randomUbit()
            val arith = randomBoolean()
            val actual = sr32(a, b, arith)
            val expected = if (arith) a sshr b else a shr b
            if (actual != expected) {
                println("sr32: FAILED sr32($a, $b, $arith) = $actual")
                fatal()
            }
        }

        println("sr32: PASSED")
        finish()
    }
}
