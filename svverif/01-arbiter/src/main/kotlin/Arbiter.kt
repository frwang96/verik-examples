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

class Arbiter(
    val arb_if: ArbiterInterface.ArbiterDutPort
) : Module() {

    var last_winner: Boolean = nc()
    var winner: Boolean = nc()
    var next_grant: Ubit<`2`> = nc()

    var state: State = nc()
    var next_state: State = nc()

    @Com
    fun comNextGrant() {
        next_state = state
        winner = last_winner
        next_grant = arb_if.grant

        when (state) {
            State.IDLE -> {
                next_grant[0] = arb_if.request[0] && !(arb_if.request[1] && !last_winner)
                next_grant[1] = arb_if.request[1] && !(arb_if.request[0] && last_winner)
                if (next_grant[0]) {
                    winner = false
                    next_state = State.GRANT0
                }
                if (next_grant[1]) {
                    winner = true
                    next_state = State.GRANT1
                }
                if (next_grant == u(0b11)) {
                    error("two grants asserted simultaneously")
                }
            }
            State.GRANT0 -> {
                if (!arb_if.request[0]) {
                    next_grant[0] = false
                    next_state = State.IDLE
                }
            }
            State.GRANT1 -> {
                if (!arb_if.request[1]) {
                    next_grant[1] = false
                    next_state = State.IDLE
                }
            }
        }
    }

    @Seq
    fun seqGrant() {
        on(posedge(arb_if.clk), posedge(arb_if.reset)) {
            if (arb_if.reset) {
                state = State.IDLE
                last_winner = false
                arb_if.grant = u0()
            } else {
                state = next_state
                last_winner = winner
                arb_if.grant = next_grant
            }
        }
    }
}
