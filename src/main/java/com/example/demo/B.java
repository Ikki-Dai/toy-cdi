/* Top Secret */
package com.example.demo;


import com.example.demo.annotation.Component;

@Component
public class B {

    private A a;


    public B(A a) {
        this.a = a;
    }

    public void test() {
        System.out.println("B hashCode: " + this.hashCode() + ", member a has inject ? : " + (a != null) + ", hashCode: " + a.hashCode());
    }

}
