/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused", "JoinDeclarationAndAssignment")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_test
import io.verik.core.*

@Entry
class add_test : uvm_test {

    @Inj
    val header = "`uvm_component_utils(${t<add_test>()});"

    val bfm: tinyalu_bfm = nc()

    constructor(name: String, parent: uvm_component?) : super(name, parent) {
        if (!uvm_config_db.get<tinyalu_bfm>(null, "*", "bfm", bfm)) fatal("Failed to get BFM")
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        val add_tester_h: add_tester
        val coverage_h: coverage
        val scoreboard_h: scoreboard

        phase!!.raise_objection(this)

        add_tester_h = add_tester(bfm)
        coverage_h = coverage(bfm)
        scoreboard_h = scoreboard(bfm)

        fork { coverage_h.execute() }
        fork { scoreboard_h.execute() }

        add_tester_h.execute()
        phase.drop_objection(this)
    }
}
