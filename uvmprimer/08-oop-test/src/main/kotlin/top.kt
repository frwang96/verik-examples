/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName")

import dut.tinyalu
import io.verik.core.*

@Entry
object top : Module() {

    @Make
    val bfm = tinyalu_bfm()

    @Make
    val DUT = tinyalu(
        A = bfm.A,
        B = bfm.B,
        clk = bfm.clk,
        op = bfm.op,
        reset_n = bfm.reset_n,
        start = bfm.start,
        done = bfm.done,
        result = bfm.result
    )

    lateinit var testbench_h: testbench

    @Run
    fun test() {
        testbench_h = testbench(bfm)
        testbench_h.execute()
    }
}
