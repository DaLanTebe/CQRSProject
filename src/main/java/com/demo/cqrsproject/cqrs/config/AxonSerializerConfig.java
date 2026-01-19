package com.demo.cqrsproject.cqrs.config;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AxonSerializerConfig {

    @Bean
    @Primary
    public Serializer axonSerializer(ObjectMapper objectMapper) {
        return JacksonSerializer.builder()
                .objectMapper(objectMapper)
                .build();
    }

    @Bean
    @Qualifier("eventSerializer")
    public Serializer eventSerializer(@Qualifier("axonSerializer") Serializer serializer) {
        return serializer;
    }

    @Bean
    @Qualifier("messageSerializer")
    public Serializer messageSerializer(@Qualifier("axonSerializer") Serializer serializer) {
        return serializer;
    }
}
