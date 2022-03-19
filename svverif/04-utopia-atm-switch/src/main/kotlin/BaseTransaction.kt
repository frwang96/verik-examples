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

import io.verik.core.*

abstract class BaseTransaction : Class() {

    val id: Int = count++

    abstract fun compare(other: BaseTransaction): Boolean
    abstract fun copy(): BaseTransaction
    abstract fun display()

    companion object {

        var count = 0
    }
}

class UniTransaction : BaseTransaction() {

    @Rand var gfc: Ubit<`4`> = u0()
    @Rand var vpi: Ubit<`8`> = u0()
    @Rand var vci: Ubit<`16`> = u0()
    @Rand var clp: Boolean = false
    @Rand var pt: Ubit<`3`> = u0()
    var hec: Ubit<`8`> = u0()
    @Rand var payload: Packed<`48`, Ubit<`8`>> = fill0()

    init {
        if (!is_syndrome_generated) generateSyndrome()
    }

    override fun postRandomize() {
        var hdr = cat(gfc, vpi, vci, clp, pt)
        hec = u0()
        repeat(4) {
            hec = syndrome[hec xor hdr[31, 24]]
            hdr = hdr shl 8
        }
        hec = hec xor u(0x55)
    }

    override fun compare(other: BaseTransaction): Boolean {
        return if (other is UniTransaction) {
            this.gfc == other.gfc &&
                this.vpi == other.vpi &&
                this.vci == other.vci &&
                this.clp == other.clp &&
                this.pt == other.pt &&
                this.hec == other.hec &&
                this.payload == other.payload
        } else false
    }

    // TODO
    override fun copy(): BaseTransaction {
        return this
    }

    override fun display() {
        println("UNI id=$id gfc=$gfc vpi=$vpi vci=$vci clp=$clp pt=$pt hec=$hec")
    }

    fun pack(): AtmCell {
        val uni = UniCell(gfc, vpi, vci, clp, pt, hec, payload)
        return AtmCell(uni = uni)
    }

    fun unpack(atm: AtmCell) {
        gfc = atm.uni.gfc
        vpi = atm.uni.vpi
        vci = atm.uni.vci
        clp = atm.uni.clp
        pt = atm.uni.pt
        hec = atm.uni.hec
        payload = atm.uni.payload
    }

    fun generateSyndrome() {
        for (i in 0 until 256) {
            var syndrome_byte: Ubit<`8`> = i.toUbit()
            repeat(8) {
                syndrome_byte = if (syndrome_byte[7]) {
                    (syndrome_byte shl 1) xor u(0x07)
                } else {
                    syndrome_byte shl 1
                }
            }
            syndrome[i] = syndrome_byte
        }
        is_syndrome_generated = true
    }

    companion object {

        var syndrome: Unpacked<`256`, Ubit<`8`>> = fill0()
        var is_syndrome_generated: Boolean = false
    }
}
