package com.phoosop.reactive.converter;

import com.phoosop.reactive.model.command.ActivityCommand;
import com.phoosop.reactive.model.external.ActivityExternal;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;


@Mapper(componentModel = "spring")
public abstract class ActivityExternalToActivityCommandConverter implements Converter<ActivityExternal, ActivityCommand> {

    public abstract ActivityCommand convert(ActivityExternal source);

}
