/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ConvertSecondaryConstructorToPrimary", "ClassName")

import dut.operation_t
import dut.operation_t.add_op
import io.verik.core.*

class add_tester : random_tester {

    constructor(b: tinyalu_bfm) : super(b)

    override fun get_op(): operation_t {
        return add_op
    }
}
