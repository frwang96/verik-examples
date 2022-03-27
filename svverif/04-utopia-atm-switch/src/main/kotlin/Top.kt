/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

typealias TX_PORTS = `4`
typealias RX_PORTS = `4`

val TX_PORTS_VAL = i<TX_PORTS>()
val RX_PORTS_VAL = i<RX_PORTS>()

@Entry
class Top : Module() {

    var clk: Boolean = nc()
    var rst: Boolean = nc()

    @Run
    fun toggleClk() {
        rst = false
        clk = false
        delay(5)
        rst = true
        delay(5)
        clk = true
        delay(5)
        rst = false
        clk = false
        repeat(1000) {
            delay(5)
            clk = !clk
        }
        fatal("FAIL timeout")
    }

    @Make
    val rx = cluster(RX_PORTS_VAL) { UtopiaInterface() }

    @Make
    val tx = cluster(TX_PORTS_VAL)  { UtopiaInterface() }

    @Make
    val mif = CpuInterface()

    @Make
    val squat = Squat<TX_PORTS, RX_PORTS>(
        rx = rx.map { it.top_rx },
        tx = tx.map { it.top_tx },
        mif = mif.periph,
        rst = rst,
        clk = clk
    )
}
