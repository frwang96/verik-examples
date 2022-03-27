/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

@Entry
class Top : Module() {

    var clk: Boolean = nc()
    var rst: Boolean = nc()

    @Make
    var rx = cluster(4) {
        RxInterface(clk)
    }

    @Make
    var tx = cluster(4) {
        TxInterface(clk)
    }

    @Make
    var router = AtmRouter(
        rx = rx.map { it.dut },
        tx = tx.map { it.dut },
        clk = clk,
        rst = rst
    )

    @Run
    fun runClk() {
        clk = false
        repeat(1000) {
            delay(20)
            clk = !clk
        }
        fatal("FAIL timeout")
    }

    @Make
    var test = AtmTest(
        rx.map { it.tb },
        tx.map { it.tb },
        rst
    )
}
