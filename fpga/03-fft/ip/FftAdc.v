/*
 * SPDX-License-Identifier: Apache-2.0
 */

module FftAdc(daddr_in, dclk_in, den_in, di_in, dwe_in, vauxp3, vauxn3, busy_out, channel_out, do_out, drdy_out, eoc_out, eos_out, alarm_out, vp_in, vn_in);
    input  [6:0]  daddr_in;
    input         dclk_in;
    input         den_in;
    input  [15:0] di_in;
    input         dwe_in;
    input         vauxp3;
    input         vauxn3;
    output        busy_out;
    output [4:0]  channel_out;
    output [15:0] do_out;
    output        drdy_out;
    output        eoc_out;
    output        eos_out;
    output        alarm_out;
    input         vp_in;
    input         vn_in;
endmodule
