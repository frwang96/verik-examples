/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

class AtmDriver(
    val exp_mbx: Mailbox<AtmCell>,
    val stream_id: Int,
    val rx: RxInterface.RxTbModulePort
) : Class() {

    @Task
    fun initialize() {
        println("@${time()}: Driver[$stream_id] started")
        rx.cb.clav = false
        rx.cb.soc = false
        wait(rx.cb)
    }

    @Task
    fun run(ncells: Int, driver_done: Event) {
        fork {
            initialize()
            for (it in 0 until ncells) {
                val ac = AtmCell()
                ac.randomize()
                if (ac.eot_cell) break
                driveCell(ac)
            }
            println("@${time()}: Driver[$stream_id] done")
            driver_done.trigger()
        }
    }

    @Task
    fun driveCell(ac: AtmCell) {
        delay(ac.delay)
        val bytes = ac.bytePack()
        println("@${time()}: Driver[$stream_id] driveCell start vci=${ac.vci}")

        wait(rx.cb)
        rx.cb.clav = true
        do {
            wait(rx.cb)
        } while (rx.cb.en)

        rx.cb.soc = true
        rx.cb.data = bytes[0]
        wait(rx.cb)
        rx.cb.soc = false
        rx.cb.data = bytes[1]
        for (i in 2 until ATM_CELL_SIZE) {
            wait(rx.cb)
            rx.cb.data = bytes[i]
        }

        wait(rx.cb)
        rx.cb.soc = floating
        rx.cb.clav = false
        rx.cb.data = uz()
        println("@${time()}: Driver[$stream_id] driveCell finish vci=${ac.vci}")
        exp_mbx.put(ac)
    }
}
