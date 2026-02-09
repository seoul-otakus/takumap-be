package com.seoulotakus.takumapbe.domain.user.dto.response;

import com.seoulotakus.takumapbe.domain.user.dto.request.FindIdRequestDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class FindIdResponseDTO {

    private String userId;

    public FindIdResponseDTO(){}

    public FindIdResponseDTO(String userId){
        this.userId = userId;
    }
}
