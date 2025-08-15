package com.winjay.algorithm.hash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字母异位词(相同字母异序词)分组 (49-中等)
 *
 * 给你一个字符串数组，请你将 字母异位词 组合在一起。可以按任意顺序返回结果列表。
 *
 * 示例 1:
 * 输入: strs = ["eat", "tea", "tan", "ate", "nat", "bat"]
 * 输出: [["bat"],["nat","tan"],["ate","eat","tea"]]
 * 解释：
 * 在 strs 中没有字符串可以通过重新排列来形成 "bat"。
 * 字符串 "nat" 和 "tan" 是字母异位词，因为它们可以重新排列以形成彼此。
 * 字符串 "ate" ，"eat" 和 "tea" 是字母异位词，因为它们可以重新排列以形成彼此。
 */
public class groupAnagrams {
    public static void main(String[] args) {
        String[] input = new String[] {"eat", "tea", "tan", "ate", "nat", "bat"};
        String[] input2 = new String[] {"", ""};
        List<List<String>> output = groupAnagrams(input);
        System.out.println("output=" + output);
    }

    // 排序法
    public static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String str : strs) {
            char[] charArray = str.toCharArray();
            Arrays.sort(charArray);
            String key = Arrays.toString(charArray);
            List<String> list = map.getOrDefault(key, new ArrayList<String>());
            list.add(str);
            map.put(Arrays.toString(charArray), list);
        }
        return new ArrayList<List<String>>(map.values());
    }

    // 暴力枚举法
    public static List<List<String>> groupAnagrams_my(String[] strs) {
        HashMap<List<String>, String> stringListHashMap = new HashMap<>();
        for (String str : strs) {
            List<String> letterList = new ArrayList<>();
            int length = str.length();
            for (int i = 0; i < length; i++) {
                letterList.add(String.valueOf(str.charAt(i)));
            }
            stringListHashMap.put(letterList, str);
        }

        // <<e, a ,t>, eat>  <<t, e, a>, tea>  <<t, a, n>, tan>  <<a, t, e>, ate>  <<n, a, t>, nat>  <<b, a, t>, bat>
        System.out.println("stringListHashMap=" + stringListHashMap);

        ArrayList<List<String>> resultList = new ArrayList<>();

        for (List<String> list : stringListHashMap.keySet()) {
            List<String> sameWord = new ArrayList<>();
            for (List<String> listInner : stringListHashMap.keySet()) {
                if (list.size() == listInner.size()) {
                    boolean isSame = true;
                    for (int i = 0; i < list.size(); i++) {
                        if (!list.contains(listInner.get(i))) {
                            isSame = false;
                        }
                    }
                    if (isSame) {
                        if (!sameWord.contains(stringListHashMap.get(list))) {
                            sameWord.add(stringListHashMap.get(list));
                        }
                        if (!sameWord.contains(stringListHashMap.get(listInner))) {
                            sameWord.add(stringListHashMap.get(listInner));
                        }
                    }
                }
            }

            System.out.println("sameWord=" + sameWord);

            if (resultList.isEmpty()) {
                resultList.add(sameWord);
            } else {
                boolean isExist = false;
                for (List<String> strings : resultList) {
                    for (String s : sameWord) {
                        if (strings.contains(s)) {
                            isExist = true;
                        }
                    }
                }
                if (!isExist) {
                    resultList.add(sameWord);
                }
            }
            // [[ate, eat, tea], [tan, nat], [nat, tan], [bat], [eat, ate, tea], [tea, ate, eat]]
        }

        return resultList;
    }
}
