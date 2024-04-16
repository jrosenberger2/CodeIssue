/**
 * 
 * @author Jared Rosenberger
 * @version 1
 * Assignment 5
 * CS322 - Compiler Construction
 * Spring 2024
 */
package compiler;
import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.Opcodes;

import lexparse.KnightCodeBaseVisitor;
import lexparse.KnightCodeParser;
import lexparse.KnightCodeParser.*;
import java.util.HashMap;
import java.io.File;
public class MyVisitor extends KnightCodeBaseVisitor{
    private SymbolTable symbols;
    private SymbolTable index;
    private File output;
    private String asmCode;
    private int storageIndex;

    public MyVisitor(SymbolTable sym) {
        symbols = sym;
        asmCode = "";
        storageIndex = 1;
        index = new SymbolTable();
    }//end Constructor

    public MyVisitor(SymbolTable sym, File out) {
        symbols = sym;
        output = out;
        asmCode = "";
        storageIndex = 1;
        index = new SymbolTable();
    }//end Constructor

    @Override
    public String visitFile(KnightCodeParser.FileContext ctx) { 
        //return visitChildren(ctx);
        visitDeclare((DeclareContext) ctx.getChild(2));
        return visitBody((BodyContext) ctx.getChild(3));
    }//end visitFile

    @Override
    public Object visitDeclare(KnightCodeParser.DeclareContext ctx) {
        System.out.println("visiting Declare");
        System.out.println("Declare has " + ctx.getChildCount() + " kids");
        if(ctx.getChildCount() >=2) {
            for(int i = 1; i<ctx.getChildCount(); i++) {
                ParseTree tempNode = ctx.getChild(i);
                //System.out.println("Entered a Var with " + tempNode.getChildCount() + " children." + tempNode.getText());
                if(tempNode.getChild(0).getText().compareTo("INTEGER") == 0) {
                    Variable temp = new Variable(0);
                    String idName = visitIdentifier((IdentifierContext) tempNode.getChild(1));
                    symbols.addEntry(idName, temp);
                }
                else {
                    Variable temp = new Variable("\"\"");
                    symbols.addEntry(ctx.getChild(1).getText(), temp);
                }
            }
        }
        System.out.println("Symbol Table after declare:");
        //symbols.print();
        return "";//visitChildren(ctx);
    }//end visitDeclare

    @Override 
    public String visitBody(KnightCodeParser.BodyContext ctx) { 
        System.out.println("visiting Body, it has " + ctx.getChildCount() + "children");
        for(int i = 1; i<ctx.getChildCount()-1; i++) {
            ParseTree tempNode = ctx.getChild(i);
            //System.out.println("tempNode is " + tempNode);
            ParseTree childNode = tempNode.getChild(0);
            String childName = childNode.getText();
            //System.out.println("childNode is " + childNode.hashCode());
            
            if(childName.substring(0, 3).equals("SET")) {
                //System.out.println("Found setvar");
                visitSetvar((SetvarContext) childNode);
            }
            else if(childName.indexOf('*') != -1) {
                System.out.println("Found MULT");
            }
            else if(childName.indexOf('/') != -1) {
                System.out.println("Found DIV");
            }
            else if(childName.indexOf('+') != -1) {
                System.out.println("Found ADD");
                asmCode = visitAddition((AdditionContext) childNode);
            }
            else if(childName.indexOf('-') != -1) {
                System.out.println("Found SUB");
            }
            else if(childName.indexOf('>') != -1 || childName.indexOf('<') != -1 || childName.indexOf('=') != -1 || childName.indexOf("<>") != -1) {
                System.out.println("Found COMP");
            }
            else if(childName.substring(0,5).equals("PRINT")) {
                visitPrint((PrintContext) childNode);
            }
            else if(childName.compareTo("read") == 0) {

            }
            else if(childName.compareTo("decision") == 0) {

            }
            else if(childName.compareTo("loop") == 0) {

            }
        }
        return asmCode; 
    }//end visitBody

