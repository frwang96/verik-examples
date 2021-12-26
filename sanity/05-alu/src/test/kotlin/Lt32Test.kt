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
object Lt32Test : Module() {

    @Run
    fun test() {
        println("lt32: Running exhaustive 4-bit inputs")
        var x: Ubit<`8`> = u0()
        for (i in 0 until 256) {
            val a: Ubit<`32`> = x.slice<`4`>(0).sext()
            val b: Ubit<`32`> = x.slice<`4`>(4).sext()
            var actual = lt32(a, b, false)
            var expected = (a < b)
            if (actual != expected) {
                println("lt32: FAILED lt32($a, $b, 1'b0) = $actual")
                fatal()
            }
            actual = lt32(a, b, true)
            expected = (s(a) < s(b))
            if (actual != expected) {
                println("lt32: FAILED lt32($a, $b, 1'b1) = $actual")
                fatal()
            }
            x++
        }

        println("lt32: Running random 32-bit inputs")
        repeat(1024) {
            val a: Ubit<`32`> = randomUbit()
            val b: Ubit<`32`> = randomUbit()
            val isSigned = randomBoolean()
            val actual = lt32(a, b, isSigned)
            val expected = if (isSigned) (s(a) < s(b)) else (a < b)
            if (actual != expected) {
                println("lt32: FAILED lt32($a, $b, $isSigned) = $actual")
                fatal()
            }
        }

        println("lt32: PASSED")
        finish()
    }
}
