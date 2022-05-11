/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

package camera

import io.verik.core.*

enum class CameraReadState {
    WAIT_FRAME_START,
    ROW_CAPTURE
}
