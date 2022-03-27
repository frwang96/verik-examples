/*
 * SPDX-License-Identifier: Apache-2.0
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
