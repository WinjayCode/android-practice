package com.winjay.algorithm.slidingwindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 找到字符串中所有字母异位词 (438-中等)
 *
 * 给定两个字符串 s 和 p，找到 s 中所有 p 的 异位词 的子串，返回这些子串的起始索引。不考虑答案输出的顺序。
 *
 * 示例 1:
 * 输入: s = "cbaebabacd", p = "abc"
 * 输出: [0,6]
 * 解释:
 * 起始索引等于 0 的子串是 "cba", 它是 "abc" 的异位词。
 * 起始索引等于 6 的子串是 "bac", 它是 "abc" 的异位词。
 *
 *  示例 2:
 * 输入: s = "abab", p = "ab"
 * 输出: [0,1,2]
 * 解释:
 * 起始索引等于 0 的子串是 "ab", 它是 "ab" 的异位词。
 * 起始索引等于 1 的子串是 "ba", 它是 "ab" 的异位词。
 * 起始索引等于 2 的子串是 "ab", 它是 "ab" 的异位词。
 */
public class findAnagrams {
    public static void main(String[] args) {
        String longStr1 = "cbaebabacd";
        String shortStr1 = "abc";

        String longStr2 = "abab";
        String shortStr2 = "ab";

        List<Integer> list1 = findAnagrams_my(longStr1, shortStr1);
        System.out.println("list1=" + list1);

        List<Integer> list2 = findAnagrams_my(longStr2, shortStr2);
        System.out.println("list2=" + list2);
    }

    // cbaebabacd   abc   [0,6]
    // abab   ab  [0,1,2]
    public static List<Integer> findAnagrams_my(String s, String p) {
        List<Integer> result = new ArrayList<>();
        int n = s.length() - p.length() + 1;
        char[] pCharArray = p.toCharArray();
        Arrays.sort(pCharArray);
        String sortP = Arrays.toString(pCharArray);
        for (int i = 0; i < n; i++) {
            if (p.contains(String.valueOf(s.charAt(i)))) {
                String substring = s.substring(i, i + p.length());
                char[] subCharArray = substring.toCharArray();
                Arrays.sort(subCharArray);
                if (Arrays.toString(subCharArray).equals(sortP)) {
                    System.out.println("index=" + i);
                    result.add(i);
                }
            }
        }
        return result;
    }
}
