package cz.cvut.fel.pro.etmt.config;

import cz.cvut.fel.pro.etmt.model.library.*;
import cz.cvut.fel.pro.etmt.payload.library.*;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        modelMapper.typeMap(QuestionPayload.class, Item.class).setConverter(converterWithDestinationSupplier(Question::new));
        modelMapper.typeMap(CategoryPayload.class, Item.class).setConverter(converterWithDestinationSupplier(Category::new));
        modelMapper.typeMap(TestTemplatePayload.class, Item.class).setConverter(converterWithDestinationSupplier(TestTemplate::new));
        modelMapper.typeMap(TestVariantPayload.class, Item.class).setConverter(converterWithDestinationSupplier(TestVariant::new));

        modelMapper.typeMap(Question.class, ItemPayload.class).setConverter(converterWithDestinationSupplier(QuestionPayload::new));
        modelMapper.typeMap(Category.class, ItemPayload.class).setConverter(converterWithDestinationSupplier(CategoryPayload::new));
        modelMapper.typeMap(TestTemplate.class, ItemPayload.class).setConverter(converterWithDestinationSupplier(TestTemplatePayload::new));
        modelMapper.typeMap(TestVariant.class, ItemPayload.class).setConverter(converterWithDestinationSupplier(TestVariantPayload::new));

        return modelMapper;
    }

    private <S, D> Converter<S, D> converterWithDestinationSupplier(Supplier<? extends D> supplier ) {
        return ctx -> ctx.getMappingEngine().map(ctx.create(ctx.getSource(), supplier.get()));
    }

}
