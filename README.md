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

## Semantic Analysis

## Intermediate Representation (IRs)

## Code Generation

## Overview

### Task Distribution

### Pros

### Cons