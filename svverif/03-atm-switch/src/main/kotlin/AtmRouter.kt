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

class AtmRouter(
    val rx0: RxInterface.RxDutModulePort,
    val rx1: RxInterface.RxDutModulePort,
    val rx2: RxInterface.RxDutModulePort,
    val rx3: RxInterface.RxDutModulePort,
    val tx0: TxInterface.TxDutModulePort,
    val tx1: TxInterface.TxDutModulePort,
    val tx2: TxInterface.TxDutModulePort,
    val tx3: TxInterface.TxDutModulePort,
    @In var clk: Boolean,
    @In var rst: Boolean
) : Module() {

    @Com
    fun com() {
        rx0.rclk = clk
        rx1.rclk = clk
        rx2.rclk = clk
        rx3.rclk = clk

        rx0.en = false
        rx1.en = false
        rx2.en = false
        rx3.en = false

        tx0.tclk = clk
        tx1.tclk = clk
        tx2.tclk = clk
        tx3.tclk = clk

        tx0.data = rx0.data
        tx1.data = rx1.data
        tx2.data = rx2.data
        tx3.data = rx3.data

        tx0.soc = rx0.soc
        tx1.soc = rx1.soc
        tx2.soc = rx2.soc
        tx3.soc = rx3.soc

        tx0.en = false
        tx1.en = false
        tx2.en = false
        tx3.en = false
    }
}
