/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

@Entry
class Top : Module() {

    var clk: Boolean = nc()

    @Run
    fun toggleClk() {
        clk = false
        forever {
            delay(5)
            clk = !clk
        }
    }

    @Make
    val arb_if = ArbiterInterface(clk)

    @Make
    val arb = Arbiter(arb_if.dut_port)

    @Make
    val test = ArbiterTest(arb_if.test_port)
}
