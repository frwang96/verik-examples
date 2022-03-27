/*
 * SPDX-License-Identifier: Apache-2.0
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
