/*
 * SPDX-License-Identifier: Apache-2.0
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
