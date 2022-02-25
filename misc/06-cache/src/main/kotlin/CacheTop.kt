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

@Entry
object CacheTop : Module() {

    var clk = false

    @Make
    val if_tb_cache = TxnIf(clk)

    @Make
    val if_cache_main_mem = TxnIf(clk)

    @Make
    val cache = Cache(
        clk = clk,
        if_rx = if_tb_cache.rx,
        if_tx = if_cache_main_mem.tx
    )

    @Make
    val main_mem = MainMem(
        clk = clk,
        if_rx = if_cache_main_mem.rx
    )

    @Make
    val tb = CacheTb(if_tb_cache.tb)

    @Run
    fun toggleClk() {
        forever {
            delay(1)
            clk = !clk
        }
    }
}
