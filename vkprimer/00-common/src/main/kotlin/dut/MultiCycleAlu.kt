/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

package dut

import io.verik.core.*

class MultiCycleAlu(
    @In var clk: Boolean,
    @In var reset_n: Boolean,
    @In var a: Ubit<`8`>,
    @In var b: Ubit<`8`>,
    @In var start: Boolean,
    @Out var done: Boolean,
    @Out var result: Ubit<`16`>
) : Module() {

    var a_int: Ubit<`8`> = nc()
    var b_int: Ubit<`8`> = nc()
    var mult1: Ubit<`16`> = nc()
    var mult2: Ubit<`16`> = nc()
    var done1: Boolean = nc()
    var done2: Boolean = nc()
    var done3: Boolean = nc()

    @Seq
    fun compute() {
        on(posedge(clk)) {
            if (!reset_n) {
                a_int = u0()
                b_int = u0()
                mult1 = u0()
                mult2 = u0()
                result = u0()
                done1 = false
                done2 = false
                done3 = false
                done = false
            } else {
                a_int = a
                b_int = b
                mult1 = a_int mul b_int
                mult2 = mult1
                result = mult2
                done1 = start && !done
                done2 = done1 && !done
                done3 = done2 && !done
                done = done3 && !done
            }
        }
    }
}
