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

import dut.Op
import io.verik.core.*

class AluCoverage(
    val bfm: AluBfm
) : Module() {

    @Run
    fun runCoverage() {
        val op_cg = OpCoverGroup(bfm.op, bfm.a, bfm.b)
        forever {
            wait(negedge(bfm.clk))
            op_cg.sample()
        }
    }

    class OpCoverGroup(
        @In var op: Op,
        @In var a: Ubit<`8`>,
        @In var b: Ubit<`8`>,
    ) : CoverGroup() {

        @Cover
        val cp_op = cp(op)

        @Cover
        val cp_a = cp(a) {
            bin("zeros", "{${u(0x00)}}")
            bin("ones", "{${u(0xff)}}")
            bin("others", "{[${u(0x01)}:${u(0xfe)}]}")
        }

        @Cover
        val cp_b = cp(b) {
            bin("zeros", "{${u(0x00)}}")
            bin("ones", "{${u(0xff)}}")
            bin("others", "{[${u(0x01)}:${u(0xfe)}]}")
        }

        @Cover
        val cc_op_a_b = cc(cp_op, cp_a, cp_b) {
            ignoreBin("others", "binsof($cp_a.others) && binsof($cp_b.others)")
        }
    }
}
