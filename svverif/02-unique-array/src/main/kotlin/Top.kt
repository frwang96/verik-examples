/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

@Entry
class Top : Module() {

    @Run
    fun run() {
        val unique_array = UniqueArray(20, u("8'd20"))
        repeat(10) {
            unique_array.randomize()
            unique_array.display()
        }
    }
}
