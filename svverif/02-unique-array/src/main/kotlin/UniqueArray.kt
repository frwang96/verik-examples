/*
 * SPDX-License-Identifier: Apache-2.0
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
