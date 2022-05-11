/*
 * SPDX-License-Identifier: Apache-2.0
 */

module FftSqrt(
    aclk,
    s_axis_cartesian_tvalid,
    s_axis_cartesian_tready,
    s_axis_cartesian_tlast,
    s_axis_cartesian_tdata,
    m_axis_dout_tvalid,
    m_axis_dout_tlast,
    m_axis_dout_tdata
);
    input         aclk;
    input         s_axis_cartesian_tvalid;
    output        s_axis_cartesian_tready;
    input         s_axis_cartesian_tlast;
    input  [31:0] s_axis_cartesian_tdata;
    output        m_axis_dout_tvalid;
    output        m_axis_dout_tlast;
    output [23:0] m_axis_dout_tdata;
endmodule
