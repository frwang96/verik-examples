/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

package camera

import io.verik.core.*

class CameraRead(
    @In var p_clk_in: Boolean,
    @In var vsync_in: Boolean,
    @In var href_in: Boolean,
    @In var p_data_in: Ubit<`8`>,
    @Out var pixel_data_out: Ubit<`16`>,
    @Out var pixel_valid_out: Boolean,
    @Out var frame_done_out: Boolean
) : Module() {

    var state: CameraReadState = nc()
    var pixel_half: Boolean = nc()

    @Seq
    fun seqOutput() {
        on(posedge(p_clk_in)) {
            when (state) {
                CameraReadState.WAIT_FRAME_START -> {
                    state = if (!vsync_in) CameraReadState.ROW_CAPTURE else CameraReadState.WAIT_FRAME_START
                    frame_done_out = false
                    pixel_half = false
                }
                CameraReadState.ROW_CAPTURE -> {
                    state = if (vsync_in) CameraReadState.WAIT_FRAME_START else CameraReadState.ROW_CAPTURE
                    frame_done_out = vsync_in
                    pixel_valid_out = href_in && pixel_half
                    if (href_in) {
                        if (pixel_half) pixel_data_out[7, 0] = p_data_in
                        else pixel_data_out[15, 8] = p_data_in
                        pixel_half = !pixel_half
                    }
                }
            }
        }
    }
}
