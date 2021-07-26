package com.phoosop.reactive.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SpringContextListener implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger LOG = LoggerFactory.getLogger(SpringContextListener.class);

    @Autowired
    private Set<Converter<?, ?>> converters;

    @Autowired
    private ConversionService conversionService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        GenericConversionService genericConversionService = (GenericConversionService) conversionService;
        for(Converter<?, ?> converter : converters) {
            genericConversionService.addConverter(converter);
            LOG.info("CONVERTER REGISTERED: {} ", converter.getClass().getName());
        }
    }
}
