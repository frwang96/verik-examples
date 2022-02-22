# Verik Examples

Example projects for [Verik](https://github.com/frwang96/verik). Refer to
[template](https://github.com/frwang96/verik-template) for a template project.
Compiling the example projects requires a local build of the Verik toolchain.

<!--- examples --->

## [uvmprimer](https://github.com/frwang96/verik-examples/tree/main/uvmprimer)

Projects adapted from [The UVM Primer](https://sites.google.com/view/uvmprimer-com/home) that demonstrate OOP testbench
functionality.
To import the [`UVM`](https://github.com/accellera/uvm) define the environment variable `UVM_HOME` and point it to
`uvm/distrib/src`.

- `02-tests`: Conventional testbench for an ALU.
- `03-interfaces`: Interfaces and bus functional models for an ALU.
- `05-classes`: Classes and inheritance.
- `06-polymorphism`: Polymorphic types.
- `07-objects`: Statically declared objects.
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

## [riscv](https://github.com/frwang96/verik-examples/tree/main/riscv)

RISC-V core based on the [PicoRV32](https://github.com/YosysHQ/picorv32) project.
This example project demonstrates substantial parameterization, assertions, and test bench code.

## [sanity](https://github.com/frwang96/verik-examples/tree/main/sanity)

Miscellaneous projects that demonstrate various aspects of the language.

- `01-count`: Counter module.
- `02-adder`: Adder module.
- `03-multiplier`: Multiplier module.
- `04-comb`: Various combinational logic.
- `05-alu`: Arithmetic logic unit.
- `06-cache`: Direct mapped cache.
- `07-import-div` : Import and simulate the PicoRV32 PCPI divide module.
