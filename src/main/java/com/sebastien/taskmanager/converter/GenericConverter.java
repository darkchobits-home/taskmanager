package com.sebastien.taskmanager.converter;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class GenericConverter<S, T> implements Converter<S, T> {

    protected final ModelMapper modelMapper;

    public GenericConverter() {
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public T convert(S source, Class<T> resultClass) {
        return modelMapper.map(source, resultClass);
    }

    @Override
    public T convert(MappingContext<S, T> mappingContext) {
        return modelMapper.map(mappingContext.getSource(), mappingContext.getDestinationType());
    }
}
