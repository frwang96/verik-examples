/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

class AtmRouter(
    val rx: Cluster<`4`, RxInterface.RxDutModulePort>,
    val tx: Cluster<`4`, TxInterface.TxDutModulePort>,
    @In var clk: Boolean,
    @In var rst: Boolean
) : Module() {

    @Com
    fun com() {
        for (i in 0 until 4) {
            rx[i].rclk = clk
            rx[i].en = false

            tx[i].tclk = clk
            tx[i].data = rx[i].data
            tx[i].soc = rx[i].soc
            tx[i].en = false
        }
    }
}
