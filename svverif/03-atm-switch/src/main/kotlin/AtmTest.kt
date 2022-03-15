/*
 * Copyright (c) 2022 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Verik

import io.verik.core.*

class AtmTest(
    val rx: Cluster<`4`, RxInterface.RxTbModulePort>,
    val tx: Cluster<`4`, TxInterface.TxTbModulePort>,
    @Out var rst: Boolean
) : Module() {

    val drv = ArrayList<AtmDriver>()
    val mon = ArrayList<AtmMonitor>()
    val scb = ArrayList<AtmScoreboard>()
    val driver_done: Event = nc()

    @Run
    fun test() {
        for (i in 0 until 4) {
            scb.add(AtmScoreboard(i))
            drv.add(AtmDriver(scb[i].exp_mbx, i, rx[i]))
            mon.add(AtmMonitor(scb[i].rcv_mbx, i, tx[i]))
        }

        rst = false
        repeat(10) { wait(rx[0].cb) }
        rst = true
        for (i in 0 until 4) {
            drv[i].run(5, driver_done)
            mon[i].run()
            scb[i].run()
        }

        wait(driver_done)
        delay(1000)

        for (i in 0 until 4) {
            scb[i].report()
        }
        finish()
    }
}
