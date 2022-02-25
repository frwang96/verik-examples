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

import dut.operation_t
import io.verik.core.*

class AddTransaction(name: String = "") : CommandTransaction(name) {

    @Inj
    private val header = "`uvm_object_utils(${t<AddTransaction>()});"

    @Cons
    val cons_op = c(op == operation_t.add_op)
}
