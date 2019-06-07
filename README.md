# Compiler of the Java-- language to Java Bytecodes

## Group G55

| Name | Student Number | Grade (0-20) | Contribution (0-100%) |
| ---- | -------------- | ------------ | --------------------- |
| Daniel Silva | 201503212 | ? | ? |
| Fábio Araújo | 201607944 | ? | 25% |
| Fábio Gaspar | 201503823 | ? | ? |
| João Agulha | 201607930 | ? | ? |

**Global project grade: ?**

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

## Semantic Analysis

## Intermediate Representation (IRs)

## Code Generation

## Overview

### Task Distribution

### Pros

### Cons