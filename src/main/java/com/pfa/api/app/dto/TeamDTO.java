package com.pfa.api.app.dto;

import java.util.List;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDTO {

    private String name;
    private List<Long> membersIds;
    private Long newResponsible;

}
