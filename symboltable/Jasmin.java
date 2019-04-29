package symboltable;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import ast.*;
import symboltable.*;

/**
 * Jasmin
 * 
 * dumping of menmonics for jasmin
 */
public class Jasmin {

    private FileWriter f = null;
    private ClassDeclaration c = null;
    private int identLevel = 0;

    private Path outputDir = Paths.get("output");

    public Jasmin(Main m) {
        this.c = m.getClassDeclaration();
        String className = this.c.getName();
        this.openFile(className);

        this.toFile(".source " + className + ".jmm");
        this.toFile(".class public " + className);
        this.toFile(".super java/lang/Object"); // TODO check if class extends
        this.toFile("");

        this.makeSummary();

        this.c.getAllMethods().forEach((k, v) -> {
            writeMethod(k, v);
        });

        // variables, methods, and stuff
        this.closeFile();
        return;
    }

    private void writeMethod(String k, MethodDeclaration v) {
        HashMap<String, Integer> vars = new HashMap<String, Integer>();
        String methodLine = ".method public"; // all methods are public in Java--

        if (k == "main()") { // only the main method is static in Java--
            methodLine += " static main([Ljava/lang/String;)V";
            ASTMainDeclaration m = (ASTMainDeclaration) v.getNode();
            vars.put(m.getParam(), vars.size());
        } else {
            // regular method

            methodLine += " " + k.substring(0, k.length() - 2); // the method name without parentheses
            methodLine += "()"; // TODO args

            try {
                methodLine += getSignature(v.getReturnType());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.toFile(methodLine);

        this.toFile(".limit stack " + v.getAllVariables().size());
        this.toFile(".limit locals " + v.getAllVariables().size() + v.getAllParameters().size());
        this.toFile("");

        Node methodBody = v.getNode().jjtGetChild(0);

        int n = methodBody.jjtGetNumChildren();

        v.getAllParameters().forEach((param) -> {
            vars.put(param.getName(), vars.size());
        });
        for (int i = 0; i < n; i++) {
            Node node = methodBody.jjtGetChild(i);
            processNode(vars, node);
        }

        this.toFile("");
        this.toFile("return");
        this.toFile(".end method");
        this.toFile("");
    }

    private void processNode(HashMap<String, Integer> vars, Node node) {
        if (node instanceof ASTVarDeclaration) {
            ASTVarDeclaration nn = (ASTVarDeclaration) node;
            vars.put(nn.getIdentifier(), vars.size());
        } else if (node instanceof ASTTerm) {
            String str = ((ASTTerm) node).getStr();
                try {
                    Integer val = Integer.parseInt(str);
                    toFile("ldc " + val);
                } catch (Exception e) {
                    // parsing as int failed. this must be a variable
                    if (vars.containsKey(str)) {
                        // it is a variable and was found
                        toFile("iload " + vars.get(str));
                    } else {
                        // this should not happen
                        System.err.println("An ASTTerm could not be parsed as a variable nor as an integer: " + str);
                        System.exit(-2);
                    }
                }
        } else if (node instanceof ASTEqual) {
            ASTEqual nn = (ASTEqual) node;
            Node dest = nn.jjtGetChild(0);
            Node value = nn.jjtGetChild(1);
            if (value instanceof ASTTerm) {
                processNode(vars, value);
            } else if (value instanceof ASTAdd) {
                processNode(vars, value);
            }

            toFile("istore " + vars.get(((ASTTerm) dest).getStr()));
        } else if(node instanceof ASTAdd){
            ASTAdd nn = (ASTAdd) node;
            processNode(vars, nn.jjtGetChild(0));  // 1st param
            processNode(vars, nn.jjtGetChild(1));  // 2nd param
            toFile("iadd");

        }
    }

    private static String getSignature(String returnType) throws Exception {
        String ret;
        switch (returnType) {
        case "boolean":
            ret = "Z";
            break;
        case "byte":
            ret = "B";
            break;
        case "char":
            ret = "C";
            break;
        case "short":
            ret = "S";
            break;
        case "int":
            ret = "I";
            break;
        case "long":
            ret = "J";
            break;
        case "flat":
            ret = "F";
            break;
        case "double":
            ret = "D";
            break;
        case "void":
            ret = "V";
            break;
        default:
            if (returnType.substring(returnType.length() - 2).equals("[]")) {
                // this is an array type
                return "[" + getSignature(returnType.substring(0, returnType.length() - 2));
            } else {
                // TODO might be a Fully qualiffied class name
                return "L";
            }
            // throw new Exception("The passed returnType is not valid! Got " + returnType);
        }

        return ret;
    }

    private void makeSummary() {
        System.out.println("Class has " + this.c.getAllVariables().size() + " variables.");
        System.out.println("Class has " + this.c.getAllMethods().size() + " methods.");

        this.c.getAllMethods().forEach((k, v) -> {
            System.out.println("    Method " + k + " has " + v.getAllParameters().size() + " params:");
            v.getAllParameters().forEach((param) -> {
                System.out.println("        - " + param);
            });
            System.out.println("    Method " + k + " has " + v.getAllVariables().size() + " variables:");
            v.getAllVariables().forEach((k1, v1) -> {
                System.out.println("        - " + v1);
            });
        });
    }

    private boolean openFile(String cname) {
        try {
            if (!Files.exists(this.outputDir)) {
                Files.createDirectories(this.outputDir);
            }
            this.f = new FileWriter("output" + File.separator + cname + ".j");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void closeFile() {
        try {
            this.f.close();
        } catch (Exception e) {
        }
        return;
    }

    private void toFile(String s) {
        if (this.f != null) {
            try {
                if (s.startsWith(".end method")) {
                    this.identLevel--;
                }
                f.write("\t".repeat(this.identLevel) + s + "\n");
                if (s.startsWith(".method")) {
                    this.identLevel++;
                }
            } catch (Exception e) {
            }
        } else {
            System.err.println("File was unexpectedely closed! Couldn't write: " + s);
            System.exit(-4);
        }
    }
}