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
object FaTest : Module() {

    @Run
    fun test() {
        println("fa: Running tests")
        var a: Ubit<`3`> = u0()
        for (i in 0 until 8) {
            val actual = fa(a[0], a[1], a[2])
            val expected = cat(
                (a[0] && a[1]) || (a[0] && a[2]) || (a[1] && a[2]),
                a[0] xor a[1] xor a[2]
            )
            if (actual != expected) {
                println("fa: FAILED fa(${a[0]}, ${a[1]}, ${a[2]}) = $actual")
                fatal()
            }
            a++
        }
        println("fa: PASSED")
    }
}
