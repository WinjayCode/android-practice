package com.winjay.algorithm.doublepoint;

import java.util.Arrays;

/**
 * 移动零 (283-简单)
 *
 * 给定一个数组 nums，编写一个函数将所有 0 移动到数组的末尾，同时保持非零元素的相对顺序。
 * 请注意 ，必须在不复制数组的情况下原地对数组进行操作。
 *
 * 示例 1:
 * 输入: nums = [0,1,0,3,12]
 * 输出: [1,3,12,0,0]
 *
 * 示例 2:
 * 输入: nums = [0]
 * 输出: [0]
 */
public class moveZeroes {
    public static void main(String[] args) {
        int[] nums = new int[]{0, 1, 0, 3, 12};
        int[] nums2 = new int[]{4, 2, 4, 0, 0, 3, 0, 5, 1, 0};

        moveZeroes(nums);
        moveZeroes(nums2);

        moveZeroes_my(nums);
        moveZeroes_my(nums2);
    }

    public static void moveZeroes(int[] nums) {
        int n = nums.length, left = 0, right = 0;
        while (right < n) {
            if (nums[right] != 0) {
                swap(nums, left, right);
                left++;
            }
            right++;
        }

        System.out.println(Arrays.toString(nums));
    }

    public static void swap(int[] nums, int left, int right) {
        int temp = nums[left];
        nums[left] = nums[right];
        nums[right] = temp;
    }

    public static void moveZeroes_my(int[] nums) {
        if (nums.length <= 1) {
            System.out.println("result=" + Arrays.toString(nums));
        }

        int firstZeroPosition = -1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 0) {
                if (firstZeroPosition == -1) {
                    firstZeroPosition = i;
                }
            } else {
                if (firstZeroPosition != -1) {
                    int temp = nums[firstZeroPosition];
                    nums[firstZeroPosition] = nums[i];
                    nums[i] = temp;

                    for (int j = firstZeroPosition + 1; j <= i; j++) {
                        if (nums[j] == 0) {
                            firstZeroPosition = j;
                            break;
                        }
                    }
                }
            }
        }
        System.out.println("result=" + Arrays.toString(nums));
    }

//    0  1  2  3  4
//       *
//    0, 1, 0, 3, 12  f=0
//    1, 0, 0, 3, 12  f=3
//    0, 1, 0, 3, 12  f=3
//    0, 1, 0, 3, 12  f=3
//    0, 1, 0, 3, 12  f=3



//    0  1  2  3  4  5  6  7  8  9
//                         *
//    4, 2, 4, 0, 0, 3, 0, 5, 1, 0  f=3
//    4, 2, 4, 3, 0, 0, 0, 5, 1, 0  f=4
//    4, 2, 4, 3, 0, 0, 0, 5, 1, 0  f=4
//    4, 2, 4, 3, 5, 0, 0, 0, 1, 0  f=5

//    4, 2, 4, 3, 5, 1, 0, 0, 0, 0  f=6

}
