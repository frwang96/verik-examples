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
object CmpTest : Module() {

    @Run
    fun test() {
        println("cmp: Running tests")
        var a: Ubit<`4`> = u0()
        for (i in 0 until 12) {
            val actual = cmp(a[0], a[1], a[2], a[3])
            val expected = cat(
                (a[0] == a[1]) && a[2],
                (!a[0] && a[1]) || ((a[0] == a[1]) && a[3])
            )
            if (actual != expected) {
                println("cmp: FAILED cmp(${a[0]}, ${a[1]}, ${a[2]}, ${a[3]}) = $actual")
                fatal()
            }
            a++
        }
        println("cmp: PASSED")
    }
}
