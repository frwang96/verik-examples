/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused", "JoinDeclarationAndAssignment")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

@Entry
open class fibonacci_test : tinyalu_base_test {

    @Inj
    private val header = "`uvm_component_utils(${t<fibonacci_test>()});"

    @Task
    override fun run_phase(phase: uvm_phase?) {
        val fibonacci: fibonacci_sequence
        fibonacci = fibonacci_sequence("fibonacci")

        phase!!.raise_objection(this)
        fibonacci.start(sequencer_h)
        phase.drop_objection(this)
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}
