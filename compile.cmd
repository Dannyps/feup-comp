SET JJT_FILE=teste.jjt
SET JJ_FILE=teste.jj
SET SYNTAX_ANALYZER_FILE=Fac

cd ast

jjtree %JJT_FILE% && javacc %JJ_FILE% && cd .. && javac -g -d bin/ ast/*.java && javac -g -d bin/ symboltable/*.java && java -cp bin/ ast.%SYNTAX_ANALYZER_FILE% %1