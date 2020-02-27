package com.lucky.test;

import lombok.Data;

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
        long startTime = System.currentTimeMillis();
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
                    if (personSize - 1 <= prize.getInventory()) {
                        randomService.guestNos.remove(firstNumber);
                        List<Integer> groupActivityNumbers = new ArrayList<>();
                        for (int i = prize.getStart(); i < prize.getEnd(); i++) {
                            groupActivityNumbers.add(i);
                        }
                        groupActivityNumbers.remove(firstNumber);
                        List<Integer> leftNumbers = randomService.getGuestNumbers(personSize - 1, groupActivityNumbers);
                        for (Integer number : leftNumbers) {
                            randomService.guestNos.remove(number);
                        }
                        for (int i = 0; i < personSize; i++) {
                            Person person = personList.get(i);
                            person.setPrize(prize);
                            if (i == 0) {
                                person.setNumber(firstNumber);
                            } else {
                                person.setNumber(leftNumbers.get(i - 1));
                            }
                        }
                    } else {
                        getNoneGroupActivityNumbers(randomService, prizes, personList);
                    }
                } else {
                    if (personSize - 1 <= prize.getInventory()) {
                        randomService.guestNos.remove(firstNumber);
                        List<Integer> availableGuestNos = getAvailableNumbers(randomService.guestNos, prizes, personSize - 1);
                        availableGuestNos.remove(firstNumber);
                        List<Integer> numbers = randomService.getGuestNumbers(personList.size() - 1, availableGuestNos);
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
                        getNoneGroupActivityNumbers(randomService, prizes, personList);
                    }
                }
                break;
            }
        }
        System.out.println("take time total: " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private void getNoneGroupActivityNumbers(RandomService randomService, List<LotteryConfig.Prize> prizes, List<Person> personList) {
        List<Integer> availableGuestNos = getAvailableNumbers(randomService.guestNos, prizes, personList.size());
        List<Integer> numbers = randomService.getGuestNumbers(personList.size(), availableGuestNos);
        for (int i = 0; i < numbers.size(); i++) {
            Integer number = numbers.get(i);
            Person person = personList.get(i);
            setPersonPrizeAndNumber(person, number, prizes, randomService, true);
        }
    }

    private List<Integer> getAvailableNumbers(List<Integer> guestNos, List<LotteryConfig.Prize> prizes, int size) {
        List<Integer> availableGuestNos = new ArrayList<>();
        List<LotteryConfig.Prize> groupActivityPrizes = prizes.stream().filter(p -> p.isGroupActivity()).collect(Collectors.toList());
        for (Integer no : guestNos) {
            if (groupActivityPrizes.stream().noneMatch(p -> no >= p.getStart() && no < p.getEnd())) {
                availableGuestNos.add(no);
            }
        }
        List<LotteryConfig.Prize> noneGroupActivityNotEnoughPrizes = prizes
                .stream()
                .filter(p -> !p.isGroupActivity() && p.getInventory() < size)
                .collect(Collectors.toList());
        for (int i = availableGuestNos.size() - 1; i >= 0; i--) {
            Integer number = availableGuestNos.get(i);
            if (noneGroupActivityNotEnoughPrizes.stream().anyMatch(p -> p.getStart() <= number && p.getEnd() > number)) {
                availableGuestNos.remove(number);
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
