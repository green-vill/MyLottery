package com.lucky.test;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class Person {
    private Integer id;
    private String name;
    private String vid;
    private LotteryConfig.Prize prize;
    private Integer number;
}
