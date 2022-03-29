# Verik Examples

Examples for [Verik](https://github.com/frwang96/verik).
Refer to [template](https://github.com/frwang96/verik-template) for a template project.
Compiling the examples requires a local build of the Verik toolchain.
Anyone is welcome to contribute by raising [issues](https://github.com/frwang96/verik-examples/issues),
posting in [discussions](https://github.com/frwang96/verik-examples/discussions), or creating
[pull requests](https://github.com/frwang96/verik-examples/pulls).
These examples serve as a [regression test](https://verik.io/docs/regression/) for the Verik toolchain.
If you have a Verik project that you are willing to share, I would love to add it to this collection.

<!--- examples --->

## [riscv](https://github.com/frwang96/verik-examples/tree/main/riscv)

RISC-V core adapted from the [PicoRV32](https://github.com/YosysHQ/picorv32) project.
PicoRV32 is a CPU core that implements the RV32IMC instruction set.
It is configurable with an optional interrupt controller, single or two-cycle ALU, and single or dual-port register
file.
<br> Tags: `#Modules` `#Parameterization` `#Assertions`

## [uvmprimer](https://github.com/frwang96/verik-examples/tree/main/uvmprimer)

Examples adapted from [The UVM Primer](https://sites.google.com/view/uvmprimer-com/home) that demonstrate OOP testbench
functionality.
These examples import from the [UVM](https://github.com/accellera/uvm) and closely follow the original SystemVerilog
coding conventions rather than the coding conventions of idiomatic Verik.
Warnings from code style violations are
[suppressed](https://www.jetbrains.com/help/idea/disabling-and-enabling-inspections.html) with
`@file:Suppress` annotations at the top of the source files.
To compile these examples define the environment variable `UVM_HOME` and point it to `uvm/distrib/src`.

### 01-conventional-test
Conventional testbench for an ALU.
<br> Tags: `#Modules` `#Coverage`

### 02-interfaces
Interfaces and bus functional models for an ALU.
<br> Tags: `#Modules` `#ModuleInterfaces` `#Coverage`

### 03-classes
Classes and inheritance for a rectangle class.
<br> Tags: `#Classes`

### 04-polymorphism
Polymorphic types for an animal class.
<br> Tags: `#Classes`

### 05-static-methods
Static methods with objects.
<br> Tags: `#Classes`

### 06-type-parameters
Type parameterized classes.
<br> Tags: `#Classes` `#Parameterization`

### 07-factory-pattern
Factory pattern for object instantiation.
<br> Tags: `#Classes` `#Parameterization`

### 08-oop-test
Basic OOP testbench for an ALU.
<br> Tags: `#Modules` `#ModuleInterfaces` `#Classes` `#Coverage`

### 09-uvm-test
Basic UVM testbench for an ALU.
<br> Tags: `#Importer` `#Modules` `#ModuleInterfaces` `#UVM` `#Coverage`

### 10-uvm-components
UVM testbench for an ALU with UVM components.
<br> Tags: `#Importer` `#Modules` `#ModuleInterfaces` `#UVM` `#Coverage`

### 11-uvm-environments
UVM testbench for an ALU with UVM environments.
<br> Tags: `#Importer` `#Modules` `#ModuleInterfaces` `#UVM` `#Coverage`

### 12-analysis-ports
Dice roller example with UVM analysis ports.
<br> Tags: `#Importer` `#UVM` `#Coverage` `#Randomization`

### 13-analysis-ports-test
UVM testbench for an ALU with UVM analysis ports.
<br> Tags: `#Importer` `#Modules` `#ModuleInterfaces` `#UVM` `#Coverage`

### 14-communication
Interthread communication with UVM put and get ports.
<br> Tags: `#Importer` `#UVM`

### 15-communication-test
UVM testbench for an ALU with UVM put and get ports.
<br> Tags: `#Importer` `#Modules` `#ModuleInterfaces` `#UVM` `#Coverage`

### 16-uvm-reporting
UVM testbench for an ALU with UVM reporting macros.
<br> Tags: `#Importer` `#Modules` `#ModuleInterfaces` `#UVM` `#Coverage`

### 17-deep-operations
Deep operations for copying and converting objects to strings.
<br> Tags: `#Classes`

### 18-uvm-transactions
UVM testbench for an ALU with UVM transactions.
<br> Tags: `#Importer` `#Modules` `#ModuleInterfaces` `#UVM` `#Coverage` `#Randomization`

### 19-uvm-agents
UVM testbench for an ALU with UVM agents.
<br> Tags: `#Importer` `#Modules` `#ModuleInterfaces` `#UVM` `#Coverage` `#Randomization`

### 20-uvm-sequences
UVM testbench for an ALU with UVM sequences.
<br> Tags: `#Importer` `#Modules` `#ModuleInterfaces` `#UVM` `#Coverage` `#Randomization`
 
## [vkprimer](https://github.com/frwang96/verik-examples/tree/main/vkprimer)

Examples from `uvmprimer` rewritten to follow the coding conventions of idiomatic Verik.
These examples do not import from the UVM and implement the equivalent functionality directly in Verik.

### 01-conventional-test
Conventional testbench for an ALU.
<br> Tags: `#Modules` `#Coverage`

### 02-interfaces
Interfaces and bus functional models for an ALU.
<br> Tags: `#Modules` `#ModuleInterfaces` `#Coverage`

### 03-classes
Classes and inheritance for a rectangle class.
<br> Tags: `#Classes`

### 04-polymorphism
Polymorphic types for an animal class.
<br> Tags: `#Classes`

### 05-static-methods
Static methods with objects.
<br> Tags: `#Classes`

### 06-type-parameters
Type parameterized classes.
<br> Tags: `#Classes` `#Parameterization`

### 07-factory-pattern
Factory pattern for object instantiation.
<br> Tags: `#Classes` `#Parameterization`

### 08-oop-test
Basic OOP testbench for an ALU.
<br> Tags: `#Modules` `#ModuleInterfaces` `#Classes` `#Coverage`

### 17-deep-operations
Deep operations for copying and converting objects to strings.
<br> Tags: `#Classes`

## [svverif](https://github.com/frwang96/verik-examples/tree/main/svverif)

Examples adapted from [SystemVerilog for Verification](http://www.chris.spear.net/systemverilog/default.htm) that
demonstrate more verification features.
These examples follow the coding conventions of idiomatic Verik.

### 01-arbiter
Arbiter testbench with a module interface that demonstrates module ports and clocking blocks.
<br> Tags: `#Modules` `#ModuleInterfaces`

### 02-unique-array
Generate an random array with unique elements.
<br> Tags: `#Classes` `#Randomization`

### 03-atm-switch
ATM switch with test bench. The number of RX and TX streams is parameterized.
<br> Tags: `#Modules` `#ModuleInterfaces` `#Classes` `#Parameterization` `#Randomization`

### 04-utopia-atm-switch
UTOPIA bus ATM switch with testbench. This example is currently work in progress.
<br> Tags: `#Modules` `#ModuleInterfaces` `#Classes` `#Parameterization` `#Coverage` `#Randomization`

## [misc](https://github.com/frwang96/verik-examples/tree/main/misc)

Miscellaneous examples that demonstrate various aspects of the language.

### 01-count
Simple counter module.
<br> Tags: `#Module`

### 02-adder
Simple parameterized ripple-carry adder module.
<br> Tags: `#Module` `#Parameterization`

### 03-multiplier
Simple combinational and sequential multipliers.
<br> Tags: `#Module`

### 04-comb
Various combinational logic expressed with simple logic gates.
<br> Tags: `#Module`

### 05-alu
Simple RV32I ALU with tests.
<br> Tags: `#Module`

### 06-cache
Direct mapped write-back cache.
<br> Tags: `#Module` `#ModuleInterface`
