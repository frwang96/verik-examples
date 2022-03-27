/*
 * SPDX-License-Identifier: Apache-2.0
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