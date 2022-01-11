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
object RcaTest : Module() {

    @Run
    fun test() {
        println("rca: Running random 32-bit inputs")
        repeat(1024) {
            val a: Ubit<`32`> = randomUbit()
            val b: Ubit<`32`> = randomUbit()
            val c = randomBoolean()
            val actual = rca<`32`>(a, b, c)
            val expected = a + b + if (c) u(1) else u(0)
            if (actual != expected) {
                println("rca: FAILED rca($a, $b, $c) = $actual")
                fatal()
            }
        }
        println("rca: PASSED")
    }
}
