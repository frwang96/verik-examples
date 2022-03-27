/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName")

import imported.uvm_pkg.run_test
import io.verik.core.*

@Entry
class top : Module() {

    @Inj
    val header = """
        import dice_pkg::*;
    """.trimIndent()

    @Run
    fun run() {
        run_test("dice_test")
    }
}
