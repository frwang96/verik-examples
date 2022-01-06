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

@SimTop
object RV32BasicTest : Module() {

    var clk: Boolean = nc()
    var resetn: Boolean = nc()

    @Run
    fun runClk() {
        clk = true
        forever {
            delay(5)
            clk = !clk
        }
    }

    @Run
    fun runResetn() {
        resetn = false
        repeat(100) { wait(posedge(clk)) }
        resetn = true
        repeat(1000) { wait(posedge(clk)) }
        finish()
    }

    var mem_valid: Boolean = nc()
    var mem_instr: Boolean = nc()
    var mem_ready: Boolean = nc()
    var mem_addr: Ubit<`32`> = nc()
    var mem_wdata: Ubit<`32`> = nc()
    var mem_wstrb: Ubit<`4`> = nc()
    var mem_rdata: Ubit<`32`> = nc()

    @Seq
    fun printMem() {
        on (posedge(clk)) {
            if (mem_valid && mem_ready) {
                when {
                    mem_instr ->
                        println("ifetch 0x$mem_addr: 0x$mem_rdata")
                    mem_wstrb != u0<`4`>() ->
                        println("write  0x$mem_addr: 0x$mem_wdata (wstrb=0x$mem_wstrb)")
                    else ->
                        println("read   0x$mem_addr: 0x$mem_rdata")
                }
            }
        }
    }

    @Make
    val rv32 = RV32(
        clk = clk,
        resetn = resetn,
        trap = nc(),
        mem_valid = mem_valid,
        mem_instr = mem_instr,
        mem_ready = mem_ready,
        mem_addr = mem_addr,
        mem_wdata = mem_wdata,
        mem_wstrb = mem_wstrb,
        mem_rdata = mem_rdata,
        mem_la_read = nc(),
        mem_la_write = nc(),
        mem_la_addr = nc(),
        mem_la_wdata = nc(),
        mem_la_wstrb = nc(),
        pcpi_valid = nc(),
        pcpi_insn = nc(),
        pcpi_rs1 = nc(),
        pcpi_rs2 = nc(),
        pcpi_wr = false,
        pcpi_rd = u0(),
        pcpi_wait = false,
        pcpi_ready = false,
        irq = u0(),
        eoi = nc(),
        trace_valid = nc(),
        trace_data = nc()
    )

    var memory: Unpacked<`256`, Ubit<`32`>> = nc()

    @Run
    fun initMemory() {
        memory[0] = u("32'h3fc00093") //       li   x1, 1020
        memory[1] = u("32'h0000a023") //       sw   x0, 0(x1)
        memory[2] = u("32'h0000a103") // loop: lw   x2, 0(x1)
        memory[3] = u("32'h00110113") //       addi x2, x2, 1
        memory[4] = u("32'h0020a023") //       sw   x2, 0(x1)
        memory[5] = u("32'hff5ff06f") //       j    <loop>
    }

    @Seq
    fun seqMem() {
        on(posedge(clk)) {
            mem_ready = false
            if (mem_valid && !mem_ready) {
                if (mem_addr < u("32'd1024")) {
                    val index = (mem_addr shr 2).tru<`8`>()
                    mem_ready = true
                    mem_rdata = memory[index]
                    if (mem_wstrb[0]) memory[index][0] = mem_wdata.slice<`8`>(0)
                    if (mem_wstrb[1]) memory[index][8] = mem_wdata.slice<`8`>(8)
                    if (mem_wstrb[2]) memory[index][16] = mem_wdata.slice<`8`>(16)
                    if (mem_wstrb[3]) memory[index][24] = mem_wdata.slice<`8`>(24)
                }
                // add memory-mapped IO here
            }
        }
    }
}