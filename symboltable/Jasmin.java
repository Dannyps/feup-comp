package symboltable;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        this.toFile(".class public " + className);
        this.toFile(".super java/lang/Object");
        this.toFile("");

        this.makeSummary();

        this.c.getAllMethods().forEach((k, v) -> {
            String methodLine = ".method public"; // all methods are public in Java--

            if (k == " main") { // only the main method is static in Java--
                methodLine += " static";
            }

            methodLine += " " + k.substring(0, k.length() - 2); // the method name without parentheses

            methodLine += "()"; // TODO args

            try {
                methodLine += getSignature(v.getReturnType());
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.toFile(methodLine);

            this.toFile(".limit stack 0");
            this.toFile("return");
            this.toFile(".end method");
        });

        // variables, methods, and stuff
        this.closeFile();
        return;
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
            }else{
                // TODO might be a Fully qualiffied class name
                return "L";
            }
            //throw new Exception("The passed returnType is not valid! Got " + returnType);
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