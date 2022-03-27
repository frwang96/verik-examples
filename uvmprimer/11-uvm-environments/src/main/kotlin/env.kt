/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_env
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

class env : uvm_env {

    @Inj
    val header = "`uvm_component_utils(${t<env>()});"

    lateinit var tester_h: base_tester
    lateinit var coverage_h: coverage
    lateinit var scoreboard_h: scoreboard

    override fun build_phase(phase: uvm_phase?) {
        tester_h = inji("${t<base_tester>()}::type_id::create(${"tester_h"}, $this);")
        coverage_h = inji("${t<coverage>()}::type_id::create(${"coverage_h"}, $this);")
        scoreboard_h = inji("${t<scoreboard>()}::type_id::create(${"scoreboard_h"}, $this);")
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}
