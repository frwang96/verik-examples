/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused", "JoinDeclarationAndAssignment")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

@Entry
open class parallel_test : tinyalu_base_test {

    @Inj
    private val header = "`uvm_component_utils(${t<parallel_test>()});"

    val parallel_h: parallel_sequence

    constructor(name: String, parent: uvm_component?) : super(name, parent) {
        parallel_h = parallel_sequence("parallel_h")
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        phase!!.raise_objection(this)
        parallel_h.start(sequencer_h)
        phase.drop_objection(this)
    }
}
