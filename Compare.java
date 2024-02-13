//import org.antlr.v4.parse.ANTLRParser.elementOptions_return;

public class Compare {
    public static void main(String[] args) {
        StringBuilder output = new StringBuilder();
        output.append(" is greater than ");
        int a = 10;
        int b = 100;
        
        if(a > b){
            output.insert(0, a);
            output.append(b);
        }
        else if(b > a){
            output.insert(0, b);
            output.append(a);
        }
        else {
            output.delete(0, output.length());
            output.append(a);
            output.append(" is equal to ");
            output.append(b);
        }
        System.out.println(output);
    }//end main
}//end Compare