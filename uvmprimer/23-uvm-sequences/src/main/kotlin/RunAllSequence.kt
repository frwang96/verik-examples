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

import imported.uvm_pkg.uvm_sequence
import imported.uvm_pkg.uvm_top
import io.verik.core.*

open class RunAllSequence(name: String = "runall_sequence") : uvm_sequence<SequenceItem, SequenceItem>(name) {

    @Inj
    private val header = "`uvm_object_utils(${t<RunAllSequence>()});"

    val reset: ResetSequence = inji("${t<ResetSequence>()}::type_id::create(${"reset"})")
    val maxmult: MaxMultSequence = inji("${t<MaxMultSequence>()}::type_id::create(${"maxmult"})")
    val random: RandomSequence = inji("${t<RandomSequence>()}::type_id::create(${"random"})")

    val sequencer: Sequencer

    init {
        @Suppress("UNCHECKED_CAST")
        sequencer = uvm_top!!.find("*.environment.sequencer") as Sequencer
    }

    @Task
    override fun body() {
        reset.start(sequencer)
        maxmult.start(sequencer)
        random.start(sequencer)
    }
}
