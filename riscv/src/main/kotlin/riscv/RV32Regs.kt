/*
 * SPDX-License-Identifier: Apache-2.0
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
    fun seqRegs() {
        on(posedge(clk)) {
            if (wen) {
                regs[waddr.tru<`5`>().inv()] = wdata
            }
        }
    }

    @Com
    fun comRdata() {
        rdata1 = regs[raddr1.tru<`5`>().inv()]
        rdata2 = regs[raddr2.tru<`5`>().inv()]
    }
}
