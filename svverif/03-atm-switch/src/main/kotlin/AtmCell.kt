/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

class AtmCell : Class() {

    @Rand var gfc: Ubit<`4`> = u0()
    @Rand var vpi: Ubit<`8`> = u0()
    @Rand var vci: Ubit<`16`> = u0()
    @Rand var pt: Ubit<`3`> = u0()
    @Rand var clp: Boolean = false
    @Rand var hec: Ubit<`8`> = u0()

    var w_good_hec = 100
    var payload: ArrayList<Ubit<`8`>> = ArrayList()

    var eot_cell = false

    @Rand var delay: Int = 0

    @Cons
    val c_hec = c(
        "$hec dist {1:=$w_good_hec, 0:=(100-$w_good_hec)}",
        "$w_good_hec inside {[0:100]}",
        "$delay inside {[1:50]}"
    )

    init {
        for (i in 0 until ATM_PAYLOAD_SIZE) payload.add(u0())
    }

    override fun postRandomize() {
        for (i in 0 until ATM_PAYLOAD_SIZE) {
            payload[i] = randomUbit()
        }
    }

    fun bytePack(): ArrayList<Ubit<`8`>> {
        val bytes = ArrayList<Ubit<`8`>>()
        for (i in 0 until ATM_CELL_SIZE) bytes.add(u0())

        bytes[0] = cat(gfc, vpi[7, 4])
        bytes[1] = cat(vpi[3, 0], vci[15, 12])
        bytes[2] = vci[11, 4]
        bytes[3] = cat(vci[3, 0], clp, pt)
        bytes[4] = hec
        for (i in 0 until ATM_PAYLOAD_SIZE) {
            bytes[i + ATM_HEADER_SIZE] = payload[i]
        }
        return bytes
    }

    fun byteUnpack(bytes: ArrayList<Ubit<`8`>>) {
        if (bytes.size != ATM_CELL_SIZE) {
            error("byteUnpack: bytes.size=${bytes.size} ATM_CELL_SIZE=$ATM_CELL_SIZE")
            return
        }
        gfc = bytes[0][7, 4]
        vpi[7, 4] = bytes[0][3, 0]
        vpi[3, 0] = bytes[1][7, 4]
        vci[15, 12] = bytes[1][3, 0]
        vci[11, 4] = bytes[2]
        vci[3, 0] = bytes[3][7, 4]
        clp = bytes[3][3]
        pt = bytes[3][2, 0]
        hec = bytes[4]
        for (i in 0 until ATM_PAYLOAD_SIZE) {
            payload[i] = bytes[i + ATM_HEADER_SIZE]
        }
    }

    fun compare(ac: AtmCell): Boolean {
        return when {
            ac.gfc != this.gfc -> {
                println("gfc mismatch ac=${ac.gfc} this=${this.gfc}")
                false
            }
            ac.vpi != this.vpi -> {
                println("vpi mismatch ac=${ac.vpi} this=${this.vpi}")
                false
            }
            ac.vci != this.vci -> {
                println("vci mismatch ac=${ac.vci} this=${this.vci}")
                false
            }
            ac.pt != this.pt -> {
                println("pt mismatch ac=${ac.pt} this=${this.pt}")
                false
            }
            ac.clp != this.clp -> {
                println("clp mismatch ac=${ac.clp} this=${this.clp}")
                false
            }
            ac.hec != this.hec -> {
                println("hec mismatch ac=${ac.hec} this=${this.hec}")
                false
            }
            else -> {
                for (i in 0 until ATM_PAYLOAD_SIZE) {
                    if (ac.payload[i] != this.payload[i]) {
                        println("payload[$i] mismatch ac=${ac.payload[i]} this=${this.payload[i]}")
                        return false
                    }
                }
                return true
            }
        }
    }
}
