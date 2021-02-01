/* Top Secret */
package com.example.demo;

import com.example.demo.context.Context;

public class M {

    public static void main(String[] args) {
//        ApplicationContext context = new AnnotationConfigApplicationContext("com.example.demo");
//        B b = context.getBean("b", B.class);
//        b.test();

        Context context = new Context("com.example.demo");
        context.refresh();

        B b = context.getBean(B.class);
        b.test();


    }

}
