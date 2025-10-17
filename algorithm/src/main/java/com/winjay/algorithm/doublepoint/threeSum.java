package com.winjay.algorithm.doublepoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * 三数之和 (15-中等)
 *
 * 给你一个整数数组 nums ，判断是否存在三元组 [nums[i], nums[j], nums[k]] 满足 i != j、i != k 且 j != k ，同时还满足 nums[i] + nums[j] + nums[k] == 0 。
 * 请你返回所有和为 0 且不重复的三元组。
 * 注意：答案中不可以包含重复的三元组。
 *
 * 示例 1：
 * 输入：nums = [-1,0,1,2,-1,-4]
 * 输出：[[-1,-1,2],[-1,0,1]]
 * 解释：
 * nums[0] + nums[1] + nums[2] = (-1) + 0 + 1 = 0 。
 * nums[1] + nums[2] + nums[4] = 0 + 1 + (-1) = 0 。
 * nums[0] + nums[3] + nums[4] = (-1) + 2 + (-1) = 0 。
 * 不同的三元组是 [-1,0,1] 和 [-1,-1,2] 。
 * 注意，输出的顺序和三元组的顺序并不重要。
 *
 * 示例 2：
 * 输入：nums = [0,1,1]
 * 输出：[]
 * 解释：唯一可能的三元组和不为 0 。
 *
 * 示例 3：
 * 输入：nums = [0,0,0]
 * 输出：[[0,0,0]]
 * 解释：唯一可能的三元组和为 0 。
 */
public class threeSum {
    public static void main(String[] args) {
        int[] nums = new int[]{-1, 0, 1, 2, -1, -4};
        int[] nums2 = new int[]{0, 1, 1};
        int[] nums3 = new int[]{0, 0, 0};

        List<List<Integer>> listList1 = threeSum_my(nums);
        List<List<Integer>> listList2 = threeSum_my(nums2);
        List<List<Integer>> listList3 = threeSum_my(nums3);

        System.out.println(listList1);
        System.out.println(listList2);
        System.out.println(listList3);

//        int[] nums4 = new int[]{0, 1, 2, -1, -4};
//        List<List<Integer>> listList4 = twoSum(nums4, 1, -1);
//        System.out.println(listList4);
    }

    public static List<List<Integer>> threeSum(int[] nums) {
        return new ArrayList<>();
    }

    public static List<List<Integer>> threeSum_my(int[] nums) {
        if (nums.length < 3) {
            return new ArrayList<>();
        }

        List<List<Integer>> listList = new ArrayList<>();

        if (nums.length == 3) {
            if (nums[0] + nums[1] + nums[2] == 0) {
                List<Integer> list = Arrays.stream(nums).boxed().toList();
                listList.add(list);
                return listList;
            }
        }
//        -1, 0, 1, 2, -1, -4

        int left = 0, right = 1, n = nums.length;
        while (right < n) {
            int fixedNum = nums[left];
            int target = 0 - fixedNum - nums[right];

            int[] newNums = Arrays.copyOfRange(nums, right + 1, n);
            List<Integer> numList = Arrays.stream(newNums).boxed().toList();

            if (numList.contains(target)) {
                List<Integer> list = new ArrayList<>();
                list.add(fixedNum);
                list.add(nums[right]);
                list.add(target);
                System.out.println("list=" + list);
                System.out.println();
                listList.add(list);
            }
            ++right;
        }

        return listList;
    }

    public static List<List<Integer>> twoSum(int[] nums, int target, int fixedNum) {
        List<List<Integer>> listList = new ArrayList<>();
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (hashMap.containsKey(target - nums[i])) {
                List<Integer> list = new ArrayList<>();
                list.add(fixedNum);
                list.add(nums[i]);
                list.add(hashMap.get(target - nums[i]));
                listList.add(list);
            }
            hashMap.put(nums[i], i);
        }
        return listList;
    }
}
