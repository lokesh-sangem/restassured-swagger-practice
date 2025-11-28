package com.tac.apitesting.models;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {
    private String toAccount;
    private String fromAccount;
    private String transferAmount;
}
