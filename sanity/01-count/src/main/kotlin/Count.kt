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

typealias WIDTH = `8`

@Entry
object Count : Module() {

    var clk: Boolean = nc()
    var rst: Boolean = nc()
    var count: Ubit<WIDTH> = nc()

    @Seq
    fun update() {
        on(posedge(clk)) {
            println("@${time()} count=$count")
            if (rst) count = u0()
            else count += u(1)
        }
    }

    @Run
    fun runClk() {
        clk = false
        forever {
            delay(1)
            clk = !clk
        }
    }

    @Run
    fun runRst() {
        rst = true
        delay(2)
        rst = false
        delay(16)
        finish()
    }
}
