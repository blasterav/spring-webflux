package com.phoosop.reactive.converter;

import com.phoosop.reactive.model.command.ActivityCommand;
import com.phoosop.reactive.model.response.ActivityResponse;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;


@Mapper(componentModel = "spring")
public abstract class ActivityCommandToActivityResponseConverter implements Converter<ActivityCommand, ActivityResponse> {

    public abstract ActivityResponse convert(ActivityCommand source);

}
