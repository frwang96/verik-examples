/*
 * SPDX-License-Identifier: Apache-2.0
 */

module FftFifo(
    s_axis_aresetn,
    s_axis_aclk,
    s_axis_tvalid,
    s_axis_tready,
    s_axis_tdata,
    s_axis_tlast,
    m_axis_tvalid,
    m_axis_tready,
    m_axis_tdata,
    m_axis_tlast
);
    input         s_axis_aresetn;
    input         s_axis_aclk;
    input         s_axis_tvalid;
    output        s_axis_tready;
    input  [31:0] s_axis_tdata;
    input         s_axis_tlast;
    output        m_axis_tvalid;
    input         m_axis_tready;
    output [31:0] m_axis_tdata;
    output        m_axis_tlast;
endmodule
