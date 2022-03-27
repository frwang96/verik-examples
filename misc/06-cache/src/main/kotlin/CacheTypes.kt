/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

typealias ADDR_WIDTH = `6`
typealias DATA_WIDTh = `8`
typealias TAG_WIDTH = `3`
typealias INDEX_WIDTH = `3`

typealias UbitAddr = Ubit<ADDR_WIDTH>
typealias UbitData = Ubit<DATA_WIDTh>
typealias UbitTag = Ubit<TAG_WIDTH>
typealias UbitIndex = Ubit<INDEX_WIDTH>

enum class Op {
    NOP,
    READ,
    WRITE
}

enum class State {
    READY,
    WRITEBACK,
    FILL
}

enum class Status {
    INVALID,
    CLEAN,
    DIRTY
}

class Line(
    var status: Status,
    var tag: UbitTag,
    var data: UbitData
) : Struct()
