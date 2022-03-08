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
@file:Suppress("ClassName", "EnumEntryName")

package dut

import io.verik.core.*

enum class operation_t(val value: Ubit<`3`>) {
    no_op(u(0b000)),
    add_op(u(0b001)),
    and_op(u(0b010)),
    xor_op(u(0b011)),
    mul_op(u(0b100)),
    rst_op(u(0b111))
}
