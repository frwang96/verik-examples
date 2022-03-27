/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "JoinDeclarationAndAssignment", "ConvertSecondaryConstructorToPrimary")

import imported.uvm_pkg.uvm_sequence
import io.verik.core.*

open class parallel_sequence : uvm_sequence<sequence_item, sequence_item> {

    @Inj
    private val header = "`uvm_object_utils(${t<parallel_sequence>()});"

    val reset: reset_sequence
    val short_random: short_random_sequence
    val fibonacci: fibonacci_sequence

    constructor(name: String = "parallel_sequence") : super(name) {
        reset = inji("${t<reset_sequence>()}::type_id::create(${"reset"})")
        fibonacci = inji("${t<fibonacci_sequence>()}::type_id::create(${"fibonacci"})")
        short_random = inji("${t<short_random_sequence>()}::type_id::create(${"short_random"})")
    }

    @Task
    override fun body() {
        reset.start(m_sequencer)
        fork { fibonacci.start(m_sequencer) }
        fork { short_random.start(m_sequencer) }
        join()
    }
}
