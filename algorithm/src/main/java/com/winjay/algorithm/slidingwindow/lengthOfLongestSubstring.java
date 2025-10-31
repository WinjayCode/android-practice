package com.winjay.algorithm.slidingwindow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 无重复字符的最长子串 (3-中等)
 *
 * 示例 1:
 * 输入: s = "abcabcbb"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。注意 "bca" 和 "cab" 也是正确答案。
 *
 * 示例 2:
 * 输入: s = "bbbbb"
 * 输出: 1
 * 解释: 因为无重复字符的最长子串是 "b"，所以其长度为 1。
 *
 * 示例 3:
 * 输入: s = "pwwkew"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "wke"，所以其长度为 3。
 *      请注意，你的答案必须是 子串 的长度，"pwke" 是一个子序列，不是子串。
 */
public class lengthOfLongestSubstring {
    public static void main(String[] args) {
        String str1 = "abcabcbb";
        String str2 = "bbbbb";
        String str3 = "pwwkew";
        String str4 = " ";
        String str5 = "au";
        String str6 = "aab";
        String str7 = "dvdf";

        int length1 = lengthOfLongestSubstring(str1);
        System.out.println("length1=" + length1);

        int length2 = lengthOfLongestSubstring(str2);
        System.out.println("length2=" + length2);

        int length3 = lengthOfLongestSubstring(str3);
        System.out.println("length3=" + length3);

        int length4 = lengthOfLongestSubstring(str4);
        System.out.println("length4=" + length4);

        int length5 = lengthOfLongestSubstring(str5);
        System.out.println("length5=" + length5);

        int length6 = lengthOfLongestSubstring(str6);
        System.out.println("length6=" + length6);

        int length7 = lengthOfLongestSubstring(str7);
        System.out.println("length7=" + length7);
    }

    // 时间复杂度：O(N)，其中 N 是字符串的长度。左指针和右指针分别会遍历整个字符串一次。
    // 空间复杂度：O(∣Σ∣)，其中 Σ 表示字符集（即字符串中可以出现的字符），∣Σ∣ 表示字符集的大小。
    // 在本题中没有明确说明字符集，因此可以默认为所有 ASCII 码在 [0,128) 内的字符，即 ∣Σ∣=128。
    // 我们需要用到哈希集合来存储出现过的字符，而字符最多有 ∣Σ∣ 个，因此空间复杂度为 O(∣Σ∣)。
    public static int lengthOfLongestSubstring(String s) {
        // 哈希集合，记录每个字符是否出现过
        Set<Character> occ = new HashSet<Character>();
        int n = s.length();
        // 右指针，初始值为 -1，相当于我们在字符串的左边界的左侧，还没有开始移动
        int rk = -1, ans = 0;
        for (int i = 0; i < n; ++i) {
            if (i != 0) {
                // 左指针向右移动一格，移除一个字符
                occ.remove(s.charAt(i - 1));
            }
            while (rk + 1 < n && !occ.contains(s.charAt(rk + 1))) {
                // 不断地移动右指针
                occ.add(s.charAt(rk + 1));
                ++rk;
            }
            // 第 i 到 rk 个字符是一个极长的无重复字符子串
            ans = Math.max(ans, rk - i + 1);
        }
        return ans;
    }

    // 时间复杂度：O(n)
    // 空间复杂度：O(n)
    public static int lengthOfLongestSubstring_my(String s) {
        if (s.length() == 1) {
            return 1;
        }

        int maxLength = 0;
        int nextStartPosition = 0;
        List<Character> list = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            if (list.contains(s.charAt(i))) {
                maxLength = Math.max(maxLength, list.size());
                list.clear();
                i = nextStartPosition;
            } else {
                if (list.isEmpty()) {
                    nextStartPosition = i;
                }
                list.add(s.charAt(i));
                maxLength = Math.max(maxLength, list.size());
            }
        }

        return maxLength;
    }
}
