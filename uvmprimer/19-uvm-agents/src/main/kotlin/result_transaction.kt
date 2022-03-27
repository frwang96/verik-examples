/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "JoinDeclarationAndAssignment")

import imported.uvm_pkg.uvm_comparer
import imported.uvm_pkg.uvm_object
import imported.uvm_pkg.uvm_transaction
import io.verik.core.*

open class result_transaction : uvm_transaction {

    @Inj
    private val header = "`uvm_object_utils(${t<result_transaction>()});"

    var result: Ubit<`16`> = u0()

    constructor(name: String = "") : super(name, null)

    override fun do_copy(rhs: uvm_object?) {
        val copied_transaction_h: result_transaction
        assert(rhs != null) {
            fatal("Tried to copy null transaction")
        }
        super.do_copy(rhs)
        copied_transaction_h = rhs as result_transaction
        result = copied_transaction_h.result
    }

    override fun convert2string(): String {
        val s: String
        s = "result:$result"
        return s
    }

    override fun do_compare(rhs: uvm_object?, comparer: uvm_comparer?): Boolean {
        val RHS: result_transaction
        var same: Boolean
        assert(rhs != null) {
            fatal("Tried to compare null transaction")
        }
        same = super.do_compare(rhs, comparer)
        RHS = rhs as result_transaction
        same = (result == RHS.result) && same
        return same
    }
}
