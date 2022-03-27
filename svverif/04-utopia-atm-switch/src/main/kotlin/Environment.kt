/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

class Environment(
    val rx: ArrayList<UtopiaInterface.TestRxPort>,
    val tx: ArrayList<UtopiaInterface.TestTxPort>,
) : Class()
