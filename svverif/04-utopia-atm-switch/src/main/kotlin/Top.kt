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
