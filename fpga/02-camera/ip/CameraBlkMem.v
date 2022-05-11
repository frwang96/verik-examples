/*
 * SPDX-License-Identifier: Apache-2.0
 */

module CameraBlkMem(clka, wea, addra, dina, clkb, addrb, doutb);
    input         clka;
    input  [0:0]  wea;
    input  [16:0] addra;
    input  [11:0] dina;
    input         clkb;
    input  [16:0] addrb;
    output [11:0] doutb;
endmodule
