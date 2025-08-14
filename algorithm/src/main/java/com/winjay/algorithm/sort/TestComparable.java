package com.winjay.algorithm.sort;

/**
 * @author Winjay
 * @date 2022-04-16
 */
public class TestComparable {

    public static void main(String[] args) {
        Student s1 = new Student();
        s1.setName("张三");
        s1.setAge(18);

        Student s2 = new Student();
        s2.setName("李四");
        s2.setAge(20);

        Comparable max = getMax(s1, s2);
        System.out.println(max);
    }

    public static Comparable getMax(Comparable c1, Comparable c2) {
        int result = c1.compareTo(c2);
        // 如果result<0，则c1比c2小
        // 如果result>0，则c1比c2大
        // 如果result==0，则c1和c2一样大
        return result >= 0 ? c1 : c2;
    }
}
