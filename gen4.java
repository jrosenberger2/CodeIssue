/**
 * gen4.java gnerates bytecode to compare two numbers, then print the result
 * @author Jared Rosenberger
 * @version 2/13/24
 */
import static utils.Utilities.writeFile;
import org.objectweb.asm.*;
public class gen4 {
    public static void main(String[] args){
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC,"program4", null, "java/lang/Object",null);

        //main visitor
        {
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC+Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
            mv.visitCode();

            //StringBuilder init
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(Opcodes.ASTORE, 1);
            //Set String
            setBuilder(mv, " is greater than ");
            
            Label intElseIf = new Label();
            Label intElse = new Label();
            Label intEnd = new Label();
            Label endMV = new Label();

            //Int Section
            //Declare & Store ints
            mv.visitIntInsn(Opcodes.BIPUSH, 10);
            mv.visitVarInsn(Opcodes.ISTORE, 2);
            mv.visitIntInsn(Opcodes.BIPUSH, 100);
            mv.visitVarInsn(Opcodes.ISTORE, 3);
            //Load & Compare ints 1&2
            mv.visitVarInsn(Opcodes.ILOAD, 2);
            mv.visitVarInsn(Opcodes.ILOAD, 3);
            mv.visitJumpInsn(Opcodes.IF_ICMPLE, intElseIf);
            
            //Case1 where a > b 
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitVarInsn(Opcodes.ILOAD, 2);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "insert", "(II)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(Opcodes.POP);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitVarInsn(Opcodes.ILOAD, 3);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(Opcodes.POP);
            mv.visitJumpInsn(Opcodes.GOTO, intEnd);
            

            mv.visitLabel(intElseIf);
            mv.visitVarInsn(Opcodes.ILOAD, 3);
            mv.visitVarInsn(Opcodes.ILOAD, 2);
            mv.visitJumpInsn(Opcodes.IF_ICMPLE, intElse);

            //Case2 where b > a 
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitVarInsn(Opcodes.ILOAD, 3);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "insert", "(II)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(Opcodes.POP);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitVarInsn(Opcodes.ILOAD, 2);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(Opcodes.POP);
            mv.visitJumpInsn(Opcodes.GOTO, intEnd);
            //end eIF          

            //Case3 where a = b
            mv.visitLabel(intElse); 
            clearBuilder(mv);
            setBuilder(mv, " is equal to ");
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitVarInsn(Opcodes.ILOAD, 2);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "insert", "(II)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(Opcodes.POP);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitVarInsn(Opcodes.ILOAD, 3);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(Opcodes.POP);
            mv.visitJumpInsn(Opcodes.GOTO, intEnd);
            
            mv.visitLabel(intEnd);
            printBuilder(mv);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(3,3);
            mv.visitEnd();

            /*
            //Long Section
            //Set String
            setBuilder(mv, "The long quotient is: ");
            //Declare 2 longs
            mv.visitLdcInsn((Long)7000000000l);
            mv.visitVarInsn(Opcodes.LSTORE, 5);
            mv.visitLdcInsn((Long)4000000000l);
            mv.visitVarInsn(Opcodes.LSTORE, 7);
            //Load & Divide Longs
            mv.visitVarInsn(Opcodes.LLOAD, 5);
            mv.visitVarInsn(Opcodes.LLOAD, 7);
            mv.visitInsn(Opcodes.LDIV);
            //Store & Print result
            mv.visitVarInsn(Opcodes.LSTORE, 9);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitVarInsn(Opcodes.LLOAD, 9);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(Opcodes.POP);
            printBuilder(mv);
            //clear string builder
            clearBuilder(mv);
            */
            
            //Return & End Visit
            
            //mv.visitInsn(Opcodes.RETURN);
            //mv.visitMaxs(0,0);
            //mv.visitEnd();
        }
        cw.visitEnd();
        //Write bytecode to another file
        byte[] b = cw.toByteArray();
        writeFile(b,"program4.class");
        System.out.println("Done!");
    }//end main

    /**
     * clearBuilder empties the string builder object used to print outputs
     * clearBuilder assumes that there is a StringBuilder Object at index 1
     * @param m is the MethodVisitor being used to visit the main method
     */
    private static void clearBuilder(MethodVisitor m) {
        m.visitVarInsn(Opcodes.ALOAD, 1);
        m.visitInsn(Opcodes.ICONST_0);
        m.visitVarInsn(Opcodes.ALOAD, 1);
        m.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "length", "()I", false);
        m.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "delete", "(II)Ljava/lang/StringBuilder;", false);
    }//end clearBuilder

    /**
     * setBuilder sets the string section for each println statement
     * setBuilder assumes that there is a Stringbuilder Object at index 1
     * @param m is the MethodVisitor being used to visit the main method
     * @param str is the String to append to the StringBuilder
     */
    private static void setBuilder(MethodVisitor m, String str) {
        m.visitVarInsn(Opcodes.ALOAD, 1);
        m.visitLdcInsn((String)str);
        m.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        m.visitInsn(Opcodes.POP);
    }//end setBuilder

    /**
     * printBuilder prints the current StringBuilder Object
     * printBuilder assumes there is an Object at index 1
     * @param m is the MethodVisitor being used to visit the main method
     */
    private static void printBuilder(MethodVisitor m) {
        m.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        m.visitVarInsn(Opcodes.ALOAD, 1);
        m.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);
    }//end printBuilder
}//end gen1