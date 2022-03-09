/*
 * Copyright (c) 2022 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
