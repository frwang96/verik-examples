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
    val tester_i = tester(bfm)

    @Make
    val coverage_i = coverage(bfm)

    @Make
    val scoreboard_i = scoreboard(bfm)

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
}
