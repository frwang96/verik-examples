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

@file:Suppress("KotlinConstantConditions")

package tutorial03

import io.verik.core.*

@SimTop
class Main : Module() {

    @Run
    fun run() {
        boolean()
        ubit()
    }

    fun boolean() {
        val a = false
        val b = true
        println(!a)      // println(true)
        println(a || b)  // println(true)
        println(a && b)  // println(false)
        println(a xor b) // println(true)
    }

    fun ubit() {
        println(u(0))       // println(u("1'h0"))
        println(u(1))       // println(u("1'h1"))
        println(u(6))       // println(u("3'h6"))
        println(u(0x12))    // println(u("8'h12"))
        println(u(0b11010)) // println(u("5'b11010"))
    }
}
