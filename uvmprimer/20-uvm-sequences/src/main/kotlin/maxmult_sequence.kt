/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused")

import dut.operation_t.mul_op
import imported.uvm_pkg.uvm_sequence
import io.verik.core.*

open class maxmult_sequence : uvm_sequence<sequence_item, sequence_item> {

    @Inj
    private val header = "`uvm_object_utils(${t<maxmult_sequence>()});"

    lateinit var command: sequence_item

    constructor(name: String = "maxmult_sequence") : super(name)

    @Task
    override fun body() {
        command = inji("${t<sequence_item>()}::type_id::create(${"command"})")
        start_item(command)
        command.op = mul_op
        command.A = u(0xff)
        command.B = u(0xff)
        finish_item(command)
    }
}
