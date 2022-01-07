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

class RV32PcpiFastMul(
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

    var instr_mul: Boolean = nc()
    var instr_mulh: Boolean = nc()
    var instr_mulhsu: Boolean = nc()
    var instr_mulhu: Boolean = nc()
    var instr_any_mul: Boolean = nc()
    var instr_any_mulh: Boolean = nc()
    var instr_rs1_signed: Boolean = nc()
    var instr_rs2_signed: Boolean = nc()

    var shift_out: Boolean = nc()
    var active: Ubit<`4`> = nc()

    var rs1: Ubit<`33`> = nc()
    var rs2: Ubit<`33`> = nc()
    var rs1_q: Ubit<`33`> = nc()
    var rs2_q: Ubit<`33`> = nc()
    var rd: Ubit<`64`> = nc()
    var rd_q: Ubit<`64`> = nc()

    var pcpi_insn_valid: Boolean = nc()
    var pcpi_insn_valid_q: Boolean = nc()
}