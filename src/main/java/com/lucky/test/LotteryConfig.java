package com.lucky.test;

import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LotteryConfig {
    private int guestForeCast;
    private int inventory;
    private List<Prize> prizes;

    private static int startIndex;

    @Data
    public static class Prize {
        private String id;
        private double ratio;
        private String name;
        private boolean groupActivity;
        private boolean inventoryControl;

        private int start;
        private int end;
        private int inventory = -1;

    }

    public LotteryConfig() {
        guestForeCast = 100;
        inventory = 10;
        setupPrize();
    }

    private void setupPrize() {
        prizes = new ArrayList<>();
        Prize bigPrize = new Prize();
        bigPrize.id = "1";
        bigPrize.ratio = 0.1;
        bigPrize.name = "first big prize";
        bigPrize.groupActivity = false;
        bigPrize.inventoryControl = true;
        prizes.add(bigPrize);
        Prize fastPass = new Prize();
        fastPass.id = "21";
        fastPass.ratio = 0.2;
        fastPass.name = "fastpass";
        fastPass.groupActivity = true;
        fastPass.inventoryControl = true;
        prizes.add(fastPass);
        Prize voucher = new Prize();
        voucher.id = "22";
        voucher.ratio = 0.2;
        voucher.name = "voucher";
        voucher.groupActivity = false;
        voucher.inventoryControl = true;
        prizes.add(voucher);
        Prize wallPaper = new Prize();
        wallPaper.id = "31";
        wallPaper.ratio = 0.95;
        wallPaper.name = "wall paper";
        wallPaper.groupActivity = false;
        wallPaper.inventoryControl = false;
        prizes.add(wallPaper);
        prizes = prizes.stream().sorted(Comparator.comparingInt(prize -> Integer.parseInt(prize.id))).collect(Collectors.toList());
        prizes.stream().peek((prize -> {
            prize.start = startIndex;
            double numbers = inventory * prize.ratio;
            prize.end = prize.inventoryControl ? startIndex + (int) numbers : guestForeCast;
            startIndex += numbers;
            prize.inventory = prize.end - prize.start;
        })).collect(Collectors.toList());
    }
}
