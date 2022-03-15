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

import io.verik.core.*

@Entry
class Top : Module() {

    var clk: Boolean = nc()
    var rst: Boolean = nc()

    @Make
    var rx = cluster(4) {
        RxInterface(clk)
    }

    @Make
    var tx = cluster(4) {
        TxInterface(clk)
    }

    @Make
    var router = AtmRouter(
        rx = rx.map { it.dut },
        tx = tx.map { it.dut },
        clk = clk,
        rst = rst
    )

    @Run
    fun runClk() {
        clk = false
        repeat(1000) {
            delay(20)
            clk = !clk
        }
        fatal("FAIL timeout")
    }

    @Make
    var test = AtmTest(
        rx.map { it.tb },
        tx.map { it.tb },
        rst
    )
}
