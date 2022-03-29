/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

class AtmTest(
    val rx: Cluster<STREAMS, RxInterface.RxTbModulePort>,
    val tx: Cluster<STREAMS, TxInterface.TxTbModulePort>,
    @Out var rst: Boolean
) : Module() {

    val drv = ArrayList<AtmDriver>()
    val mon = ArrayList<AtmMonitor>()
    val scb = ArrayList<AtmScoreboard>()
    val driver_done: Event = nc()

    @Run
    fun test() {
        for (i in 0 until STREAMS_VAL) {
            scb.add(AtmScoreboard(i))
            drv.add(AtmDriver(scb[i].exp_mbx, i, rx[i]))
            mon.add(AtmMonitor(scb[i].rcv_mbx, i, tx[i]))
        }

        rst = false
        repeat(10) { wait(rx[0].cb) }
        rst = true
        for (i in 0 until STREAMS_VAL) {
            drv[i].run(5, driver_done)
            mon[i].run()
            scb[i].run()
        }

        wait(driver_done)
        delay(1000)

        for (i in 0 until STREAMS_VAL) {
            scb[i].report()
        }
        finish()
    }
}
