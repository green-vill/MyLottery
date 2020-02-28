package com.lucky.test;

import java.util.*;
import java.util.stream.Collectors;

public class LuckDrawMain {

    public static void main(String[] args) {
//        List<Person> personList1 = getPersonGroup(0, 0);
//        List<Person> personList2 = getPersonGroup(1, 2);
//        List<Person> personList3 = getPersonGroup(3, 5);
//        List<Person> personList4 = getPersonGroup(6, 9);
//        List<Person> personList5 = getPersonGroup(10, 14);
//
//        LotteryConfig lotteryConfig = new LotteryConfig();
//        RandomService randomService = new RandomService(lotteryConfig.getGuestForeCast());
//
//        LotteryService lotteryService = new LotteryService(randomService, lotteryConfig);
//
//        lotteryService.generateNumbers(personList1);
//        lotteryService.generateNumbers(personList2);
//        lotteryService.generateNumbers(personList3);
//        lotteryService.generateNumbers(personList4);
//        lotteryService.generateNumbers(personList5);
//
//        List<Person> results = new ArrayList<>();
//        results.addAll(personList1);
//        results.addAll(personList2);
//        results.addAll(personList3);
//        results.addAll(personList4);
//        results.addAll(personList5);
//        System.out.println("中奖结果如下：");
//        for (Person person : results) {
//            System.out.println(person);
//        }

        LotteryConfig lotteryConfig = new LotteryConfig();
        int guestForeCast = lotteryConfig.getGuestForeCast();
        int size = 1;
        List<Person> results = new ArrayList<>();
        RandomService randomService = new RandomService(lotteryConfig.getGuestForeCast());
        LotteryService lotteryService = new LotteryService(randomService, lotteryConfig);
        for (int i = 0; i < guestForeCast  ; i = i + size) {
            size = new Random().ints(1, 6).findFirst().getAsInt();
            if (i + size >= guestForeCast) {
                break;
            }
            System.out.println("size is ==" + size + ",i=" + i);
            List<Person> personList = getPersonGroup2(size, guestForeCast);
            lotteryService.generateNumbers(personList);
            results.addAll(personList);
            System.out.println("personList size:" + personList.size());
        }
        Set<Integer> resultNumbers = results.stream().map(r -> r.getNumber()).collect(Collectors.toSet());
        System.out.println("resultNumbers size:" + resultNumbers.size());
        resultNumbers.forEach(rn -> System.out.print(rn + " "));
        System.out.println("results size:" + results.size());
        results.stream().forEach(person -> System.out.println(person));
    }

    private static List<Person> getPersonGroup2(int size, int guestForeCast) {
        List<Person> personList = new ArrayList<>();
        List<Integer> integers = new Random().ints(size * 5, 0, guestForeCast)
                .mapToObj(number -> Integer.valueOf(number))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        for (int i = 0; i < size; i++) {
            Person person = new Person();
            int randomId = integers.get(i);
            person.setId(randomId);
            person.setName("name" + randomId);
            person.setVid("vid-" + randomId);
            personList.add(person);
        }
        return personList;
    }

    private static List<Person> getPersonGroup(int start, int end) {
        List<Person> personList = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            Person person = new Person();
            person.setId(i);
            person.setName("name" + i);
            person.setVid("vid-" + i);
            personList.add(person);
        }
        return personList;
    }


}
