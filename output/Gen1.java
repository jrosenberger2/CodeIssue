package output;
import org.objectweb.asm.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
public class Gen1 { public static void main(String[] args) {
System.out.println("Generating Bytecode...");
ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC,"output/program1", null, "java/lang/Object",null);
MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC+Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
mv.visitCode();
mv.visitIntInsn(Opcodes.BIPUSH,10);
mv.visitVarInsn(Opcodes.ISTORE,1);
mv.visitIntInsn(Opcodes.BIPUSH,12);
mv.visitVarInsn(Opcodes.ISTORE,2);
mv.visitVarInsn(Opcodes.ILOAD,1);
mv.visitVarInsn(Opcodes.ILOAD,2);
mv.visitInsn(Opcodes.IADD);
mv.visitVarInsn(Opcodes.ISTORE,3);
mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
mv.visitVarInsn(Opcodes.ILOAD,3);
mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
cw.visitEnd();
byte[] b = cw.toByteArray();
try{File out = new File("output/program1.class");
FileOutputStream byteOut = new FileOutputStream(out);
byteOut.write(b);
byteOut.close();
}catch(IOException e){System.out.println(e.getMessage());}
System.out.println("Done!");
}
}