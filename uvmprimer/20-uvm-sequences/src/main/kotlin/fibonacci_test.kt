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
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused", "JoinDeclarationAndAssignment")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

@Entry
open class fibonacci_test : tinyalu_base_test {

    @Inj
    private val header = "`uvm_component_utils(${t<fibonacci_test>()});"

    @Task
    override fun run_phase(phase: uvm_phase?) {
        val fibonacci: fibonacci_sequence
        fibonacci = fibonacci_sequence("fibonacci")

        phase!!.raise_objection(this)
        fibonacci.start(sequencer_h)
        phase.drop_objection(this)
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}