package org.example.fabricjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/**
 *
 * @author valentinebeats
 * @version 1.0
 * @date 2022/11/27
 * <p>
 */
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {

        SpringApplication.run(MyApplication.class, args);
       // jjf();
    }

    public  static void jjf(){
        List<String> res = new ArrayList<>();
        int i=0;
        int number1=0,number2=0,number3=0,number4=0;
        for(int k=0;k<150;k++) {
            number1=(int) ((Math.random()*10))%9+1;
            number2=(int) ((Math.random()*10))%9+1;
            //加法部分
            String r1=""+number1+" + "+number2+" = "+(number1+number2);
            System.out.println(""+number1+" + "+number2+" =");
            res.add(r1);

            number3=(int) ((Math.random()*20))%19+1;
            number4=(int) ((Math.random()*20))%19+1;
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


