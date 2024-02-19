import java.util.Scanner;
public class Scanner9 {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please input a double");
        //String input = scan.nextLine();
        //double num = Double.parseDouble(input);
        double num = scan.nextDouble();
        double total = 0;
        int i = 0;
        while(i < 10) {
            total += num;
        }
        System.out.println("Your toal is: " + total);
        
        scan.close();
    }//end main
}//end Scanner9