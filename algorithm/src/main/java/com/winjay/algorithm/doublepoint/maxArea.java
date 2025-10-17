package com.winjay.algorithm.doublepoint;

/**
 * 盛最多水的容器 (11-中等)
 *
 * 给定一个长度为 n 的整数数组 height 。有 n 条垂线，第 i 条线的两个端点是 (i, 0) 和 (i, height[i]) 。
 * 找出其中的两条线，使得它们与 x 轴共同构成的容器可以容纳最多的水。
 * 返回容器可以储存的最大水量。
 * 说明：你不能倾斜容器。
 *
 * 示例 1：
 * 输入：[1,8,6,2,5,4,8,3,7]
 * 输出：49
 * 解释：图中垂直线代表输入数组 [1,8,6,2,5,4,8,3,7]。在此情况下，容器能够容纳水（表示为蓝色部分）的最大值为 49。
 *
 * 示例 2：
 * 输入：height = [1,1]
 * 输出：1
 *
 * 解题思路：
 * 一句话概括：我们left++和right--都是为了尝试取到更多的水，如果短的板不动的话，取到的水永远不会比上次多。
 */
public class maxArea {
    public static void main(String[] args) {
        int[] nums = new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7};
        int[] nums2 = new int[]{1, 1};

        maxArea(nums);
        maxArea(nums2);

        maxArea_my(nums);
        maxArea_my(nums2);
    }

    // 时间复杂度：O(N)，双指针总计最多遍历整个数组一次。
    // 空间复杂度：O(1)，只需要额外的常数级别的空间。
    public static void maxArea(int[] height) {
        int left = 0, right = height.length - 1, maxArea = 0;
        while (left < right) {
            int area = Math.min(height[left], height[right]) * (right - left);
            maxArea = Math.max(maxArea, area);
            if (height[left] <= height[right]) {
                ++left;
            } else {
                --right;
            }
        }
        System.out.println(maxArea);
    }

    // 时间复杂度: O(n^2)
    public static void maxArea_my(int[] height) {
        if (height.length < 2) {
            return;
        }
        int maxResult = Integer.MIN_VALUE;
        for (int i = 0; i < height.length; i++) {
            for (int j = i + 1; j < height.length; j++) {
                int result = (j - i) * Math.min(height[i], height[j]);
                maxResult = Math.max(maxResult, result);
            }
        }
        System.out.println(maxResult);
    }
}
