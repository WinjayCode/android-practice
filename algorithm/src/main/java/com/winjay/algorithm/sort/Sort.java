package com.winjay.algorithm.sort;

import java.util.Arrays;

public class Sort {
    public static void main(String[] args) {
        int[] arr = new int[]{6, 3, 8, 1, 9};
        int[] result = bubble_sort(arr);
        System.out.println("Sort result=" + Arrays.toString(result));
        // 3,6,1,8,9
        // 3,1,6,8,9
        // 1,3,6,8,9
    }

    /**
     * 冒泡排序
     *
     * 算法逻辑：
     * 1.依次比较相邻元素，前大后小则交换
     * 2.每轮遍历将最大值冒泡到末尾
     * 3.重复n-1轮完成排序
     */
    public static int[] bubble_sort(int[] arr) {
        boolean r = false;
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                int temp = arr[i];
                arr[i] = arr[i + 1];
                arr[i + 1] = temp;
                r = true;
            }
        }
        if (r) {
            System.out.println("sort");
            bubble_sort(arr);
        } else {
            System.out.println(Arrays.toString(arr));
        }
        return arr;
    }
}
