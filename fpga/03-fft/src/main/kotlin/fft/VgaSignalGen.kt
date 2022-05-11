/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

package fft

import io.verik.core.*

class VgaSignalGen(
    @In var vclock_in: Boolean,
    @Out var hcount_out: Ubit<`11`>,
    @Out var vcount_out: Ubit<`10`>,
    @Out var hsync_out: Boolean,
    @Out var vsync_out: Boolean,
    @Out var blank_out: Boolean
) : Module() {

    val DISPLAY_WIDTH = u("11'h400")
    val DISPLAY_HEIGHT = u("10'h300")

    val H_FP = u(24)
    val H_SYNC_PULSE = u(136)
    val H_BP = u(160)

    val V_FP = u(3)
    val V_SYNC_PULSE = u(6)
    val V_BP = u(29)

    var hblank: Boolean = nc()
    var vblank: Boolean = nc()

    @Com var hblankon = (hcount_out == DISPLAY_WIDTH - u(1))
    @Com var hsyncon = (hcount_out == DISPLAY_WIDTH + H_FP - u(1))
    @Com var hsyncoff = (hcount_out == DISPLAY_WIDTH + H_FP + H_SYNC_PULSE - u(1))
    @Com var hreset = (hcount_out == DISPLAY_WIDTH + H_FP + H_SYNC_PULSE + H_BP - u(1))

    @Com var vblankon = hreset && (vcount_out == DISPLAY_HEIGHT - u(1))
    @Com var vsyncon = hreset && (vcount_out == DISPLAY_HEIGHT + V_FP - u(1))
    @Com var vsyncoff = hreset && (vcount_out == DISPLAY_HEIGHT + V_FP + V_SYNC_PULSE - u(1))
    @Com var vreset = hreset && (vcount_out == DISPLAY_HEIGHT + V_FP + V_SYNC_PULSE + V_BP - u(1))

    @Com
    var next_hblank = when {
        hreset -> false
        hblankon -> true
        else -> hblank
    }

    @Com
    var next_vblank = when {
        vreset -> false
        vblankon -> true
        else -> vblank
    }

    @Seq
    fun seqOutput() {
        on (posedge(vclock_in)) {
            hcount_out = if (hreset) u0() else hcount_out + u(1)
            hblank = next_hblank
            hsync_out = when {
                hsyncon -> false
                hsyncoff -> true
                else -> hsync_out
            }

            vcount_out = if (hreset) {
                if (vreset) u0() else vcount_out + u(1)
            } else vcount_out
            vblank = next_vblank
            vsync_out = when {
                vsyncon -> false
                vsyncoff -> true
                else -> vsync_out
            }
            blank_out = next_vblank || (next_hblank && !hreset)
        }
    }
}