    @Override 
    public Object visitSetvar(KnightCodeParser.SetvarContext ctx) { 
        System.out.println("visiting setvar");
        String id = ctx.getChild(1).getText();
        //System.out.println("ID Token: " + id);
        String exprTxt = ctx.getChild(3).getText();
        if(symbols.getValue(id) != null && ctx.getChild(3).getChildCount() == 1) {
            Variable temp = symbols.getValue(id);
            if(!temp.isString()) {
                temp.setInt((Integer.parseInt(ctx.getChild(3).getChild(0).getText())));
                symbols.addEntry(id, temp);
            }
            else {
                temp.setString(exprTxt);
                symbols.addEntry(id, temp);
            }
        }
        else if(exprTxt.indexOf('*') != -1) {
            System.out.println("Found MULT");

            index.addEntry(id, new Variable(storageIndex-1));
            symbols.remove(id);
        }
        else if(exprTxt.indexOf('/') != -1) {
            System.out.println("Found DIV");

            index.addEntry(id, new Variable(storageIndex-1));
            symbols.remove(id);
        }
        else if(exprTxt.indexOf('+') != -1) {
            System.out.println("Found ADD");
            asmCode += visitAddition((AdditionContext) ctx.getChild(3));
            index.addEntry(id, new Variable(storageIndex-1));
            symbols.remove(id);
        }
        else if(exprTxt.indexOf('-') != -1) {
            System.out.println("Found SUB");

            index.addEntry(id, new Variable(storageIndex-1));
            symbols.remove(id);
        }

        return asmCode; 
    }

    @Override 
    public String visitPrint(KnightCodeParser.PrintContext ctx) { 
        System.out.println("Visiting print");
        if(index.getValue(ctx.ID().getText()) != null) {
            //System.out.println(symbols.getValue(ctx.ID().getText()).getInt());
            Variable temp = index.remove(ctx.ID().getText());
            asmCode += "mv.visitFieldInsn(Opcodes.GETSTATIC, \"java/lang/System\", \"out\", \"Ljava/io/PrintStream;\");\n";
            asmCode += "mv.visitVarInsn(Opcodes.ILOAD,"+temp.getInt()+");\n";
            asmCode += "mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, \"java/io/PrintStream\", \"println\", \"(I)V\", false);\n";
        }
        else if(symbols.getValue(ctx.ID().getText()) != null) {
            asmCode += "mv.visitFieldInsn(Opcodes.GETSTATIC, \"java/lang/System\", \"out\", \"Ljava/io/PrintStream;\");\n";
            asmCode += "mv.visitLdcInsn((String)\""+symbols.remove(ctx.ID().getText()).getString()+"\");\n";
            asmCode += "mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, \"java/io/PrintStream\", \"println\", \"(Ljava/lang/String;)V\", false);\n";
        }
        else {
            //System.out.println(ctx.STRING());
            asmCode += "mv.visitFieldInsn(Opcodes.GETSTATIC, \"java/lang/System\", \"out\", \"Ljava/io/PrintStream;\");\n";
            asmCode += "mv.visitLdcInsn((String)\""+ctx.STRING()+"\");\n";
            asmCode += "mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, \"java/io/PrintStream\", \"println\", \"(Ljava/lang/String;)V\", false);\n";
        }
        return "";
    }//end visitPrint

    @Override 
    public String visitAddition(KnightCodeParser.AdditionContext ctx) {
        System.out.println("Visiting ADD");
        //System.out.println(ctx.getChildCount());
        int a=0;
        int b;
        String termA = ctx.getChild(0).getText();
        String termB = ctx.getChild(2).getText();
        if(symbols.getValue(termA) != null) {
            a = symbols.remove(termA).getInt();
        }
        if(symbols.getValue(termB) != null) {
            b = symbols.remove(termB).getInt();
        }
        else {
            a = Integer.parseInt(termA);
            b = Integer.parseInt(termB);
        }
        asmCode += "mv.visitIntInsn(Opcodes.BIPUSH,"+a+");\n";
        asmCode += "mv.visitVarInsn(Opcodes.ISTORE,"+storageIndex+");\n";
        index.addEntry(termA, new Variable(storageIndex));
        storageIndex++;
        asmCode += "mv.visitIntInsn(Opcodes.BIPUSH,"+b+");\n";
        asmCode += "mv.visitVarInsn(Opcodes.ISTORE,"+storageIndex+");\n";
        index.addEntry(termB, new Variable(storageIndex));
        storageIndex++;
        asmCode += "mv.visitVarInsn(Opcodes.ILOAD,"+(storageIndex-2)+");\n";
        asmCode += "mv.visitVarInsn(Opcodes.ILOAD,"+(storageIndex-1)+");\n";
        asmCode += "mv.visitInsn(Opcodes.IADD);\n";
        asmCode += "mv.visitVarInsn(Opcodes.ISTORE,"+storageIndex+");\n";
        storageIndex++;
        return asmCode; 
    }

    @Override 
    public String visitIdentifier(KnightCodeParser.IdentifierContext ctx) { 
        //System.out.println("visitIdentifier: " + ctx.ID().getText());
        return ctx.ID().getText();
    }//end visitIdentifier

    @Override 
    public String visitId(KnightCodeParser.IdContext ctx) { 
        //System.out.println("visitID: " + ctx.ID().getText());
        return ctx.ID().getText(); 
    }//end visitID
}//end MyVisitor