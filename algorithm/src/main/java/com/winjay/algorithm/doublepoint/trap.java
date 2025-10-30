package com.winjay.algorithm.doublepoint;

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

        int result1 = trap(nums);
        System.out.println(result1);
        int result2 = trap(nums2);
        System.out.println(result2);
    }

    // 时间复杂度：O(n)，其中 n 是数组 height 的长度。两个指针的移动总次数不超过 n。
    // 空间复杂度：O(1)。只需要使用常数的额外空间。
    public static int trap(int[] height) {
        int capacity = 0;
        int left = 0, right = height.length - 1;
        int leftMax = 0, rightMax = 0;
        while (left < right) {
            leftMax = Math.max(leftMax, height[left]);
            rightMax = Math.max(rightMax, height[right]);
            if (height[left] < height[right]) {
                capacity += leftMax - height[left];
                ++left;
            } else {
                capacity += rightMax - height[right];
                --right;
            }
        }
        return capacity;
    }
}
