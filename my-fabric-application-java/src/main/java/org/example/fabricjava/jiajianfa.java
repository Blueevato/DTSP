package org.example.fabricjava;// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class jiajianfa {
    public static void main(String[] args) {
        // Press Alt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
      //  System.out.printf("Hello and welcome!");

        // Press Shift+F10 or click the green arrow button in the gutter to run the code.
       // for (int i = 1; i <= 5; i++) {

            // Press Shift+F9 to start debugging your code. We have set one breakpoint
            // for you, but you can always add more by pressing Ctrl+F8.
      //      System.out.println("i = " + i);
//100以内加减法麻烦也弄一下，加起来不超过100，被减数也不超过100
        jjf();
        }
    public  static void jjf(){
        List<String> res = new ArrayList<>();
        int i=0;
        int test=0;
        int number1=0,number2=0,number3=0,number4=0;
        for(int k=0;k<900;k++) {
            number1=(int) ((Math.random()*100))%9+1;
            number2=(int) ((Math.random()*100))%9+1;
            while(number1+number2>=20){
                number1=(int) ((Math.random()*100))%9+1;
                number2=(int) ((Math.random()*100))%9+1;
            }
            //加法部分
            String r1=""+number1+" + "+number2+" = "+(number1+number2);
            System.out.println(""+number1+" + "+number2+" =");
            res.add(r1);

            number3=(int) ((Math.random()*100))%9+1;
            number4=(int) ((Math.random()*100))%9+1;
            //减法部分
            if(number3<number4) {
                int temp;
                temp=number3;
                number3=number4;
                number4=temp;
            }
            String r2 = ""+number3+" - "+number4+" = "+(number3-number4);
            res.add(r2);
            System.out.println(""+number3+" - "+number4+" =");
        }
        for(int j=0;j<res.size();j++){
            System.out.println(""+ res.get(j));
        }
    }
}