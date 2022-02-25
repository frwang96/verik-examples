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

import imported.uvm_pkg.uvm_comparer
import imported.uvm_pkg.uvm_object
import imported.uvm_pkg.uvm_transaction
import io.verik.core.*

open class ResultTransaction(name: String = "") : uvm_transaction(name, null) {

    @Inj
    private val header = "`uvm_object_utils(${t<ResultTransaction>()});"

    var result: Ubit<`16`> = u0()

    override fun do_copy(rhs: uvm_object?) {
        super.do_copy(rhs)
        if (rhs !is ResultTransaction) {
            inj("`uvm_fatal(${"RESULT TRANSACTION"}, ${"Argument is of wrong type"})")
            return
        }
        result = rhs.result
    }

    override fun do_compare(rhs: uvm_object?, comparer: uvm_comparer?): Boolean {
        return if (rhs !is ResultTransaction) false
        else super.do_compare(rhs, comparer) && (rhs.result == result)
    }

    override fun toString(): String {
        return "result=$result"
    }
}
