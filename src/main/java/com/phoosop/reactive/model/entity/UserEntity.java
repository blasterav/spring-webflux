package com.phoosop.reactive.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("user")
@NoArgsConstructor
@Accessors(chain = true)
public class UserEntity {

    @Id
    private Long id;

    private String cardId;

    private String firstName;

    private String secondName;

    private String type;

    private Integer status;

    private Integer level;

    private String dateOfBirth;

    private Integer age;

}
