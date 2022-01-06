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

package riscv

import io.verik.core.*

class RV32Regs(
    @In var clk: Boolean,
    @In var wen: Boolean,
    @In var waddr: Ubit<`6`>,
    @In var raddr1: Ubit<`6`>,
    @In var raddr2: Ubit<`6`>,
    @In var wdata: Ubit<`32`>,
    @Out var rdata1: Ubit<`32`>,
    @Out var rdata2: Ubit<`32`>
) : Module() {

    val regs: Unpacked<`31`, Ubit<`32`>> = nc()

    @Seq
    fun seqRegs() {}
}
