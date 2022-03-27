/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress(
    "ClassName",
    "JoinDeclarationAndAssignment",
    "FunctionName",
    "ConvertSecondaryConstructorToPrimary",
    "unused"
)

import imported.uvm_pkg.uvm_analysis_port
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

class result_monitor : uvm_component {

    @Inj
    val header = "`uvm_component_utils(${t<result_monitor>()});"

    val bfm: tinyalu_bfm = nc()
    lateinit var ap: uvm_analysis_port<result_transaction>

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    override fun build_phase(phase: uvm_phase?) {
        if (!uvm_config_db.get<tinyalu_bfm>(null, "*", "bfm", bfm)) {
            inj("`uvm_fatal(${"RESULT MONITOR"}, ${"Failed to get BFM"})")
        }
        ap = uvm_analysis_port("ap", this)
    }

    override fun connect_phase(phase: uvm_phase?) {
        bfm.result_monitor_h = this
    }

    fun write_to_monitor(r: Ubit<`16`>) {
        val result_t: result_transaction
        result_t = result_transaction("result_t")
        result_t.result = r
        ap.write(result_t)
    }
}
