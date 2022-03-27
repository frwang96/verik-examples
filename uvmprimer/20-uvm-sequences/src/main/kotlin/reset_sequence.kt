/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused")

import dut.operation_t.rst_op
import imported.uvm_pkg.uvm_sequence
import io.verik.core.*

open class reset_sequence : uvm_sequence<sequence_item, sequence_item> {

    @Inj
    private val header = "`uvm_object_utils(${t<reset_sequence>()});"

    lateinit var command: sequence_item

    constructor(name: String = "") : super(name)

    @Task
    override fun body() {
        command = inji("${t<sequence_item>()}::type_id::create(${"command"})")
        start_item(command)
        command.op = rst_op
        finish_item(command)
    }
}
