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

class CacheTb(
    val if_tb: TxnIf.TxnTb
) : Module() {

    val mem: Unpacked<EXP<ADDR_WIDTH>, UbitData> = nc()
    var pass = true

    @Run
    fun runTest() {
        reset()
        repeat(200) { transact() }
        if (pass)
            finish()
        else
            fatal()
    }

    @Task
    fun reset() {
        for (i in 0 until mem.size) {
            mem[i] = u0()
        }
        wait(if_tb.cb)
        if_tb.cb.rst = true
        if_tb.cb.req_op = Op.NOP
        wait(if_tb.cb)
        if_tb.cb.rst = false
    }

    @Task
    fun transact() {
        repeat(3) { wait(if_tb.cb) }
        if (random(1) == 0) {
            // write mem
            val addr: UbitAddr = randomUbit()
            val data: UbitData = randomUbit()
            mem[addr] = data
            println("tb write addr=0x$addr data=0x$data")

            wait(if_tb.cb)
            if_tb.cb.req_op = Op.WRITE
            if_tb.cb.req_addr = addr
            if_tb.cb.req_data = data
            wait(if_tb.cb)
            if_tb.cb.req_op = Op.NOP
        } else {
            // read mem
            val addr: UbitAddr = randomUbit()
            println("tb read addr=0x$addr")

            wait(if_tb.cb)
            if_tb.cb.req_op = Op.READ
            if_tb.cb.req_addr = addr
            wait(if_tb.cb)
            if_tb.cb.req_op = Op.NOP

            while (!if_tb.cb.rsp_vld) wait(if_tb.cb)
            val data = if_tb.cb.rsp_data
            val expected = mem[addr]

            if (data == expected) {
                println("tb PASS data=0x$data expected=0x$expected")
            } else {
                println("tb FAIL data=0x$data expected=0x$expected")
                pass = false
            }
        }
    }
}
