# Verik Examples

Examples for [Verik](https://github.com/frwang96/verik).
Refer to [template](https://github.com/frwang96/verik-template) for a template project.
Compiling the examples requires a local build of the Verik toolchain.

<!--- examples --->

## [riscv](https://github.com/frwang96/verik-examples/tree/main/riscv)

RISC-V core based on the [PicoRV32](https://github.com/YosysHQ/picorv32) project.
PicoRV32 is a CPU core that implements the RV32IMC instruction set.
It is configurable with an optional interrupt controller, single or two-cycle ALU, and single or dual-port register
file.
This example demonstrates substantial parameterization, assertions, and test bench code.

## [uvmprimer](https://github.com/frwang96/verik-examples/tree/main/uvmprimer)

Examples adapted from [The UVM Primer](https://sites.google.com/view/uvmprimer-com/home) that demonstrate OOP testbench
functionality.
These examples import from the [UVM](https://github.com/accellera/uvm) and closely follow the original SystemVerilog
coding conventions rather than the coding conventions of idiomatic Verik.
To compile these examples define the environment variable `UVM_HOME` and point it to `uvm/distrib/src`.

- `01-conventional-test`: Conventional testbench for an ALU.
- `02-interfaces`: Interfaces and bus functional models for an ALU.
- `03-classes`: Classes and inheritance.
- `04-polymorphism`: Polymorphic types.
- `05-static-methods`: Static methods.
- `06-type-parameters`: Type parameterized classes.
- `07-factory-pattern`: Factory pattern for object instantiation.
- `08-oop-test`: Basic OOP testbench.
- `09-uvm-test`: Basic UVM testbench.
- `10-uvm-components`: UVM components.
- `11-uvm-environments`: UVM environments.
- `12-analysis-ports`: UVM analysis ports.
- `13-analysis-ports-test`: UVM analysis ports testbench.
- `14-communication`: UVM put and get ports for interthread communication.
- `15-communication-test`: UVM testbench with put and get ports.
- `16-uvm-reporting`: UVM reporting macros.
- `17-deep-operations`: Deep copy and to string operations.
- `18-uvm-transactions`: UVM transactions.
- `19-uvm-agents`: UVM agents.
- `20-uvm-sequences`: UVM sequences.
 
## [vkprimer](https://github.com/frwang96/verik-examples/tree/main/vkprimer)

Examples from `uvmprimer` rewritten to follow the coding conventions of idiomatic Verik.
These examples do not import from the UVM and implement the equivalent functionality directly in Verik.

## [svverif](https://github.com/frwang96/verik-examples/tree/main/svverif)

Examples adapted from [SystemVerilog for Verification](http://www.chris.spear.net/systemverilog/default.htm) that
demonstrate more advanced verification features.
These examples are rewritten to follow the coding conventions of idiomatic Verik.

- `01-arbiter`: Arbiter testbench with module ports and clocking blocks.

## [misc](https://github.com/frwang96/verik-examples/tree/main/misc)

Miscellaneous examples that demonstrate various aspects of the language.

- `01-count`: Simple counter module.
- `02-adder`: Simple adder module.
- `03-multiplier`: Simple multiplier module.
- `04-comb`: Various combinational logic with tests.
- `05-alu`: Arithmetic logic unit with tests.
- `06-cache`: Direct mapped cache.
