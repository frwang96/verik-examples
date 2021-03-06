/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName")

package dut

import io.verik.core.*

class three_cycle(
    @In var A: Ubit<`8`>,
    @In var B: Ubit<`8`>,
    @In var clk: Boolean,
    @In var reset_n: Boolean,
    @In var start: Boolean,
    @Out var done_mult: Boolean,
    @Out var result_mult: Ubit<`16`>
) : Module() {

    var a_int: Ubit<`8`> = nc()
    var b_int: Ubit<`8`> = nc()
    var mult1: Ubit<`16`> = nc()
    var mult2: Ubit<`16`> = nc()
    var done3: Boolean = nc()
    var done2: Boolean = nc()
    var done1: Boolean = nc()
    var done_mult_int: Boolean = nc()

    @Seq
    fun mult() {
        on(posedge(clk), negedge(reset_n)) {
            if (!reset_n) {
                done_mult_int = false
                done3 = false
                done2 = false
                done1 = false

                a_int = u0()
                b_int = u0()
                mult1 = u0()
                mult2 = u0()
                result_mult = u0()
            } else {
                a_int = A
                b_int = B
                mult1 = a_int mul b_int
                mult2 = mult1
                result_mult = mult2
                done3 = start && !done_mult_int
                done2 = done3 && !done_mult_int
                done1 = done2 && !done_mult_int
                done_mult_int = done1 && !done_mult_int
            }
        }
    }

    @Com
    fun set_done_mult() {
        done_mult = done_mult_int
    }
}
