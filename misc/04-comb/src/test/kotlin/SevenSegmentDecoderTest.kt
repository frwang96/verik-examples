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
object SevenSegmentDecoderTest : Module() {

    @Run
    fun test() {
        val expected: Ubit<`112`> = u("112'hfce7fe22fdb4cf6ee8a9c46a98b8")
        var actual: Ubit<`112`> = u0()
        var count: Ubit<`4`> = u0()
        do {
            val decoded = sevenSegmentDecoder(count)
            val a = decoded[6]
            val b = decoded[5]
            val c = decoded[4]
            val d = decoded[3]
            val e = decoded[2]
            val f = decoded[1]
            val g = decoded[0]
            println("sevenSegmentDecoder($count)")
            print(if (a || f) "+" else " ")
            print(if (a) "--" else "  ")
            println(if (a || b) "+" else " ")
            print(if (f) "|  " else "   ")
            println(if (b) "|" else " ")
            print(if (e || f || g) "+" else " ")
            print(if (g) "--" else "  ")
            println(if (b || c || g) "+" else " ")
            print(if (e) "|  " else "   ")
            println(if (c) "|" else " ")
            print(if (d || e) "+" else " ")
            print(if (d) "--" else "  ")
            println(if (c || d) "+" else " ")
            println()

            actual = (actual shl 7) or (decoded xor (cat(u(0b000), count) shl 3) xor count.ext<`*`>()).ext<`*`>()
            count++
        } while (count != u0<`*`>())
        println()
        println("sevenSegmentDecoder: ${if (actual != expected) "FAILED" else "PASS"}")
        if (actual != expected) fatal()
        else finish()
    }
}