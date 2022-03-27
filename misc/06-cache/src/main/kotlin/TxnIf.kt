/*
 * SPDX-License-Identifier: Apache-2.0
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
