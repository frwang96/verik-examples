/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file: Verik

package fft

import io.verik.core.*

class SquareAndSum<AXIS_TDATA_WIDTH : `*`>(
    @In var s00_axis_aclk: Boolean,
    @In var s00_axis_aresetn: Boolean,
    @Out var s00_axis_tready: Boolean,
    @In var s00_axis_tdata: Ubit<OF<AXIS_TDATA_WIDTH>>,
    @In var s00_axis_tlast: Boolean,
    @In var s00_axis_tvalid: Boolean,
    @In var m00_axis_aclk: Boolean,
    @In var m00_axis_aresetn: Boolean,
    @Out var m00_axis_tvalid: Boolean,
    @Out var m00_axis_tdata: Ubit<OF<AXIS_TDATA_WIDTH>>,
    @Out var m00_axis_tlast: Boolean,
    @In var m00_axis_tready: Boolean
) : Module() {

    val AXIS_TDATA_WIDTH = i<AXIS_TDATA_WIDTH>()

    var m00_axis_tvalid_reg_pre: Boolean = nc()
    var m00_axis_tlast_reg_pre: Boolean = nc()
    var m00_axis_tvalid_reg: Boolean = nc()
    var m00_axis_tlast_reg: Boolean = nc()
    var m00_axis_tdata_reg: Ubit<OF<AXIS_TDATA_WIDTH>> = nc()

    var s00_axis_tready_reg: Boolean = nc()
    var real_square: Sbit<OF<AXIS_TDATA_WIDTH>> = nc()
    var imag_square: Sbit<OF<AXIS_TDATA_WIDTH>> = nc()

    var real_in:Sbit<DIV<AXIS_TDATA_WIDTH, `2`>> = s00_axis_tdata[AXIS_TDATA_WIDTH - 1, AXIS_TDATA_WIDTH / 2].toSbit()
    var imag_in:Sbit<DIV<AXIS_TDATA_WIDTH, `2`>> = s00_axis_tdata[AXIS_TDATA_WIDTH / 2 - 1, 0].toSbit()

    @Com
    fun comOutput() {
        m00_axis_tvalid = m00_axis_tvalid_reg
        m00_axis_tlast = m00_axis_tlast_reg
        m00_axis_tdata = m00_axis_tdata_reg
        s00_axis_tready = s00_axis_tready_reg
    }

    @Seq
    fun seqS00() {
        on(posedge(s00_axis_aclk)) {
            s00_axis_tready_reg = if (!s00_axis_aresetn) false else m00_axis_tready
        }
    }

    @Seq
    fun seqM00() {
        on(posedge(m00_axis_aclk)) {
            if (!m00_axis_aresetn) {
                m00_axis_tvalid_reg = false
                m00_axis_tlast_reg = false
                m00_axis_tdata_reg = u0()
            } else {
                m00_axis_tvalid_reg_pre = s00_axis_tvalid
                m00_axis_tlast_reg_pre = s00_axis_tlast
                real_square = real_in mul real_in
                imag_square = imag_in mul imag_in

                m00_axis_tvalid_reg = m00_axis_tvalid_reg_pre
                m00_axis_tlast_reg = m00_axis_tlast_reg_pre
                m00_axis_tdata_reg = (real_square + imag_square).toUbit()
            }
        }
    }
}
