/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

typealias STREAMS = `4`
val STREAMS_VAL = i<STREAMS>()

val ATM_HEADER_SIZE = 5
val ATM_PAYLOAD_SIZE = 48
val ATM_CELL_SIZE = ATM_HEADER_SIZE + ATM_PAYLOAD_SIZE
