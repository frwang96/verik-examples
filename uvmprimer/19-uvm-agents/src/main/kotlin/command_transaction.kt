/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "JoinDeclarationAndAssignment", "FunctionName", "unused",
    "ConvertSecondaryConstructorToPrimary"
)

import dut.operation_t
import imported.uvm_pkg.uvm_comparer
import imported.uvm_pkg.uvm_object
import imported.uvm_pkg.uvm_transaction
import io.verik.core.*

open class command_transaction : uvm_transaction {

    @Inj
    private val header = "`uvm_object_utils(${t<command_transaction>()});"

    @Rand var A: Ubit<`8`> = nc()
    @Rand var B: Ubit<`8`> = nc()
    @Rand var op: operation_t = nc()

    @Cons
    val data = c(
        "$A dist {${u(0x00)}:=1, [${u(0x01)} : ${u(0xfe)}]:=1, ${u(0xff)}:=1}",
        "$B dist {${u(0x00)}:=1, [${u(0x01)} : ${u(0xfe)}]:=1, ${u(0xff)}:=1}"
    )

    override fun do_copy(rhs: uvm_object?) {
        val copied_transaction_h: command_transaction
        if (rhs == null) {
            inj("`uvm_fatal(${"COMMAND TRANSACTION"}, ${"Tried to copy from a null pointer"})")
        }
        copied_transaction_h = rhs as command_transaction
        super.do_copy(rhs)
        A = copied_transaction_h.A
        B = copied_transaction_h.B
        op = copied_transaction_h.op
    }

    fun clone_me(): command_transaction {
        val clone: command_transaction
        val tmp: uvm_object
        tmp = this.clone()!!
        clone = tmp as command_transaction
        return clone
    }

    override fun do_compare(rhs: uvm_object?, comparer: uvm_comparer?): Boolean {
        val compared_transaction_h: command_transaction
        val same: Boolean
        if (rhs == null) {
            inj("`uvm_fatal(${"COMMAND TRANSACTION"}, ${"Tried to do comparison to a null pointer"})")
        }
        if (rhs !is command_transaction) {
            same = false
        } else {
            compared_transaction_h = rhs
            same = super.do_compare(rhs, comparer) &&
                (compared_transaction_h.A == A) &&
                (compared_transaction_h.B == B) &&
                (compared_transaction_h.op == op)
        }
        return same
    }

    override fun convert2string(): String {
        val s: String
        s = "A:$A B:$B op:$op"
        return s
    }

    constructor(name: String = "") : super(name, null)
}
