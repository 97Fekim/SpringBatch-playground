package com.psc.sample.q103.batch;

import com.psc.sample.q103.dto.CoinMarket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class JsonJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private static final int chunkSize = 5;

    @Bean
    public Job jsonJob1_BatchBuild() {
        return jobBuilderFactory.get("jsonJob1")
                .start(jsonJob1_batchStep1())
                .build();
    }

    @Bean
    public Step jsonJob1_batchStep1() {
        return stepBuilderFactory.get("jsonJob1_batchStep1")
                .<CoinMarket, CoinMarket>chunk(chunkSize)
                .reader(jsonJob_jsonReader())
                .writer(coinMarket -> coinMarket.stream().forEach(coinMarket1 -> {
                    log.debug(coinMarket1.toString());
                }))
                .build();
    }

    @Bean
    public JsonItemReader<CoinMarket> jsonJob_jsonReader() {
        return new JsonItemReaderBuilder<CoinMarket>()
                .name("jsonJob1_jsonReader")
                .jsonObjectReader(new JacksonJsonObjectReader<>(CoinMarket.class))
                .resource(new ClassPathResource("sample/jsonJob1_input.json"))
                .build();
    }


}
