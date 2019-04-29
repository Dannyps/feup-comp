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

    private Path outputDir = Paths.get("output");

    public Jasmin(Main m) {
        this.c = m.getClassDeclaration();
        String className = this.c.getName();
        this.openFile(className);
        this.toFile(".class public " + className);
        this.toFile(".super java/lang/Object");
        this.toFile("");
        this.toFile(".method static public <clinit>()V");

        this.closeFile();
        return;
    }

    private boolean openFile(String cname) {
        try {
            if(!Files.exists(this.outputDir)){
                Files.createDirectories(this.outputDir);
            }
            this.f = new FileWriter("output" + File.separator +  cname + ".j");
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
                f.write(s + "\n");
            } catch (Exception e) {
            }
        } else {
            System.err.println("File was unexpectedely closed! Couldn't write: " + s);
            System.exit(-4);
        }
    }
}