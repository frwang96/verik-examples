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