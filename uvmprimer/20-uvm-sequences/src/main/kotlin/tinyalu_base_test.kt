/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ConvertSecondaryConstructorToPrimary")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_test
import io.verik.core.*

abstract class tinyalu_base_test : uvm_test {

    @Inj
    private val header = "`uvm_component_utils(${t<tinyalu_base_test>()});"

    lateinit var env_h: env
    lateinit var sequencer_h: sequencer

    override fun build_phase(phase: uvm_phase?) {
        env_h = inji("${t<env>()}::type_id::create(${"env_h"}, $this);")
    }

    override fun end_of_elaboration() {
        sequencer_h = env_h.sequencer_h
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}
