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

typealias IF_WIDTH = `8`

class UtopiaInterface : ModuleInterface() {

    var data: Ubit<IF_WIDTH> = nc()
    var clk_in: Boolean = nc()
    var clk_out: Boolean = nc()

    var soc: Boolean = nc()
    var en: Boolean = nc()
    var clav: Boolean = nc()
    var valid: Boolean = nc()
    var ready: Boolean = nc()
    var reset: Boolean = nc()
    var selected: Boolean = nc()

    var atm_cell: AtmCell = nc()

    @Make
    var top_rx = TopRxPort(
        data = data,
        soc = soc,
        clav = clav,
        clk_in = clk_in,
        reset = reset,
        ready = ready,
        clk_out = clk_out,
        en = en,
        atm_cell = atm_cell,
        valid = valid
    )

    @Make
    var top_tx = TopTxPort(
        clav = clav,
        selected = selected,
        clk_in = clk_in,
        clk_out = clk_out,
        atm_cell = atm_cell,
        data = data,
        soc = soc,
        en = en,
        valid = valid,
        reset = reset,
        ready = ready
    )

    @Make
    var core_rx = CoreRxPort(
        clk_in = clk_in,
        data = data,
        soc = soc,
        clav = clav,
        ready = ready,
        reset = reset,
        clk_out = clk_out,
        en = en,
        atm_cell = atm_cell,
        valid = valid
    )

    @Make
    var core_tx = CoreTxPort(
        clk_in = clk_in,
        clav = clav,
        atm_cell = atm_cell,
        valid = valid,
        reset = reset,
        clk_out = clk_out,
        data = data,
        soc = soc,
        en = en,
        ready = ready
    )

    @Make
    var cbr = RxClockingBlock(
        event = negedge(clk_out),
        clk_in = clk_in,
        clk_out = clk_out,
        atm_cell = atm_cell,
        valid = valid,
        reset = reset,
        en = en,
        ready = ready,
        data = data,
        soc = soc,
        clav = clav
    )

    @Make
    var test_rx = TestRxPort(cbr)

    @Make
    var cbt = TxClockingBlock(
        event = negedge(clk_out),
        clk_out = clk_out,
        clk_in = clk_in,
        atm_cell = atm_cell,
        soc = soc,
        en = en,
        valid = valid,
        reset = reset,
        data = data,
        ready = ready,
        clav = clav
    )

    @Make
    var test_tx = TestTxPort(cbt)

    class TopRxPort(
        @In var data: Ubit<IF_WIDTH>,
        @In var soc: Boolean,
        @In var clav: Boolean,
        @Out var clk_in: Boolean,
        @Out var reset: Boolean,
        @Out var ready: Boolean,
        @Out var clk_out: Boolean,
        @Out var en: Boolean,
        @Out var atm_cell: AtmCell,
        @Out var valid: Boolean
    ) : ModulePort()

    class TopTxPort(
        @In var clav: Boolean,
        @In var selected: Boolean,
        @Out var clk_in: Boolean,
        @Out var clk_out: Boolean,
        @Out var atm_cell: AtmCell,
        @Out var data: Ubit<IF_WIDTH>,
        @Out var soc: Boolean,
        @Out var en: Boolean,
        @Out var valid: Boolean,
        @Out var reset: Boolean,
        @Out var ready: Boolean
    ) : ModulePort()

    class CoreRxPort(
        @In var clk_in: Boolean,
        @In var data: Ubit<IF_WIDTH>,
        @In var soc: Boolean,
        @In var clav: Boolean,
        @In var ready: Boolean,
        @In var reset: Boolean,
        @Out var clk_out: Boolean,
        @Out var en: Boolean,
        @Out var atm_cell: AtmCell,
        @Out var valid: Boolean
    ) : ModulePort()

    class CoreTxPort(
        @In var clk_in: Boolean,
        @In var clav: Boolean,
        @In var atm_cell: AtmCell,
        @In var valid: Boolean,
        @In var reset: Boolean,
        @Out var clk_out: Boolean,
        @Out var data: Ubit<IF_WIDTH>,
        @Out var soc: Boolean,
        @Out var en: Boolean,
        @Out var ready: Boolean,
    ) : ModulePort()

    class RxClockingBlock(
        override val event: Event,
        @In var clk_in: Boolean,
        @In var clk_out: Boolean,
        @In var atm_cell: AtmCell,
        @In var valid: Boolean,
        @In var reset: Boolean,
        @In var en: Boolean,
        @In var ready: Boolean,
        @Out var data: Ubit<IF_WIDTH>,
        @Out var soc: Boolean,
        @Out var clav: Boolean
    ) : ClockingBlock()

    class TestRxPort(
        val cbr: RxClockingBlock
    ) : ModulePort()

    class TxClockingBlock(
        override val event: Event,
        @In var clk_out: Boolean,
        @In var clk_in: Boolean,
        @In var atm_cell: AtmCell,
        @In var soc: Boolean,
        @In var en: Boolean,
        @In var valid: Boolean,
        @In var reset: Boolean,
        @In var data: Ubit<IF_WIDTH>,
        @In var ready: Boolean,
        @Out var clav: Boolean
    ) : ClockingBlock()

    class TestTxPort(
        val cbt: TxClockingBlock
    ) : ModulePort()
}
