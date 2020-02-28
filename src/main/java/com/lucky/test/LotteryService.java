package com.lucky.test;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LotteryService {
    private RandomService randomService;
    private LotteryConfig lotteryConfig;

    public LotteryService(RandomService randomService, LotteryConfig lotteryConfig) {
        this.randomService = randomService;
        this.lotteryConfig = lotteryConfig;
    }

    public void generateNumbers(List<Person> personList) {
        if (personList == null || personList.size() == 0) {
            return;
        }
        int personSize = personList.size();
        List<Integer> firstNumbers = randomService.getGuestNumbers(1, null);
        if (CollectionUtils.isEmpty(firstNumbers)) {
            System.out.println("firstNumbers is empty");
            System.out.print("guestNos:");
            randomService.guestNos.forEach(n -> System.out.print(n + " "));
            personList.forEach(p -> System.out.println(p));
            return;
        }
        Integer firstNumber = firstNumbers.get(0);
        List<LotteryConfig.Prize> prizes = lotteryConfig.getPrizes();
        for (LotteryConfig.Prize prize : prizes) {
            if (firstNumber >= prize.getStart() && firstNumber < prize.getEnd()) {
                if (personSize == 1) {
                    personList.get(0).setPrize(prize);
                    personList.get(0).setNumber(firstNumber);
                    prize.setInventory(prize.getInventory() - 1);
                    break;
                }
                if (prize.isGroupActivity()) {
                    if (personSize - 1 <= prize.getInventory() - 1) {
//                        randomService.guestNos.remove(firstNumber);
                        List<Integer> groupActivityNumbers = new ArrayList<>();
                        for (int i = prize.getStart(); i < prize.getEnd(); i++) {
                            groupActivityNumbers.add(i);
                        }
                        groupActivityNumbers.remove(firstNumber);
                        List<Integer> leftNumbers = randomService.getGuestNumbers(personSize - 1, groupActivityNumbers);
                        if (CollectionUtils.isEmpty(leftNumbers)) {
                            System.out.println("leftNumbers empty, firstNumber:" + firstNumber);
                            System.out.print("guestNos:");
                            randomService.guestNos.forEach(n -> System.out.print(n + " "));
                            personList.forEach(p -> System.out.println(p));
                            return;
                        }
                        for (Integer number : leftNumbers) {
                            randomService.guestNos.remove(number);
                        }
                        for (int i = 0; i < personSize; i++) {
                            prize.setInventory(prize.getInventory() - 1);
                            Person person = personList.get(i);
                            person.setPrize(prize);
                            if (i == 0) {
                                person.setNumber(firstNumber);
                            } else {
                                person.setNumber(leftNumbers.get(i - 1));
                            }
                        }
                    } else {
                        randomService.guestNos.add(firstNumber);
                        getNoneGroupActivityNumbers(randomService, prizes, personList);
                    }
                } else {
                    if (personSize - 1 <= prize.getInventory() - 1) {
//                        randomService.guestNos.remove(firstNumber);
                        List<Integer> availableGuestNos = getAvailableNumbers(randomService.guestNos, prizes, personSize - 1);
                        availableGuestNos.remove(firstNumber);
                        List<Integer> numbers = randomService.getGuestNumbers(personList.size() - 1, availableGuestNos);
                        if (CollectionUtils.isEmpty(numbers)) {
                            System.out.println("get non GroupAct Numbers empty, firstNumber:" + firstNumber);
                            personList.forEach(p -> System.out.println(p));
                            return;
                        }
                        for (int i = 0; i < personSize; i++) {
                            if (i == 0) {
                                Person person = personList.get(i);
                                setPersonPrizeAndNumber(person, firstNumber, prizes, randomService, false);
                            } else {
                                Integer number = numbers.get(i - 1);
                                Person person = personList.get(i);
                                setPersonPrizeAndNumber(person, number, prizes, randomService, true);
                            }
                        }
                    } else {
                        randomService.guestNos.add(firstNumber);
                        getNoneGroupActivityNumbers(randomService, prizes, personList);
                    }
                }
                break;
            }
        }
    }

    private void getNoneGroupActivityNumbers(RandomService randomService, List<LotteryConfig.Prize> prizes, List<Person> personList) {
        List<Integer> availableGuestNos = getAvailableNumbers(randomService.guestNos, prizes, personList.size());
        if (CollectionUtils.isEmpty(availableGuestNos)) {
            System.out.println("getNoneGroupActivityNumbers empty, guestNos:");
            randomService.guestNos.forEach(no -> System.out.print(no + " "));
            personList.forEach(p -> System.out.println(p));
            return;
        }
        List<Integer> numbers = randomService.getGuestNumbers(personList.size(), availableGuestNos);
        if (CollectionUtils.isEmpty(numbers)) {
            System.out.println("getNoneGroupActivityNumbers empty");
            personList.forEach(p -> System.out.println(p));
            return;
        }
        for (int i = 0; i < numbers.size(); i++) {
            Integer number = numbers.get(i);
            Person person = personList.get(i);
            setPersonPrizeAndNumber(person, number, prizes, randomService, true);
        }
    }

    private List<Integer> getAvailableNumbers(List<Integer> guestNos, List<LotteryConfig.Prize> prizes, int size) {
        List<Integer> availableGuestNos = new ArrayList<>();
        List<LotteryConfig.Prize> noneGroupActivityEnoughPrizes = prizes
                .stream()
                .filter(p -> !p.isGroupActivity() && p.getInventory() >= size)
                .collect(Collectors.toList());
        for (Integer no : guestNos) {
            if (noneGroupActivityEnoughPrizes.stream().anyMatch(p -> no >= p.getStart() && no < p.getEnd())) {
                availableGuestNos.add(no);
            }
        }

        return availableGuestNos;
    }

    private void setPersonPrizeAndNumber(Person person, Integer number, List<LotteryConfig.Prize> prizes, RandomService randomService, boolean doRemove) {
        person.setNumber(number);
        for (LotteryConfig.Prize prize1 : prizes) {
            if (prize1.getStart() <= number && prize1.getEnd() > number) {
                person.setPrize(prize1);
                prize1.setInventory(prize1.getInventory() - 1);
            }
        }
        if (doRemove) {
            randomService.guestNos.remove(number);
        }
    }
}
