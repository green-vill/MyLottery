package com.lucky.test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LuckDrawMain {

    public static void main(String[] args) {
        List<Person> personList = new ArrayList<>();
        Person person1 = new Person();
        person1.setId(1);
        person1.setName("person1");
        person1.setVid("v001");

        Person person2 = new Person();
        person2.setId(2);
        person2.setName("person2");
        person2.setVid("v002");

        Person person3 = new Person();
        person3.setId(3);
        person3.setName("person3");
        person3.setVid("v003");

        personList.add(person1);
        personList.add(person2);
        personList.add(person3);

        generateNumbers(personList);
    }

    public static void generateNumbers(List<Person> personList) {
        long startTime = System.currentTimeMillis();
        LotteryConfig lotteryConfig = new LotteryConfig();
        int guestForeCast = lotteryConfig.getGuestForeCast();
        RandomService randomService = new RandomService(guestForeCast);
        if (personList == null || personList.size() == 0) {
            return;
        }
        int personSize = personList.size();
        List<Integer> firstNumbers = randomService.getGuestNumbers(1, null);
        Integer firstNumber = firstNumbers.get(0);
        List<LotteryConfig.Prize> prizes = lotteryConfig.getPrizes();
        for (LotteryConfig.Prize prize : prizes) {
            if (firstNumber >= prize.getStart() && firstNumber < prize.getEnd()) {
                if (prize.isGroupActivity()) {
                    if (personSize <= prize.getInventory()) {
                        randomService.guestNos.remove(firstNumber);
                        List<Integer> leftNumbers = randomService.getGuestNumbers(personSize - 1, null);
                        for (Integer number : leftNumbers) {
                            randomService.guestNos.remove(number);
                        }
                        for (int i = 0; i < personSize; i++) {
                            personList.get(i).setPrize(prize);
                            if (i == 0) {
                                personList.get(i).setNumber(firstNumber);
                            } else {
                                personList.get(i).setNumber(leftNumbers.get(i - 1));
                            }
                        }
                    } else {
                        getNoneGroupActivityNumbers(randomService, prizes, personList);
                    }
                } else {
                    List<Integer> availableGuestNos = getAvailableNumbers(randomService.guestNos, prizes);
                    List<Integer> numbers = randomService.getGuestNumbers(personList.size() - 1, availableGuestNos);
                    for (int i = 0; i < personSize; i++) {
                        if (i == 0) {
                            Person person = personList.get(i);
                            setPersonPrizeAndNumber(person, firstNumber, prizes, randomService);
                        } else {
                            Integer number = numbers.get(i - 1);
                            Person person = personList.get(i);
                            setPersonPrizeAndNumber(person, number, prizes, randomService);
                        }
                    }
                }
                break;
            }
        }
        System.out.println("take time total: " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private static void getNoneGroupActivityNumbers(RandomService randomService, List<LotteryConfig.Prize> prizes, List<Person> personList) {
        List<Integer> availableGuestNos = getAvailableNumbers(randomService.guestNos, prizes);
        List<Integer> numbers = randomService.getGuestNumbers(personList.size(), availableGuestNos);
        for (int i = 0; i < numbers.size(); i++) {
            Integer number = numbers.get(i);
            Person person = personList.get(i);
            setPersonPrizeAndNumber(person, number, prizes, randomService);
        }
    }

    private static List<Integer> getAvailableNumbers(List<Integer> guestNos, List<LotteryConfig.Prize> prizes) {
        List<Integer> availableGuestNos = new ArrayList<>();
        List<LotteryConfig.Prize> groupActivityPrizes = prizes.stream().filter(p -> p.isGroupActivity()).collect(Collectors.toList());
        for (Integer no : guestNos) {
            if (groupActivityPrizes.stream().noneMatch(p -> no >= p.getStart() && no < p.getEnd())) {
                availableGuestNos.add(no);
            }
        }
        return availableGuestNos;
    }

    private static void setPersonPrizeAndNumber(Person person, Integer number, List<LotteryConfig.Prize> prizes, RandomService randomService) {
        person.setNumber(number);
        for (LotteryConfig.Prize prize1 : prizes) {
            if (prize1.getStart() <= number && prize1.getEnd() > number) {
                person.setPrize(prize1);
                prize1.setInventory(prize1.getInventory() - 1);
            }
        }
        randomService.guestNos.remove(number);
    }
}
