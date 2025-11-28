package com.tac.apitesting.models;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewUser {
    private String firstname;
    private String lastname;
    private String username;
    private String password1;
    private String password2;
}
