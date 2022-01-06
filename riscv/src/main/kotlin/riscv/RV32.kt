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

@SynthTop
class RV32(
    @In var clk: Boolean,
    @In var resetn: Boolean,
    @Out var trap: Boolean,

    // Memory interface
    @Out var mem_valid: Boolean,
    @Out var mem_instr: Boolean,
    @In var mem_ready: Boolean,
    @Out var mem_addr: Ubit<`32`>,
    @Out var mem_wdata: Ubit<`32`>,
    @Out var mem_wstrb: Ubit<`4`>,
    @In var mem_rdata: Ubit<`32`>,

    // Look-Ahead Interface
    @Out var mem_la_read: Boolean,
    @Out var mem_la_write: Boolean,
    @Out var mem_la_addr: Ubit<`32`>,
    @Out var mem_la_wdata: Ubit<`32`>,
    @Out var mem_la_wstrb: Ubit<`4`>,

    // Pico Co-Processor Interface (PCPI)
    @Out var pcpi_valid: Boolean,
    @Out var pcpi_insn: Ubit<`32`>,
    @Out var pcpi_rs1: Ubit<`32`>,
    @Out var pcpi_rs2: Ubit<`32`>,
    @In var pcpi_wr: Boolean,
    @In var pcpi_rd: Ubit<`32`>,
    @In var pcpi_wait: Boolean,
    @In var pcpi_ready: Boolean,

    // IRQ Interface
    @In var irq: Ubit<`32`>,
    @Out var eoi: Ubit<`32`>,

    // Trace Interface
    @Out var trace_valid: Boolean,
    @Out var trace_data: Ubit<`36`>
) : Module() {

    val IRQ_TIMER = 0
    val IRQ_EBREAK = 1
    val IRQ_BUSERROR = 2

    var count_cycle: Ubit<`64`> = nc()
    var count_instr: Ubit<`64`> = nc()
    var reg_pc: Ubit<`32`> = nc()
    var reg_next_pc: Ubit<`32`> = nc()
    var reg_op1: Ubit<`32`> = nc()
    var reg_op2: Ubit<`32`> = nc()
    var reg_out: Ubit<`32`> = nc()
    var reg_sh: Ubit<`5`> = nc()

    var next_insn_opcode: Ubit<`32`> = nc()
    var dbg_insn_opcode: Ubit<`32`> = nc()
    var dbg_insn_addr: Ubit<`32`> = nc()

    var dbg_mem_valid: Boolean = nc()
    var dbg_mem_instr: Boolean = nc()
    var dbg_mem_ready: Boolean = nc()
    var dbg_mem_addr: Ubit<`32`> = nc()
    var dbg_mem_wdata: Ubit<`32`> = nc()
    var dbg_mem_wstrb: Ubit<`4`> = nc()
    var dbg_mem_rdata: Ubit<`32`> = nc()

    var next_pc: Ubit<`32`> = nc()

    var irq_delay: Boolean = nc()
    var irq_active: Boolean = nc()
    var irq_mask: Ubit<`32`> = nc()
    var irq_pending: Ubit<`32`> = nc()
    var irq_timer: Ubit<`32`> = nc()

    // Internal PCPI Cores

    var pcpi_mul_wr: Boolean = nc()
    var pcpi_mul_rd: Ubit<`32`> = nc()
    var pcpi_mul_wait: Boolean = nc()
    var pcpi_mul_ready: Boolean = nc()

    var pcpi_div_wr: Boolean = nc()
    var pcpi_div_rd: Ubit<`32`> = nc()
    var pcpi_div_wait: Boolean = nc()
    var pcpi_div_ready: Boolean = nc()

    var pcpi_int_wr: Boolean = nc()
    var pcpi_int_rd: Ubit<`32`> = nc()
    var pcpi_int_wait: Boolean = nc()
    var pcpi_int_ready: Boolean = nc()

    @Com
    fun comPcpiMul() {
        pcpi_mul_wr = false
        pcpi_mul_rd = ux()
        pcpi_mul_wait = false
        pcpi_mul_ready = false
    }

    @Com
    fun comPcpiDiv() {
        pcpi_div_wr = false
        pcpi_div_rd = ux()
        pcpi_div_wait = false
        pcpi_div_ready = false
    }

    @Com
    fun comPcpiInt() {
        pcpi_int_wr = false
        pcpi_int_rd = ux()
        pcpi_int_wait = false
        pcpi_int_ready = false
    }

    // Memory Interface

    var mem_state: Ubit<`2`> = nc()
    var mem_wordsize: Ubit<`2`> = nc()
    var mem_rdata_word: Ubit<`32`> = nc()
    var mem_rdata_q: Ubit<`32`> = nc()
    var mem_do_prefetch: Boolean = nc()
    var mem_do_rinst: Boolean = nc()
    var mem_do_rdata: Boolean = nc()
    var mem_do_wdata: Boolean = nc()

    var mem_xfer: Boolean = nc()
    var mem_la_secondword: Boolean = nc()
    var mem_la_firstword_reg: Boolean = nc()
    var last_mem_valid: Boolean = nc()
    var mem_la_firstword: Boolean = nc()
    var mem_la_firstword_xfer: Boolean = nc()

    var prefetched_high_word: Boolean = nc()
    var clear_prefetched_high_word: Boolean = nc()
    var mem_16bit_buffer: Ubit<`16`> = nc()

    var mem_rdata_latched_noshuffle: Ubit<`32`> = nc()
    var mem_rdata_latched: Ubit<`32`> = nc()

    var mem_la_use_prefetched_high_word: Boolean = nc()
    var mem_busy: Boolean = nc()
    var mem_done: Boolean = nc()
}
