package com.psc.sample.q103.batch;

import com.psc.sample.q103.dto.CoinMarket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class JsonJob2 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private static final int chunkSize = 5;

    @Bean
    public Job jsonJob2_BatchBuild() {
        return jobBuilderFactory.get("jsonJob2")
                .start(jsonJob2_batchStep1())
                .build();
    }

    @Bean
    public Step jsonJob2_batchStep1() {
        return stepBuilderFactory.get("jsonJob2_batchStep1")
                .<CoinMarket, CoinMarket>chunk(chunkSize)
                .reader(jsonJob2_jsonReader())
                .processor(jsonJob2_processor())
                .writer(jsonJob2_jsonWriter())
                .build();
    }

    private ItemProcessor<CoinMarket, CoinMarket> jsonJob2_processor() {

        return coinMarket -> {
            if(coinMarket.getMarket().startsWith("KRW-")) {
                return new CoinMarket(coinMarket.getMarket(), coinMarket.getKorean_name(), coinMarket.getEnglish_name());
            } else {
                return null;
            }
        };
    }


    @Bean
    public JsonItemReader<CoinMarket> jsonJob2_jsonReader() {
        return new JsonItemReaderBuilder<CoinMarket>()
                .name("jsonJob2_jsonReader")
                .jsonObjectReader(new JacksonJsonObjectReader<>(CoinMarket.class))
                .resource(new ClassPathResource("sample/jsonJob2_input.json"))
                .build();
    }

    @Bean
    public JsonFileItemWriter<CoinMarket> jsonJob2_jsonWriter() {
        return new JsonFileItemWriterBuilder<CoinMarket>()
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(new FileSystemResource("output/jsonJob2_output.json"))
                .name("jsonJob2_jsonWriter")
                .build();
    }
}
