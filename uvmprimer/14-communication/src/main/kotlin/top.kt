/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName")

import imported.uvm_pkg.run_test
import imported.uvm_pkg.uvm_config_db
import io.verik.core.*

@Entry
object top : Module() {

    @Make
    val clk_bfm_i = clk_bfm()

    @Run
    fun run() {
        uvm_config_db.set<clk_bfm>(null, "*", "clk_bfm_i", clk_bfm_i)
        run_test("communication_test")
    }
}
