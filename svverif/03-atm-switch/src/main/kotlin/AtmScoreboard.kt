/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

class AtmScoreboard(
    val stream_id: Int
) : Class() {

    val exp_mbx = Mailbox<AtmCell>()
    val rcv_mbx = Mailbox<AtmCell>()

    var rcv_cells = 0
    var exp_cells = 0
    var matched = 0
    var mismatched = 0

    @Task
    fun run() {
        fork {
            forever {
                val exp_ac = exp_mbx.get()
                exp_cells++
                val rcv_ac = rcv_mbx.get()
                rcv_cells++
                println("@${time()}: Scoreboard[$stream_id] exp=${exp_ac.vci} rcv=${rcv_ac.vci}")
                if (exp_ac.compare(rcv_ac)) matched++
                else mismatched++
            }
        }
    }

    fun report() {
        print("@${time()}: Scoreboard[$stream_id]")
        println(" exp_cells=$exp_cells rcv_cells=$rcv_cells matched=$matched mismatched=$mismatched")
    }
}
