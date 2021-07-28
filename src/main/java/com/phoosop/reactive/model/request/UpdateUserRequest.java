package com.phoosop.reactive.model.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.phoosop.reactive.annotations.IsDate;
import com.phoosop.reactive.annotations.IsEnum;
import com.phoosop.reactive.annotations.Max;
import com.phoosop.reactive.annotations.Min;
import com.phoosop.reactive.exception.StatusConstants;
import com.phoosop.reactive.model.enums.UserStatus;
import com.phoosop.reactive.model.enums.UserType;
import com.phoosop.reactive.util.DateTimeUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateUserRequest {

    private String cardId;

    private String firstName;

    private String secondName;

    @IsEnum(enumClass = UserType.class, exception = StatusConstants.HttpConstants.TYPE_IS_INVALID)
    private String type;

    @IsEnum(enumClass = UserStatus.class, exception = StatusConstants.HttpConstants.STATUS_IS_INVALID)
    private Integer status;

    @IsDate(format = DateTimeUtils.DATE_FORMAT_YYYY_MM_DD, exception = StatusConstants.HttpConstants.DATE_OF_BIRTH_IS_INVALID)
    private String dateOfBirth;

    @Min(value = 18, exception = StatusConstants.HttpConstants.AGE_IS_INVALID)
    @Max(value = 54, exception = StatusConstants.HttpConstants.AGE_IS_INVALID)
    private Integer age;

    private String mobileNumber;

    private String mobileBrand;

}
