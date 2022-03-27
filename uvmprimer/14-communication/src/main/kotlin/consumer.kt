/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_get_port
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

class consumer : uvm_component {

    @Inj
    val header = "`uvm_component_utils(${t<consumer>()});"

    lateinit var get_port_h: uvm_get_port<Int>
    lateinit var clk_bfm_i: clk_bfm
    var shared = 0

    override fun build_phase(phase: uvm_phase?) {
        get_port_h = uvm_get_port("get_port_h", this)
        if (!uvm_config_db.get<clk_bfm>(null, "*", "clk_bfm_i", clk_bfm_i)) fatal("Failed to get BFM")
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        forever {
            wait(posedge(clk_bfm_i.clk))
            if (get_port_h.try_get(shared)) {
                println("${time()}ns Received: $shared")
            }
        }
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}
