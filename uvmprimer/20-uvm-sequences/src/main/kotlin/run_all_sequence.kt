/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "JoinDeclarationAndAssignment", "ConvertSecondaryConstructorToPrimary", "UNCHECKED_CAST")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_sequence
import imported.uvm_pkg.uvm_top
import io.verik.core.*

open class run_all_sequence : uvm_sequence<sequence_item, sequence_item> {

    @Inj
    private val header = "`uvm_object_utils(${t<run_all_sequence>()});"

    val reset: reset_sequence
    val maxmult: maxmult_sequence
    val random: random_sequence
    val sequencer_h: sequencer
    val uvm_component_h: uvm_component?

    constructor(name: String = "runall_sequence") : super(name) {
        uvm_component_h = uvm_top!!.find("*.env_h.sequencer_h")
        if (uvm_component_h == null) {
            inj("`uvm_fatal(${"RUNALL SEQUENCE"}, ${"Failed to get the sequencer"})")
        }
        sequencer_h = uvm_component_h as sequencer
        reset = inji("${t<reset_sequence>()}::type_id::create(${"reset"})")
        maxmult = inji("${t<maxmult_sequence>()}::type_id::create(${"maxmult"})")
        random = inji("${t<random_sequence>()}::type_id::create(${"random"})")
    }

    @Task
    override fun body() {
        reset.start(sequencer_h)
        maxmult.start(sequencer_h)
        random.start(sequencer_h)
    }
}
