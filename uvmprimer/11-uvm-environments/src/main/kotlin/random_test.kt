/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_test
import io.verik.core.*

@Entry
class random_test : uvm_test {

    @Inj
    val header = "`uvm_component_utils(${t<random_test>()});"

    lateinit var env_h: env

    override fun build_phase(phase: uvm_phase?) {
        inj("${t<base_tester>()}::type_id::set_type_override(${t<random_tester>()}::get_type());")
        env_h = inji("${t<env>()}::type_id::create(${"env_h"}, $this);")
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}
