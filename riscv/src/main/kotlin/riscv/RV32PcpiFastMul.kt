/*
 * SPDX-License-Identifier: Apache-2.0
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