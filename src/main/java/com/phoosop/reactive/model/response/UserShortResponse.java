package com.phoosop.reactive.model.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserShortResponse {

    private Long id;

    private String cardId;

    private String firstName;

    private String secondName;

    private String type;

    private Integer status;

}
