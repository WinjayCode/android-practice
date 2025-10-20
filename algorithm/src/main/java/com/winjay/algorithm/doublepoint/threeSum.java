package com.winjay.algorithm.doublepoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        int[] nums4 = new int[]{0, 0, 0, 0};
        int[] nums5 = new int[]{0, 0, 0, 0, -1, 0, 1, 2, -1, -4};
        int[] nums6 = new int[]{-4, -2, 1, -5, -4, -4, 4, -2, 0, 4, 0, -2, 3, 1, -5, 0};

        List<List<Integer>> result1 = threeSum(nums);
        System.out.println(result1);
        List<List<Integer>> result2 = threeSum(nums2);
        System.out.println(result2);
        List<List<Integer>> result3 = threeSum(nums3);
        System.out.println(result3);
        List<List<Integer>> result4 = threeSum(nums4);
        System.out.println(result4);
        List<List<Integer>> result5 = threeSum(nums5);
        System.out.println(result5);
        List<List<Integer>> result6 = threeSum(nums6);
        System.out.println(result6);



        List<List<Integer>> listList1 = threeSum_my(nums);
        List<List<Integer>> listList2 = threeSum_my(nums2);
        List<List<Integer>> listList3 = threeSum_my(nums3);
        List<List<Integer>> listList4 = threeSum_my(nums4);
        List<List<Integer>> listList5 = threeSum_my(nums5);
        List<List<Integer>> listList6 = threeSum_my(nums6);

        System.out.println(listList1);
        System.out.println(listList2);
        System.out.println(listList3);
        System.out.println(listList4);
        System.out.println(listList5);
        System.out.println(listList6);
    }

    // 时间复杂度: O(n^2)，其中 N 是数组 nums 的长度。
    // 空间复杂度：O(logN)。我们忽略存储答案的空间，额外的排序的空间复杂度为 O(logN)。
    // 然而我们修改了输入的数组 nums，在实际情况下不一定允许，因此也可以看成使用了一个额外的数组存储了 nums 的副本并进行排序，空间复杂度为 O(N)。
    public static List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> listList = new ArrayList<>();
        int n = nums.length;
        Arrays.sort(nums);
        // 枚举 a
        for (int first = 0; first < n; first++) {
            // 需要和上一次枚举的数不相同
            if (first > 0 && nums[first] == nums[first - 1]) {
                continue;
            }
            // c 对应的指针初始指向数组的最右端
            int third = n - 1;
            int target = 0 - nums[first];
            // 枚举 b
            for (int second = first + 1; second < n; second++) {
                // 需要和上一次枚举的数不相同
                if (second > first + 1 && nums[second] == nums[second - 1]) {
                    continue;
                }
                // 需要保证 b 的指针在 c 的指针的左侧
                while (second < third && nums[second] + nums[third] > target) {
                    --third;
                }
                // 如果指针重合，随着 b 后续的增加，就不会有满足 a+b+c=0 并且 b<c 的 c 了，可以退出循环
                if (second == third) {
                    break;
                }

                if (nums[second] + nums[third] == target) {
                    List<Integer> list = new ArrayList<>();
                    list.add(nums[first]);
                    list.add(nums[second]);
                    list.add(nums[third]);
                    listList.add(list);
                }
            }
        }
        return listList;
    }



    // 时间复杂度: O(n^2)
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

        int left = 0, right = 1, n = nums.length;
        Arrays.sort(nums);
        while (right < n - 1) {
            int fixedNum = nums[left];
            if (left > 0 && nums[left] == nums[left - 1]) {
                ++left;
                right = left + 1;
                continue;
            }
            int target = 0 - fixedNum - nums[right];

            int[] newNums = Arrays.copyOfRange(nums, right + 1, n);
            List<Integer> numList = Arrays.stream(newNums).boxed().toList();

            if (numList.contains(target)) {
                List<Integer> list = new ArrayList<>();
                list.add(fixedNum);
                list.add(nums[right]);
                list.add(target);
                if (!haveSame(listList, list)) {
                    listList.add(list);
                }
            }
            ++right;
            if (right == n - 1) {
                ++left;
                right = left + 1;
            }
        }

        return listList;
    }

    public static boolean haveSame(List<List<Integer>> listList, List<Integer> list) {
        boolean haveSame = false;
        for (List<Integer> listIntegers : listList) {
            Collections.sort(listIntegers);
            Collections.sort(list);
            System.out.println("haveSame(): listIntegers=" + listIntegers + ", list=" + list);
            if (listIntegers.equals(list)) {
                haveSame = true;
                break;
            }
        }
        return haveSame;
    }
}
