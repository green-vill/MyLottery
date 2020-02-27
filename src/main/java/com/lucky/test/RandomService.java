package com.lucky.test;


import java.util.*;

public class RandomService {
    public List<Integer> guestNos = new ArrayList<>();

    public RandomService(int guestCount) {
        for (int i = 0; i < guestCount; i++) {
            guestNos.add(i);
        }
    }

    public List<Integer> getGuestNumbers(int size, List<Integer> numbers) {
        List<Integer> results = getRandomNumbers(size, numbers);
        return results;
    }

//    private List<String> getRandomNumbers(int k) {
//        int n = guestNos.size();
//        List<String> results = new ArrayList<>();
//        for (int i = 0; i < k; i++) {
//            // 取出一个随机数
//            int r = (int) (Math.random() * n);
//            Integer value = guestNos.get(r);
//            results.add(String.valueOf(value));
//            // 排除已经取过的值
//            guestNos.remove(value);
//            n--;
//        }
//        return results;
//    }

    private List<Integer> getRandomNumbers(int k, List<Integer> numbers) {
        List<Integer> data = guestNos;
        if (numbers != null && numbers.size() > 0) {
            data = numbers;
        }
        int n = data.size();
        List<Integer> results = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            // 取出一个随机数
            int r = (int) (Math.random() * n);

            Integer value = data.get(r);
            results.add(value);
            // 排除已经取过的值
//            data.remove(value);
            n--;
        }
        return results;
    }

    public int getNumber(int start, int end) {
        return new Random().ints(start, end).findFirst().getAsInt();
    }
}
