/*
 * Copyright (c) 2022 Francis Wang
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

class UniqueArray(
    var max_size: Int,
    var max_value: Ubit<`8`>
) : Class() {

    @Rand
    var size: Int = 0

    lateinit var a: ArrayList<Ubit<`8`>>

    @Cons
    val c_size = c("$size inside {[1:$max_size]}")

    init {
        if (max_value < max_size.toUbit()) {
            max_value = max_size.toUbit()
        }
    }

    override fun postRandomize() {
        a = ArrayList<Ubit<`8`>>()
        val rand_range = RandRange(max_value)
        for (i in 0 until size) {
            rand_range.randomize()
            a.add(rand_range.value)
        }
    }

    fun display() {
        print("a:")
        a.forEach { print(" $it") }
        println()
    }
}
