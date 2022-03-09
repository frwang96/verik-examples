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
@file:Suppress(
    "ClassName",
    "ConvertSecondaryConstructorToPrimary",
    "JoinDeclarationAndAssignment",
    "ReplaceWithOperatorAssignment"
)

package dice

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_subscriber
import io.verik.core.*

class average : uvm_subscriber<Int> {

    @Inj
    val header = "`uvm_component_utils(${t<average>()});"

    var dice_total: Double
    var count: Int

    constructor(name: String, parent: uvm_component?) : super(name, parent) {
        dice_total = 0.0
        count = 0
    }

    override fun write(t: Int) {
        dice_total = dice_total + t
        count++
    }

    override fun report_phase(phase: uvm_phase?) {
        println("DICE AVERAGE: ${dice_total / count}")
    }
}