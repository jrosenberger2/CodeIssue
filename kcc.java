/**
 * 
 * @author Jared Rosenberger
 * @version 1
 * Assignment 5
 * CS322 - Compiler Construction
 * Spring 2024
 */
package compiler;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

//ANTLR packages
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.gui.Trees;

import lexparse.*;

public class kcc{


    public static void main(String[] args){
        CharStream input;
        KnightCodeLexer lexer;
        CommonTokenStream tokens;
        KnightCodeParser parser;

        try{
            input = CharStreams.fromFileName(args[0]);  //get the input
            lexer = new KnightCodeLexer(input); //create the lexer
            tokens = new CommonTokenStream(lexer); //create the token stream
            parser = new KnightCodeParser(tokens); //create the parser
       
            ParseTree tree = parser.file();  //set the start location of the parser
            
            SymbolTable vars = new SymbolTable();
            String genName = "Gen" + args[1].substring(args[1].indexOf(".")-1, args[1].indexOf("."));
            String outputName = args[1].substring(args[1].indexOf("/")+1, args[1].indexOf("."));
            File output = new File("output/" + genName + ".java");
            //String code = "";
            try {
                FileOutputStream outStream = new FileOutputStream(output);
                String boilerplate = "package output;\nimport org.objectweb.asm.*;\nimport java.io.File;\nimport java.io.FileOutputStream;\nimport java.io.IOException;\n";
                boilerplate += "public class " + genName + " { public static void main(String[] args) {\n";
                boilerplate += "System.out.println(\"Generating Bytecode...\");\nClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);\n";
                boilerplate += "cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC,\"output/" + outputName + "\", null, \"java/lang/Object\",null);\n";
                boilerplate += "MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC+Opcodes.ACC_STATIC, \"main\", \"([Ljava/lang/String;)V\", null, null);\n";
                boilerplate += "mv.visitCode();\n";
                
                
                MyVisitor visit = new MyVisitor(vars, output);
                boilerplate += visit.visit(tree);
                
                boilerplate += "cw.visitEnd();\n";
                boilerplate += "byte[] b = cw.toByteArray();\ntry{";
                boilerplate += "File out = new File(\"output/" +outputName+".class\");\n";
                boilerplate += "FileOutputStream byteOut = new FileOutputStream(out);\n";
                boilerplate += "byteOut.write(b);\n";
                boilerplate += "byteOut.close();\n}catch(IOException e){System.out.println(e.getMessage());}\nSystem.out.println(\"Done!\");\n}\n}";
                outStream.write(boilerplate.getBytes());
                outStream.close();
            } catch(FileNotFoundException e) {System.out.println(e.getMessage());}


            
            

            //Trees.inspect(tree, parser);  //displays the parse tree
            
            vars.print();
            //System.out.println(tree.toStringTree(parser));
        
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }//end main

    public static void setupOutput(File out) {
        
    }//end setupOutput
}//end kcc