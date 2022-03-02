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
