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

class RV32PcpiDiv(
    @In var clk: Boolean,
    @In var resetn: Boolean,
    @In var pcpi_valid: Boolean,
    @In var pcpi_insn: Ubit<`32`>,
    @In var pcpi_rs1: Ubit<`32`>,
    @In var pcpi_rs2: Ubit<`32`>,
    @Out var pcpi_wr: Boolean,
    @Out var pcpi_rd: Ubit<`32`>,
    @Out var pcpi_wait: Boolean,
    @Out var pcpi_ready: Boolean
) : Module() {

    var instr_div: Boolean = nc()
    var instr_divu: Boolean = nc()
    var instr_rem: Boolean = nc()
    var instr_remu: Boolean = nc()
    var instr_any_div_rem: Boolean = nc()

    var pcpi_wait_q: Boolean = nc()
    var start: Boolean = nc()

    var dividend: Ubit<`32`> = nc()
    var divisor: Ubit<`63`> = nc()
    var quotient: Ubit<`32`> = nc()
    var quotient_msk: Ubit<`32`> = nc()
    var running: Boolean = nc()
    var outsign: Boolean = nc()
}