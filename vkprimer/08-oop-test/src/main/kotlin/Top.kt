/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import dut.Alu
import io.verik.core.*

@Entry
object Top : Module() {

    @Make
    val bfm = AluBfm()

    @Make
    val alu = Alu(
        clk = bfm.clk,
        reset_n = bfm.rst_n,
        a = bfm.a,
        b = bfm.b,
        op = bfm.op,
        start = bfm.start,
        done = bfm.done,
        result = bfm.result
    )

    @Run
    fun test() {
        val testbench = AluTestbench(bfm)
        testbench.execute()
    }
}
