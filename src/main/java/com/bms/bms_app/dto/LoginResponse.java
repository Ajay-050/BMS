package com.bms.bms_app.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class LoginResponse {

    private Long id;
    private String name;
    private String email;

}
