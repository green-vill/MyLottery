package com.lucky.test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LuckDrawMain {

    public static void main(String[] args) {
        List<Person> personList1 = getPersonGroup(0, 0);
        List<Person> personList2 = getPersonGroup(1, 2);
        List<Person> personList3 = getPersonGroup(3, 5);
        List<Person> personList4 = getPersonGroup(6, 9);
        List<Person> personList5 = getPersonGroup(10, 14);

        LotteryConfig lotteryConfig = new LotteryConfig();
        RandomService randomService = new RandomService(lotteryConfig.getGuestForeCast());

        LotteryService lotteryService = new LotteryService(randomService, lotteryConfig);

        lotteryService.generateNumbers(personList1);
        lotteryService.generateNumbers(personList2);
        lotteryService.generateNumbers(personList3);
        lotteryService.generateNumbers(personList4);
        lotteryService.generateNumbers(personList5);

        List<Person> results = new ArrayList<>();
        results.addAll(personList1);
        results.addAll(personList2);
        results.addAll(personList3);
        results.addAll(personList4);
        results.addAll(personList5);
        System.out.println("中奖结果如下：");
        for (Person person : results) {
            System.out.println(person);
        }
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
