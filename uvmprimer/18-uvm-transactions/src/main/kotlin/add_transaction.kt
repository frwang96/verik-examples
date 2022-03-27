/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused")

import dut.operation_t.add_op
import io.verik.core.*

class add_transaction : command_transaction {

    @Inj
    private val header = "`uvm_object_utils(${t<add_transaction>()});"

    @Cons
    val add_only = c(op == add_op)

    constructor(name: String = "") : super(name)
}
