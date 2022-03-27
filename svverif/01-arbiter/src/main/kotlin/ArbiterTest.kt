/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

class ArbiterTest(
    val arb_if: ArbiterInterface.ArbiterTestPort
) : Module() {

    @Task
    private fun resetTest() {
        println("resetTest: asserting and checking reset")
        arb_if.reset = false
        delay(100)
        arb_if.reset = true
        arb_if.cb.request = u0()
        repeat(2) { wait(arb_if.cb) }
        arb_if.reset = false
        wait(arb_if.cb)
        assert(arb_if.cb.grant.eqz())
    }

    @Task
    private fun requestGrantTest(request: Ubit<`2`>, grant: Ubit<`2`>) {
        wait(arb_if.cb)
        arb_if.cb.request = request
        println("@${time()}: drove req=$request")
        repeat(2) { wait(arb_if.cb) }
        assert(arb_if.cb.grant == grant)
    }

    @Run
    fun test() {
        repeat(10) { wait(arb_if.cb) }
        resetTest()

        monitor("@${time()}: grant=${arb_if.cb.grant.toBinString()}")
        requestGrantTest(u(0b01), u(0b01))
        requestGrantTest(u(0b00), u(0b00))
        requestGrantTest(u(0b10), u(0b10))
        requestGrantTest(u(0b00), u(0b00))
        requestGrantTest(u(0b11), u(0b01))
        requestGrantTest(u(0b00), u(0b00))

        repeat(10) { wait(arb_if.cb) }
        finish()
    }
}