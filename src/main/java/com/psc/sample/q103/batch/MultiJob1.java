package com.psc.sample.q103.batch;

import com.psc.sample.q103.dto.TwoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class MultiJob1 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private static final int chunkSize = 5;

    @Bean
    public Job multiJob1_batchBuild() {
        return jobBuilderFactory.get("multiJob1")
                .start(multiJob1_batchStep1(null))
                .build();
    }

    // @JobScope를 명시한 Bean은 Application Loading 시점에 싱글톤으로 등록되는게 아닌,
    // 아래 명시한 메서드가 실행될 때까지 지연시킨 후 등록시킨다. 이를 Late Binding이라고도 한다.
    @JobScope
    @Bean
    public Step multiJob1_batchStep1(@Value("#{jobParameters[version]}")String version) { // @Value는 Bean의 생성시점에만 사용할 수 있다.

        log.debug("------");
        log.debug("version = " + version);
        log.debug("------");

        return stepBuilderFactory.get("multiJob1_batchStep1")
                .<TwoDto, TwoDto>chunk(chunkSize)
                .reader(multiJob1_reader(null))
                .processor(multiJob1_processor(null))
                .writer(multiJob1_writer(null))
                .build();

    }

    @StepScope
    @Bean
    public FlatFileItemReader<TwoDto> multiJob1_reader(@Value("#{jobParameters[inFileName]}")String inFileName) {
        return new FlatFileItemReaderBuilder<TwoDto>()
                .name("multiJob1_reader")
                .resource(new ClassPathResource("sample/" + inFileName))
                .delimited().delimiter(":")
                .names("one", "two")
                .targetType(TwoDto.class)
                .recordSeparatorPolicy(new SimpleRecordSeparatorPolicy() {
                    @Override
                    public String postProcess(String record){
                        log.debug("policy : " + record);

                        // 파서 대상이 아니면 무시해줌.
                        if (record.indexOf(":") == -1) {
                            return null;
                        }
                        return record.trim();
                    }
                }).build();
    }

    @StepScope
    @Bean
    public ItemProcessor<TwoDto, TwoDto> multiJob1_processor(@Value("#{jobParameters[versuion]}")String version) {
        log.debug("processor version : " + version);

        return twoDto -> new TwoDto(twoDto.getOne(), twoDto.getTwo());
    }

    @StepScope
    @Bean
    public FlatFileItemWriter<TwoDto> multiJob1_writer(@Value("#{jobParameters[outFileName]}")String outFileName) {
        return new FlatFileItemWriterBuilder<TwoDto>()
                .name("multiJob1_writer")
                .resource(new FileSystemResource("sample/" + outFileName))
                .lineAggregator(item -> {
                    return item.getOne() + " --- " + item.getTwo();
                }).build();
    }

}
