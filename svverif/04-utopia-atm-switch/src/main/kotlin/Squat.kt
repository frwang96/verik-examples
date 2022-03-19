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

class Squat<NUM_RX : `*`, NUM_TX : `*`>(
    val rx: Cluster<NUM_RX, UtopiaInterface.TopRxPort>,
    val tx: Cluster<NUM_TX, UtopiaInterface.TopTxPort>,
    val mif: CpuInterface.PeripheralPort,
    @In var rst: Boolean,
    @In var clk: Boolean
) : Module() {

    val NUM_RX = i<NUM_RX>()
    val NUM_TX = i<NUM_TX>()

    @Make
    val lut = LookupTable<`12`, CellConfig>()

    @Seq
    var reset = oni(posedge(clk)) { rst }

    val write_cycle = u(0b010)
    val read_cycle = u(0b001)

    @Com
    fun lutWrite() {
        if (mif.bus_mode && cat(mif.sel, mif.rd_ds, mif.wr_rw) == write_cycle) {
            lut.write(mif.addr, mif.data_in)
        }
    }

    @Com
    fun lutRead() {
        mif.rdy_ack = floating
        mif.data_out = CellConfig(uz(), uz())
        if (mif.bus_mode) {
            when (cat(mif.sel, mif.rd_ds, mif.wr_rw)) {
                write_cycle -> mif.rdy_ack = false
                read_cycle -> {
                    mif.rdy_ack = false
                    mif.data_out = lut.read(mif.addr)
                }
            }
        }
    }

    @Make
    val atm_rx = cluster(NUM_RX) { UtopiaAtmRx(rx[it]) }

    @Com
    fun comRx() {
        for (i in 0 until NUM_RX) {
            rx[i].clk_in = clk
            rx[i].reset = reset
        }
    }

    @Make
    val atm_tx = cluster(NUM_TX) { UtopiaAtmTx(tx[it]) }

    @Com
    fun comTx() {
        for (i in 0 until NUM_TX) {
            tx[i].clk_in = clk
            tx[i].reset = reset
        }
    }
}
