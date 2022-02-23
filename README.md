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
These examples import from the [`UVM`](https://github.com/accellera/uvm) and closely follow the original SystemVerilog
coding conventions.
To compile these examples define the environment variable `UVM_HOME` and point it to `uvm/distrib/src`.

- `02-conventional-test`: Conventional testbench for an ALU.
- `03-interfaces`: Interfaces and bus functional models for an ALU.
- `05-classes`: Classes and inheritance.
- `06-polymorphism`: Polymorphic types.
- `07-static-methods`: Static methods.
- `08-type-parameters`: Type parameterized classes.
- `09-factory-pattern`: Factory pattern for object instantiation.
- `10-oop-test`: Basic OOP testbench.
- `11-uvm-test`: Basic UVM testbench.
- `12-uvm-components`: UVM components.
- `13-uvm-environments`: UVM environments.
- `15-analysis-ports`: UVM analysis ports.
- `16-analysis-ports-test`: UVM analysis ports testbench.
- `17-communication`: UVM put and get ports for interthread communication.
- `18-communication-test`: UVM testbench with put and get ports.
- `19-uvm-reporting`: UVM reporting macros.
- `20-deep-operations`: Deep copy and to string operations.
- `21-uvm-transactions`: UVM transactions.
- `22-uvm-agents`: UVM agents.
- `23-uvm-sequences`: UVM sequences.
 
## [vkprimer](https://github.com/frwang96/verik-examples/tree/main/vkprimer)

Examples from `uvmprimer` rewritten to follow the coding conventions of idiomatic Verik.
These examples do not import from the UVM and implement the equivalent functionality directly in Verik.

## [sanity](https://github.com/frwang96/verik-examples/tree/main/sanity)

Miscellaneous examples that demonstrate various aspects of the language.

- `01-count`: Counter module.
- `02-adder`: Adder module.
- `03-multiplier`: Multiplier module.
- `04-comb`: Various combinational logic.
- `05-alu`: Arithmetic logic unit.
- `06-cache`: Direct mapped cache.
- `07-import-div` : Import and simulate the PicoRV32 PCPI divide module.
