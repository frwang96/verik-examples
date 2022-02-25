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

class TxnIf(
    @In var clk: Boolean
) : ModuleInterface() {

    var rst: Boolean = nc()
    var req_op: Op = nc()
    var req_addr: UbitAddr = nc()
    var req_data: UbitData = nc()
    var rsp_vld: Boolean = nc()
    var rsp_data: UbitData = nc()

    @Make
    val tx = TxnTx(
        rst = rst,
        req_op = req_op,
        req_addr = req_addr,
        req_data = req_data,
        rsp_vld = rsp_vld,
        rsp_data = rsp_data
    )

    @Make
    val rx = TxnRx(
        rst = rst,
        req_op = req_op,
        req_addr = req_addr,
        req_data = req_data,
        rsp_vld = rsp_vld,
        rsp_data = rsp_data
    )

    @Make
    val cb = TxnCb(
        event = posedge(clk),
        rst = rst,
        req_op = req_op,
        req_addr = req_addr,
        req_data = req_data,
        rsp_vld = rsp_vld,
        rsp_data = rsp_data
    )

    @Make
    val tb = TxnTb(cb)

    class TxnTx(
        @Out var rst: Boolean,
        @Out var req_op: Op,
        @Out var req_addr: UbitAddr,
        @Out var req_data: UbitData,
        @In var rsp_vld: Boolean,
        @In var rsp_data: UbitData
    ) : ModulePort()

    class TxnRx(
        @In var rst: Boolean,
        @In var req_op: Op,
        @In var req_addr: UbitAddr,
        @In var req_data: UbitData,
        @Out var rsp_vld: Boolean,
        @Out var rsp_data: UbitData
    ) : ModulePort()

    class TxnCb(
        override val event: Event,
        @Out var rst: Boolean,
        @Out var req_op: Op,
        @Out var req_addr: UbitAddr,
        @Out var req_data: UbitData,
        @In var rsp_vld: Boolean,
        @In var rsp_data: UbitData
    ) : ClockingBlock()

    class TxnTb(
        val cb: TxnCb
    ) : ModulePort()
}
