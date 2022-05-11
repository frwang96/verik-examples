/*
 * SPDX-License-Identifier: Apache-2.0
 */

module FftBlkMem(clka, ena, wea, addra, dina, douta, clkb, enb, web, addrb, dinb, doutb);
    input         clka;
    input         ena;
    input  [0:0]  wea;
    input  [9:0]  addra;
    input  [31:0] dina;
    output [31:0] douta;
    input         clkb;
    input         enb;
    input  [0:0]  web;
    input  [9:0]  addrb;
    input  [31:0] dinb;
    output [31:0] doutb;
endmodule
