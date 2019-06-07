# Compiler of the Java-- language to Java Bytecodes

## Group G55

| Name | Student Number | Grade (0-20) | Contribution (0-100%) |
| ---- | -------------- | ------------ | --------------------- |
| Daniel Silva | 201503212 | 16 | 25% |
| Fábio Araújo | 201607944 | 16 | 25% |
| Fábio Gaspar | 201503823 | 16 | 25% |
| João Agulha | 201607930 | 16 | 25% |

**Global project grade: 16**

## Summary

## Usage

### O programa foi testado com os ficheiros na pasta samples/valid 
    MonteCarloPi.jmm
    enunciado.jmm
    FindMaximum.jmm
    QuickSort.jmm
    LazySort.jmm

e todos funcionam.

O ficheiro life.jmm embora compile e gera os byteCodes nao funciona corretamente.

para correr o programa basta executar os comandos :

    sh compile.sh (file .jmm)
    java -jar jasmin.jar output/(file.jmm)
    java (file.jmm)


as otimizações nao foram realizadas, dai nao se ter que inserir nenhuma opção (-r ou -o)


Use the script `compile.sh` and specify the Java-- file (**.jmm**) to be parsed.

```
sh compile.sh samples/text.jmm
```

### Jasmin testing:

After a .j file has been created by this compiler, Jasmin must be used to create the Java Bytecodes.

`java -jar jasmin.jar <filename.j>`

And finally: 
`java <classname>`

In order to make the program print an int variable in a method, you can add the code bellow to the `<filename.j>` file before running the java program. It must be added right before the `return` statement. The first defined variable will be printed.

``` jasmin
; push java.lang.System.out (type PrintStream)
getstatic java/lang/System/out Ljava/io/PrintStream;
; load the first defined variable in this method
iload 1
; invoke println
invokevirtual java/io/PrintStream/println(I)V
```

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

## Code Generation

## Overview

### Task Distribution

### Pros

### Cons