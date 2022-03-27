/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

@Entry
open class full_test : tinyalu_base_test {

    @Inj
    private val header = "`uvm_component_utils(${t<full_test>()});"

    lateinit var runall_seq: run_all_sequence

    @Task
    override fun run_phase(phase: uvm_phase?) {
        runall_seq = run_all_sequence("runall_seq")
        phase!!.raise_objection(this)
        runall_seq.start(null)
        phase.drop_objection(this)
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}
