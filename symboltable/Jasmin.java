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

    private HashMap<String, HashMap<String, Integer>> vars;
    private String defaultMethod = null;

    private HashMap<String, Integer> getVars(String method) {
        this.defaultMethod = method;
        // System.out.println("defaultMethod set: "+ method);
        return vars.get(method);
    }

    private HashMap<String, Integer> getVars() throws Exception {
        if (this.defaultMethod != null) {
            return vars.get(this.defaultMethod);
        } else
            throw new Exception("the defaultMethod is not set.");
    }

    void addMethodToVars(String method) {
        vars.put(method, new HashMap<String, Integer>());
        this.defaultMethod = method;
        // System.out.println("added method " + method + " to vars");
    }

    public Jasmin(Main m) {
        this.vars = new HashMap<String, HashMap<String, Integer>>();
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

    /**
     * @brief Writes the revelant instructions to the file regarding this method.
     * @param methodName the name of the method
     * @param v          the methodDeclaration
     */
    private void writeMethod(String methodName, MethodDeclaration v) {
        this.addMethodToVars(methodName);
        String methodLine = ".method public"; // all methods are public in Java--

        if (methodName == "main()") { // only the main method is static in Java--
            methodLine += " static main([Ljava/lang/String;)V";
            ASTMainDeclaration m = (ASTMainDeclaration) v.getNode();
            this.getVars(methodName).put(m.getParam(), vars.size());
        } else {
            // regular method

            methodLine += " " + methodName.substring(0, methodName.length() - 2); // the method name without parentheses
            methodLine += "()"; // TODO args

            try {
                methodLine += getSignature(v.getReturnType());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.toFile(methodLine);

        this.toFile(".limit stack " + v.getAllParameters().size());
        int total = v.getAllVariables().size() + v.getAllParameters().size();
        this.toFile(".limit locals " + total);
        this.toFile("");

        Node methodBody = getMethodBody(v);

        System.out.println("Metod body ----> " + methodBody.toString());

        int n = methodBody.jjtGetNumChildren();

        System.out.println(n);
        System.out.println(v.getAllVariables().size());

        v.getAllVariables().values().forEach((param) -> {
            this.getVars(methodName).put(param.getName(), vars.size());
        });

        // for(String x : vars.get("getDouble()").keySet()) {
        // System.out.println(x);
        // }

        // for(Integer x : vars.get("getDouble()").values()) {
        // System.out.println(x);
        // }

        System.out.println("--------------------------");

        for (int i = 0; i < n; i++) {
            Node node = methodBody.jjtGetChild(i);
            // System.out.println(node);
            try {
                processNode(v, node);
                System.out.println(node);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.toFile("");
        System.out.println("------------------" + v.getType() + "-----------------------");
        if (!v.getType().equals("void")) {
            try {
                this.toFile(getSignature(v.getType()).toLowerCase() + "return");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            this.toFile("return");
        }
        this.toFile(".end method");
        this.toFile("");
    }

    private Node getMethodBody(MethodDeclaration v) {
        Node methodBody = null;
        int i = 0;
        while (!(methodBody instanceof ASTMethodBody)) {
            methodBody = v.getNode().jjtGetChild(i++);
        }
        // System.out.println("method body index: " + i);
        return methodBody;
    }

    /**
     * @brief Recursive function that takes care of a node. Will call itself for
     *        child nodes.
     * 
     * @param vars the variables for this method
     * @param node the node currently under analysis
     * @throws Exception
     */
    private void processNode(MethodDeclaration method, Node node) throws Exception {
        if (node instanceof ASTVarDeclaration) {
            ASTVarDeclaration nn = (ASTVarDeclaration) node;
            this.getVars().put(nn.getIdentifier(), vars.size()); // store variable in the vars hashmap
        } else if (node instanceof ASTTerm) {
            String str = ((ASTTerm) node).getStr();
            System.out.println(str);
            try {
                Integer val = Integer.parseInt(str);
                toFile("ldc " + val);
            } catch (Exception e) {
                // parsing as int failed. this must be a variable
                if (vars.containsKey(str)) {
                    // it is a variable and was found
                    toFile("iload " + vars.get(str));
                } else if(str == "null"){
                    // TODO we got a null
                    toFile("iload 0");
                }else{
                    // this should not happen
                    System.err.println("An ASTTerm could not be parsed as a variable nor as an integer: " + str);
                    System.exit(-2);
                }
            }
        } else if (node instanceof ASTEqual) {
            ASTEqual nn = (ASTEqual) node;
            Node dest = nn.jjtGetChild(0);
            Node value = nn.jjtGetChild(1);

            System.out.println(dest);
            System.out.println(value);


            if (value instanceof ASTTerm) {
                processNode(method, value);
            } else { // it must be processed
                //processNode(value);
            }

            // System.out.println(dest);
            // System.out.println(((ASTTerm) dest).getStr());
            // System.out.println("------------------------------------------------------------------");
            // System.out.println(vars.size());

            for(String var : vars.keySet()) {
                System.out.println(var);
            }

            toFile("istore " + vars.get(method.getName()).get(((ASTTerm) dest).getStr()));
        } else if (node instanceof ASTAdd) {
            ASTAdd nn = (ASTAdd) node;
            processNode(method, nn.jjtGetChild(0)); // 1st param
            processNode(method, nn.jjtGetChild(1)); // 2nd param
            toFile("iadd");
        } else if (node instanceof ASTMult) {
            ASTMult nn = (ASTMult) node;
            processNode(method, nn.jjtGetChild(0)); // 1st param
            processNode(method, nn.jjtGetChild(1)); // 2nd param
            toFile("imul");
        } else if (node instanceof ASTDiv) {
            ASTDiv nn = (ASTDiv) node;
            processNode(method, nn.jjtGetChild(0)); // 1st param
            processNode(method, nn.jjtGetChild(1)); // 2nd param
            toFile("idiv");
        } else if (node instanceof ASTIf) {
            processIf(method, (ASTIf) node);
        }
    }

    private void processIf(MethodDeclaration method, ASTIf node) throws Exception {
        Node ifCondition = node.jjtGetChild(0);
        Node ifBody = node.jjtGetChild(1);
        Node elseBody = node.jjtGetChild(2);

        System.out.println("--------------------------------------------------------");
        System.out.println(ifCondition);
        System.out.println(ifBody);
        System.out.println(elseBody);

        if(ifCondition.jjtGetChild(0) instanceof ASTLess) {
            ASTLess less = (ASTLess) ifCondition.jjtGetChild(0);
            processNode(method, less.jjtGetChild(0));
            processNode(method, less.jjtGetChild(1));
            toFile("if_icmplt ifBody");
            toFile("goto elseBody");
            toFile("ifBody:");
            System.out.println("start if body");

            for(int i = 0 ; i < ifBody.jjtGetNumChildren() ; i++) {
                System.out.println(ifBody.jjtGetChild(i));
                processNode(method, ifBody.jjtGetChild(i));
            }
            toFile("goto end");
            System.out.println("start else body");

            toFile("elseBody:");
            for(int i = 0 ; i < elseBody.jjtGetNumChildren() ; i++) {
                processNode(method, ifBody.jjtGetChild(i));
            }

            toFile("end :");
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