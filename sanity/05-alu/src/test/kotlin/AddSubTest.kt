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
object AddSubTest : Module() {

    @Run
    fun test() {
        println("addSub: Running random 32-bit inputs")
        repeat(1024) {
            val a: Ubit<`32`> = randomUbit()
            val b: Ubit<`32`> = randomUbit()
            val isSub = randomBoolean()
            val actual = addSub(a, b, isSub)
            val expected = if (isSub) a - b else a + b
            if (actual != expected) {
                println("addSub: FAILED addSub($a, $b, $isSub) = $actual")
                fatal()
            }
        }
        println("addSub: PASSED")
    }
}
