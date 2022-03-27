/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

class CpuInterface : ModuleInterface() {

    var bus_mode: Boolean = nc()
    var addr: Ubit<`12`> = nc()
    var sel: Boolean = nc()
    var data_in: CellConfig = nc()
    var data_out: CellConfig = nc()
    var rd_ds: Boolean = nc()
    var wr_rw: Boolean = nc()
    var rdy_ack: Boolean = nc()

    @Make
    val periph = PeripheralPort(
        bus_mode = bus_mode,
        addr = addr,
        sel = sel,
        data_in = data_in,
        rd_ds = rd_ds,
        wr_rw = wr_rw,
        data_out = data_out,
        rdy_ack = rdy_ack
    )

    @Make
    val test = TestPort(
        bus_mode = bus_mode,
        addr = addr,
        sel = sel,
        data_in = data_in,
        rd_ds = rd_ds,
        wr_rw = wr_rw,
        data_out = data_out,
        rdy_ack = rdy_ack
    )

    class PeripheralPort(
        @In var bus_mode: Boolean,
        @In var addr: Ubit<`12`>,
        @In var sel: Boolean,
        @In var data_in: CellConfig,
        @In var rd_ds: Boolean,
        @In var wr_rw: Boolean,
        @Out var data_out: CellConfig,
        @Out var rdy_ack: Boolean
    ) : ModulePort()

    class TestPort(
        @Out var bus_mode: Boolean,
        @Out var addr: Ubit<`12`>,
        @Out var sel: Boolean,
        @Out var data_in: CellConfig,
        @Out var rd_ds: Boolean,
        @Out var wr_rw: Boolean,
        @In var data_out: CellConfig,
        @In var rdy_ack: Boolean
    ) : ModulePort()
}
