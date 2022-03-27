/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

@Entry
class add_test : random_test {

    @Inj
    private val header = "`uvm_component_utils(${t<add_test>()});"

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    override fun build_phase(phase: uvm_phase?) {
        inj("${t<random_tester>()}::type_id::set_type_override(${t<add_tester>()}::get_type());")
        super.build_phase(phase)
    }
}
