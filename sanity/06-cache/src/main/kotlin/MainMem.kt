/*
 * Copyright (c) 2021 Francis Wang
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

class MainMem(
    @In var clk: Boolean,
    val if_rx: TxnIf.TxnRx
) : Module() {

    var mem: Unpacked<EXP<ADDR_WIDTH>, UbitData> = nc()

    @Seq
    fun update() {
        on(posedge(clk)) {
            if_rx.rsp_vld = false
            if (if_rx.rst) {
                for (i in 0 until mem.size) {
                    mem[i] = u0()
                }
            } else {
                if (if_rx.req_op != Op.NOP) {
                    println("mem received op=${if_rx.req_op} addr=0x${if_rx.req_addr} data=0x${if_rx.req_data}")
                    if (if_rx.req_op == Op.WRITE) {
                        mem[if_rx.req_addr] = if_rx.req_data
                    } else {
                        if_rx.rsp_data = mem[if_rx.req_addr]
                        if_rx.rsp_vld = true
                    }
                }
            }
        }
    }
}
