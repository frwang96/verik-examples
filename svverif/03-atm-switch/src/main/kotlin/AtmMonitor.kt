/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("DEPRECATED_IDENTITY_EQUALS")

import io.verik.core.*

class AtmMonitor(
    val rcv_mbx: Mailbox<AtmCell>,
    val stream_id: Int,
    val tx: TxInterface.TxTbModulePort
) : Class() {

    @Task
    fun run() {
        fork {
            tx.cb.clav = false
            wait(tx.cb)

            println("@${time()}: Monitor[$stream_id] start")
            forever {
                receiveCell()
            }
        }
    }

    @Task
    private fun receiveCell() {
        val ac = AtmCell()
        val bytes = ArrayList<Ubit<`8`>>()
        wait(tx.cb)

        tx.cb.clav = true
        while (inji("${tx.cb.soc} !== ${true}")) wait(tx.cb) // TODO replace operator

        for (i in 0 until ATM_CELL_SIZE) {
            while (tx.cb.en) wait(tx.cb)
            bytes.add(tx.cb.data)
            wait(tx.cb)
            tx.cb.clav = false
        }

        ac.byteUnpack(bytes)
        println("@${time()}: Monitor[$stream_id] received vci=${ac.vci}")
        rcv_mbx.put(ac)
    }
}
