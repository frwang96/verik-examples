/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ConvertSecondaryConstructorToPrimary", "ClassName")

import io.verik.core.*

class testbench : Class {

    val bfm: tinyalu_bfm

    lateinit var tester_h: tester
    lateinit var coverage_h: coverage
    lateinit var scoreboard_h: scoreboard

    constructor(b: tinyalu_bfm) : super() {
        bfm = b
    }

    @Task
    fun execute() {
        tester_h = tester(bfm)
        coverage_h = coverage(bfm)
        scoreboard_h = scoreboard(bfm)

        fork { tester_h.execute() }
        fork { coverage_h.execute() }
        fork { scoreboard_h.execute() }
    }
}
