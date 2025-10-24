package com.winjay.algorithm.doublepoint;

import java.util.Arrays;
import java.util.List;

/**
 * 接雨水 (42-困难)
 * <p>
 * 给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，下雨之后能接多少雨水。
 * <p>
 * 示例 1：
 * 输入：height = [0,1,0,2,1,0,1,3,2,1,2,1]
 * 输出：6
 * 解释：上面是由数组 [0,1,0,2,1,0,1,3,2,1,2,1] 表示的高度图，在这种情况下，可以接 6 个单位的雨水（蓝色部分表示雨水）。
 * <p>
 * 示例 2：
 * 输入：height = [4,2,0,3,2,5]
 * 输出：9
 */
public class trap {
    public static void main(String[] args) {
        int[] nums = new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1};
        int[] nums2 = new int[]{4, 2, 0, 3, 2, 5};

//        int result1 = trap_my(nums);
//        System.out.println(result1);
//        int result2 = trap_my(nums2);
//        System.out.println(result2);

//        int[] test = new int[]{0, 1, 2, 0, 1, 2};
        int[] test = new int[]{3, 2, 1, 2, 1};
        int testResult = trap_my(test);
        System.out.println(testResult);
    }

    public static int trap(int[] height) {
        int capacity = 0;
        return capacity;
    }


//    0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1
//    4, 2, 0, 3, 2, 5
//    0, 1, 2, 0, 1, 2
//    3, 2, 1, 2, 1
//    4, 3, 1, 2, 1
    public static int trap_my(int[] height) {
        int totalCapacity = 0;
        int n = height.length;
//        int capacity;
        int invalidCapacity = 0;
        boolean haveLeftBoundary = false;
        for (int left = 0; left < n; left++) {
            boolean haveSpace = false;

            int lastNum = height[left];
            // 左指针不是0时，开始移动右指针
            for (int right = left + 1; right < n; right++) {
                // 右 == 左
                if (height[right] == lastNum) {
                    continue;
                }

                // 右指针非零时，和左指针做比较
                if (height[right] > lastNum) {
                    if (haveLeftBoundary) {
                        haveSpace = true;
                    }
                    // 前面有零标记时，开始计算当前容器的容量
                    if (haveSpace) {
                        invalidCapacity += height[right];
                    }
                }
                    // 右 >= 左
//                if (height[right] >= height[left]) {
//                    // 前面有零标记时，开始计算当前容器的容量
//                    if (haveSpace) {
//                        capacity = height[left] * (right - left);
//                        totalCapacity += capacity;
//                        haveSpace = false;
//
//                        left = right + 1;
//                    }
//                    // 前面没有零标记时，右 >= 左，左指针右移
//                    else {
//                        break;
//                    }
//                }

                // 右 < 左
                if (height[right] < lastNum) {
                    if (haveSpace) {
                        totalCapacity = lastNum * (right - 1 - left);
                        System.out.println("totalCapacity=" + totalCapacity + ", invalidCapacity=" + invalidCapacity);
                        totalCapacity -= invalidCapacity;
                    } else {
                        haveLeftBoundary = true;
                        lastNum = height[right];
                        invalidCapacity += height[right];
                    }
                }
            }

//            if (left == n) {
//                break;
//            }
        }
        return totalCapacity;
    }
}
