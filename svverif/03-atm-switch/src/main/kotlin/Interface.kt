/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

class RxInterface(
    @In var clk: Boolean
) : ModuleInterface() {

    var data: Ubit<`8`> = nc()
    var soc: Boolean = nc()
    var en: Boolean = nc()
    var clav: Boolean = nc()
    var rclk: Boolean = nc()

    @Make
    val cb = RxClockingBlock(
        event = posedge(clk),
        data = data,
        soc = soc,
        clav = clav,
        en = en
    )

    @Make
    val dut = RxDutModulePort(
        en = en,
        rclk = rclk,
        data = data,
        soc = soc,
        clav = clav
    )

    @Make
    val tb = RxTbModulePort(cb)

    class RxClockingBlock(
        override val event: Event,
        @Out var data: Ubit<`8`>,
        @Out var soc: Boolean,
        @Out var clav: Boolean,
        @In var en: Boolean
    ) : ClockingBlock()

    class RxDutModulePort(
        @Out var en: Boolean,
        @Out var rclk: Boolean,
        @In var data: Ubit<`8`>,
        @In var soc: Boolean,
        @In var clav: Boolean
    ) : ModulePort()

    class RxTbModulePort(
        val cb: RxClockingBlock
    ) : ModulePort()
}

class TxInterface(
    @In var clk: Boolean
) : ModuleInterface() {

    var data: Ubit<`8`> = nc()
    var soc: Boolean = nc()
    var en: Boolean = nc()
    var clav: Boolean = nc()
    var tclk: Boolean = nc()

    @Make
    val cb = TxClockingBlock(
        event = posedge(clk),
        data = data,
        soc = soc,
        en = en,
        clav = clav
    )

    @Make
    val dut = TxDutModulePort(
        data = data,
        soc = soc,
        en = en,
        tclk = tclk,
        clav = clav
    )

    @Make
    val tb = TxTbModulePort(cb)

    class TxClockingBlock(
        override val event: Event,
        @In var data: Ubit<`8`>,
        @In var soc: Boolean,
        @In var en: Boolean,
        @Out var clav: Boolean
    ) : ClockingBlock()

    class TxDutModulePort(
        @Out var data: Ubit<`8`>,
        @Out var soc: Boolean,
        @Out var en: Boolean,
        @Out var tclk: Boolean,
        @In var clav: Boolean
    ) : ModulePort()

    class TxTbModulePort(
        val cb: TxClockingBlock
    ) : ModulePort()
}
