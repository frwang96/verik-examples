/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused")

import imported.uvm_pkg.uvm_sequence
import io.verik.core.*

open class random_sequence : uvm_sequence<sequence_item, sequence_item> {

    @Inj
    private val header = "`uvm_object_utils(${t<random_sequence>()});"

    lateinit var command: sequence_item

    constructor(name: String = "") : super(name)

    @Task
    override fun body() {
        repeat(100) {
            command = inji("${t<sequence_item>()}::type_id::create(${"command"})")
            start_item(command)
            command.randomize()
            finish_item(command)
        }
    }
}
