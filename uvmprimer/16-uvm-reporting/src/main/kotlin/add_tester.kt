/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused")

import dut.operation_t
import dut.operation_t.add_op
import imported.uvm_pkg.uvm_component
import io.verik.core.*

class add_tester : random_tester {

    @Inj
    private val header = "`uvm_component_utils(${t<add_tester>()});"

    override fun get_op(): operation_t {
        return add_op
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}
