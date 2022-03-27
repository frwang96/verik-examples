/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused")

import imported.uvm_pkg.uvm_sequence
import imported.uvm_pkg.uvm_verbosity.UVM_MEDIUM
import io.verik.core.*

open class short_random_sequence : uvm_sequence<sequence_item, sequence_item> {

    @Inj
    private val header = "`uvm_object_utils(${t<short_random_sequence>()});"

    lateinit var command: sequence_item

    constructor(name: String = "") : super(name)

    @Task
    override fun body() {
        repeat(20) {
            command = inji("${t<sequence_item>()}::type_id::create(${"command"})")
            start_item(command)
            command.randomize()
            finish_item(command)
            inj("`uvm_info(${"SHORT RANDOM"}, ${"random command: ${command.convert2string()}"}, $UVM_MEDIUM)")
        }
    }
}
