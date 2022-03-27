/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "JoinDeclarationAndAssignment")

import dut.operation_t
import dut.operation_t.*
import imported.uvm_pkg.uvm_comparer
import imported.uvm_pkg.uvm_object
import imported.uvm_pkg.uvm_sequence_item
import io.verik.core.*

class sequence_item : uvm_sequence_item {

    @Inj
    private val header = "`uvm_object_utils(${t<sequence_item>()});"

    constructor(name: String = "") : super(name)

    @Rand var A: Ubit<`8`> = nc()
    @Rand var B: Ubit<`8`> = nc()
    @Rand var op: operation_t = nc()

    @Cons
    val op_con = c("$op dist {$no_op := 1, $add_op := 5, $and_op:=5, $xor_op:=5, $mul_op:=5, $rst_op:=1}")

    @Cons
    val data = c(
        "$A dist {${u(0x00)}:=1, [${u(0x01)} : ${u(0xfe)}]:=1, ${u(0xff)}:=1}",
        "$B dist {${u(0x00)}:=1, [${u(0x01)} : ${u(0xfe)}]:=1, ${u(0xff)}:=1}"
    )

    var result: Ubit<`16`> = nc()

    override fun do_compare(rhs: uvm_object?, comparer: uvm_comparer?): Boolean {
        val tested: sequence_item
        val same: Boolean
        if (rhs == null) {
            inj("`uvm_fatal(${get_type_name()}, ${"Tried to do comparison to a null pointer"})")
        }
        if (rhs !is sequence_item) {
            same = false
        } else {
            tested = rhs
            same = super.do_compare(rhs, comparer) &&
                (tested.A == A) &&
                (tested.B == B) &&
                (tested.op == op)
        }
        return same
    }

    override fun do_copy(rhs: uvm_object?) {
        val RHS: sequence_item
        if (rhs == null) {
            fatal("Tried to copy null transaction")
        }
        super.do_copy(rhs)
        RHS = rhs as sequence_item
        A = RHS.A
        B = RHS.B
        op = RHS.op
        result = RHS.result
    }

    override fun convert2string(): String {
        val s: String
        s = "A:$A B:$B op:$op = $result"
        return s
    }
}