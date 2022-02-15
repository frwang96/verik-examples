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

class Cache(
    @In var clk: Boolean,
    val if_rx: TxnIf.TxnRx,
    val if_tx: TxnIf.TxnTx
) : Module() {

    val lines: Unpacked<EXP<INDEX_WIDTH>, Line> = nc()
    var state: State = nc()
    var cur_op: Op = nc()
    var cur_addr: UbitAddr = nc()
    var cur_data: UbitData = nc()

    @Seq
    fun update() {
        on(posedge(clk)) {
            if_rx.rsp_vld = false
            if_tx.rst = false
            if_tx.req_op = Op.NOP
            if (if_rx.rst) {
                if_tx.rst = true
                state = State.READY
                for (i in 0 until lines.size) {
                    lines[i] = Line(Status.INVALID, u0(), u0())
                }
            } else {
                when (state) {
                    State.READY -> {
                        if (if_rx.req_op != Op.NOP) {
                            println("cache received op=${if_rx.req_op} addr=0x${if_rx.req_addr} data=0x${if_rx.req_data}")
                            cur_op = if_rx.req_op
                            cur_addr = if_rx.req_addr
                            cur_data = if_rx.req_data

                            val tag = getTag(if_rx.req_addr)
                            val index = getIndex(if_rx.req_addr)
                            val line = lines[index]
                            if (line.status != Status.INVALID && line.tag == tag) {
                                print("cache hit index=0x$index tag=0x$tag line.tag=0x${line.tag} line.status=${line.status}")
                                if (if_rx.req_op == Op.WRITE) {
                                    lines[index].data = if_rx.req_data
                                    lines[index].status = Status.DIRTY
                                } else {
                                    if_rx.rsp_vld = true
                                    if_rx.rsp_data = line.data
                                }
                            } else {
                                print("cache miss index=0x$index tag=0x$tag line.tag=0x${line.tag} line.status=${line.status}")
                                if (line.status == Status.DIRTY) {
                                    if_tx.req_op = Op.WRITE
                                    if_tx.req_addr = cat(line.tag, index)
                                    if_tx.req_data = line.data
                                    state = State.WRITEBACK
                                } else {
                                    if_tx.req_op = Op.READ
                                    if_tx.req_addr = if_rx.req_addr
                                    state = State.FILL
                                }
                            }
                        }
                    }
                    State.WRITEBACK -> {
                        if_tx.req_op = Op.READ
                        if_tx.req_addr = cur_addr
                        state = State.FILL
                    }
                    State.FILL -> {
                        if (if_tx.rsp_vld) {
                            val tag = getTag(cur_addr)
                            val index = getIndex(cur_addr)
                            println("cache fill index=0x$index tag=0x$tag data=0x${if_tx.rsp_data}")
                            lines[index] = Line(Status.CLEAN, tag, if_tx.rsp_data)
                            if (cur_op == Op.WRITE) {
                                lines[index].data = cur_data
                                lines[index].status = Status.DIRTY
                            } else {
                                if_rx.rsp_vld = true
                                if_rx.rsp_data = if_tx.rsp_data
                            }
                            state = State.READY
                        }
                    }
                }
            }
        }
    }

    private fun getTag(addr: UbitAddr): UbitTag {
        return addr[i<ADDR_WIDTH>() - 1, i<ADDR_WIDTH>() - i<TAG_WIDTH>()]
    }

    private fun getIndex(addr: UbitAddr): UbitIndex {
        return addr.tru()
    }
}
