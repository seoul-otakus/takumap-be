package com.seoulotakus.takumapbe.domain.user.dto.request;

import lombok.Getter;

@Getter
public class FindIdRequestDTO {

    private String email;

    public FindIdRequestDTO(){}

    public FindIdRequestDTO(String email){
        this.email = email;
    }
}
