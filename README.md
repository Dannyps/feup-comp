# Compiler of the Java-- language to Java Bytecodes

## Group G55

| Name | Student Number | Grade (0-20) | Contribution (0-100%) |
| ---- | -------------- | ------------ | --------------------- |
| Daniel Silva | 201503212 | ? | 25% |
| Fábio Araújo | 201607944 | ? | 25% |
| Fábio Gaspar | 201503823 | ? | ? |
| João Agulha | 201607930 | ? | ? |

**Global project grade: ?**

## Summary

The purpose of this project is the development of a compiler written in Java, making use of _JJTree_ for code parsing and tree generation and _Jasmin_ for creating the bytecodes.
The project was split into several stages that essentially correspond to steps in the flow of a real compiler.
We have attained the basic functionality goal. Moreover, the feedback we received throughout each checkpoint has been enough to let us know that we developed the project the right way.

## Usage

### The following examples were successfully tested:

#### samples/valid/ 
    - MonteCarloPi.jmm
    - enunciado.jmm
    - FindMaximum.jmm
    - QuickSort.jmm
    - LazySort.jmm


#### Note:
 - The `life.jmm` is parsed successfully, and has its bytecodes generated, but does not run correctly.
 - No optimizations were implemented. As such, no options (e.g. -r or -o) are available.

### Parsing and jasmin file generation:

Use the script `compile.sh` and specify the Java-- file (**.jmm**) to be parsed.

```
sh compile.sh samples/text.jmm
```

### Jasmin testing:

After a .j file has been created by this compiler, Jasmin must be used to create the Java Bytecodes.

`java -jar jasmin.jar <filename.j>`

And finally: 
`java <classname>`


## Dealing with syntatic errors

The compiler has the ability to detect syntatic errors, i.e., unknown tokens which can't be found in Java-- grammar and sequences of tokens which are invalid, such as missing an expected `(` in a `if` statement. The errors are reported, and it makes an attempt to move forward by consuming tokens until it reaches a stable point, which is no longer affected by the detected error. Therefore, it attempts to report more than a single error.

## Semantic Analysis

Several semantic errors are detectable and reported. Below is a list of errors detected:

- Duplicated identifiers in the same scope such as classes names, method names, variable indentifiers at different scopes (repeated fields, local variables, collision with local variables and parameters, ...)
- Types mismatches such as method return type being accordingly to method declaration
- Variables are instantiated before used
- Expressions evaluate to the derired data type
- Others...

## Intermediate Representation (IRs)

There are two IRs in our project:

### High Level Intermediate Representation
This is composed by the Symbol Table. The ST can be considered a HLIR as it is accurate, i.e., it is capable of representing the source code without loss of information. The implemented SymbolTable is stored using a series of Classes, each representing a possilbe kind of node.


### Low Level Intermediate Representation
The LLIR is the jasmin file's contents. We can consider these a LLIR as they contain the instructions derived from the aforementioned HLIR. 


## Code Generation

We started by creating a class for encapsulating the code generation responsability, but ended up disregarding it as we realized that there was a lot of repeated code. This way, we creating a single class for both code generation and Symbol Table generation.
This may seem awkard, but it is in fact a way of speeding up the compilation process.
Code generation was a challenging and dificult marker in this project. Firstly, because we didn't have any past experience with Java Bytecodes, and secondly because we'd never generated code from a Symbol Table.

Despite these difficulties, we have implemented this functionality with great courage and confidence. The only thing sin the `j--` specification that we were unable to implement in terms of code generation was extending another class.

## Overview

### Task Distribution

 - initial parsing: equal contribution
 - error treatment: Daniel Silva
 - syntax tree: Fábio Gaspar, Fábio Araújo, João Agulha
 - Symbol tables: Fábio Araújo and João Agulha
 - Jasmin function and arithm: Daniel Silva, Fábio Gaspar
 - Jasmin for Conditions and Loops: equal contribution
 - Jasmin for arrays: Fábio Araújo
### Pros
 - documentation on code
 - easy to use (simple commands)

### Cons
 - no optimizations
 - condensed code