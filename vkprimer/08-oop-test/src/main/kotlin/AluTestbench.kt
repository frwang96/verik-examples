/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

class AluTestbench(val bfm: AluBfm) : Class() {

    @Task
    fun execute() {
        val tester = AluTester(bfm)
        val coverage = AluCoverage(bfm)
        val scoreboard = AluScoreboard(bfm)
        fork { tester.execute() }
        fork { coverage.execute() }
        fork { scoreboard.execute() }
    }
}
