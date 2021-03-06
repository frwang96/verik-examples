/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

class ArbiterInterface(
    @In var clk: Boolean
) : ModuleInterface() {

    var grant: Ubit<`2`> = nc()
    var request: Ubit<`2`> = nc()
    var reset: Boolean = nc()

    @Make
    val cb = ArbiterClockingBlock(posedge(clk), request, grant)

    @Make
    val test_port = ArbiterTestPort(cb, reset)

    @Make
    val dut_port = ArbiterDutPort(request, reset, clk, grant)

    class ArbiterClockingBlock(
        override val event: Event,
        @Out var request: Ubit<`2`>,
        @In var grant: Ubit<`2`>
    ) : ClockingBlock()

    class ArbiterTestPort(
        val cb: ArbiterClockingBlock,
        @Out var reset: Boolean
    ) : ModulePort()

    class ArbiterDutPort(
        @In var request: Ubit<`2`>,
        @In var reset: Boolean,
        @In var clk: Boolean,
        @Out var grant: Ubit<`2`>
    ) : ModulePort()
}
