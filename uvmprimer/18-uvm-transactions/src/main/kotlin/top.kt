/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName")

import dut.tinyalu
import imported.uvm_pkg.run_test
import imported.uvm_pkg.uvm_config_db
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

    @Run
    fun run() {
        uvm_config_db.set<tinyalu_bfm>(null, "*", "bfm", bfm)
        run_test()
    }
}
