/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName")

import io.verik.core.*

class clk_bfm : ModuleInterface() {

    var clk: Boolean = nc()

    @Run
    fun runClk() {
        clk = false
        forever {
            delay(10)
            clk = !clk
        }
    }
}
