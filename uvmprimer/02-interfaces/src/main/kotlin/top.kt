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

import dut.tinyalu
import io.verik.core.*

@Entry
object top : Module() {

    @Make
    val bfm = tinyalu_bfm()

    @Make
    val tester_i = tester(bfm)

    @Make
    val coverage_i = coverage(bfm)

    @Make
    val scoreboard_i = scoreboard(bfm)

    @Make
    val DUT = tinyalu(
        A = bfm.A,
        B = bfm.B,
        clk = bfm.clk,
        op = bfm.op,
        reset_n = bfm.reset_n,
        start = bfm.start,
        done = bfm.done,
        result = bfm.result
    )
}
