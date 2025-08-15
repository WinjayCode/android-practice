package com.winjay.algorithm.hash;

import com.winjay.algorithm.sort.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 最长连续序列 (128-中等)
 *
 * 给定一个未排序的整数数组 nums ，找出数字连续的最长序列（不要求序列元素在原数组中连续）的长度。
 * 请你设计并实现时间复杂度为 O(n) 的算法解决此问题。
 *
 * 示例 1：
 * 输入：nums = [100,4,200,1,3,2]
 * 输出：4
 * 解释：最长数字连续序列是 [1, 2, 3, 4]。它的长度为 4。
 *
 * 示例 2：
 * 输入：nums = [0,3,7,2,5,8,4,6,0,1]
 * 输出：9
 *
 * 示例 3：
 * 输入：nums = [1,0,1,2]
 * 输出：3
 */
public class longestConsecutive {
    public static void main(String[] args) {
        int[] nums = new int[]{100, 4, 200, 1, 3, 2};
        int[] nums2 = new int[]{0,3,7,2,5,8,4,6,0,1};
        int[] nums3 = new int[]{9,1,-3,2,4,8,3,-1,6,-2,-4,7};
        int[] nums4 = new int[]{7,-9,3,-6,3,5,3,6,-2,-5,8,6,-4,-6,-4,-4,5,-9,2,7,0,0};
        int longest = longestConsecutive(nums4);
        System.out.println("longest=" + longest);
    }

    // 时间复杂度为o(n)
    public static int longestConsecutive(int[] nums) {
        Set<Integer> num_set = new HashSet<>();
        for (int num : nums) {
            num_set.add(num); // 自带去重
        }

        int longest = 0;

        for (int num : num_set) {
            if (!num_set.contains(num - 1)) { // 从连续序列数字的最小值开始查找，避免增加时间复杂度
                int currentNum = num;
                int currentStreak = 1;

                while (num_set.contains(currentNum + 1)) {
                    currentNum += 1;
                    currentStreak += 1;
                }

                longest = Math.max(longest, currentStreak);
            }
        }

        return longest;
    }

    // 去重，排序，统计
    // 时间复杂度为o(n^2)
    public static int longestConsecutive_my(int[] nums) {
        if (nums.length < 2) {
            return nums.length;
        }

        int longest = 0;

        List<Integer> list = new ArrayList<>();
        for (int num : nums) {
            // list.contains(num) 方法的时间复杂度是 O(n)，再加上for循环，整个去重操作的时间复杂度是 O(n^2)
            if (!list.contains(num)) {
                list.add(num);
            }
        }
        System.out.println("list=" + list);

        // 冒泡排序的时间复杂度是 O(n^2)
        int[] sortArray = Sort.bubble_sort(list.stream().mapToInt(Integer::intValue).toArray());
        System.out.println("sortArray=" + Arrays.toString(sortArray));

        int length = 1;
        for (int i = 1; i < sortArray.length; i++) {
            if (sortArray[i] - sortArray[i - 1] == 1) {
                ++length;
            } else {
                if (length >= longest) {
                    longest = length;
                }
                length = 1;
            }
        }
        return Math.max(length, longest);
    }
}
