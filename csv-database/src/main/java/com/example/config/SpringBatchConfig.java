package com.example.config;

import com.example.entity.Person;
import com.example.listener.PersonListener;
import com.example.processor.PersonProcessor;
import com.example.repository.PersonRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
//@EnableBatchProcessing
public class SpringBatchConfig {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Value("${input.file.path}")
    private String inputFilePath;

    @Value("${config.lines-skip}")
    private int lineSkip;

    @Value("${config.chunk-size}")
    private int chunkSize;

    @Value("${config.concurrency-limit}")
    private int concurrencyLimit;

    @Value("${config.skip-limit}")
    private int skipLimit;

    private static final String READER_NAME = "csv-reader";

    private static final String WRITER_METHOD_NAME = "save";

    private static final String STEP_1_NAME = "step-1";

    private static final String JOB_NAME = "person-job";


    // Create Reader
    @Bean
    public FlatFileItemReader<Person> personReader() {
        FlatFileItemReader<Person> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource(inputFilePath));
        itemReader.setName(READER_NAME);

        // Skip first row - Header row
        itemReader.setLinesToSkip(lineSkip);

        itemReader.setLineMapper(lineMapper());

        return itemReader;
    }

    private LineMapper<Person> lineMapper() {
        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

        // Set Delimiter - Here source is csv so ','
        lineTokenizer.setDelimiter(DelimitedLineTokenizer.DELIMITER_COMMA);

        // Set Strict to false - If any empty data presented then also it will insert it
        lineTokenizer.setStrict(false);

        // Match the exact column name from the csv
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender");

        BeanWrapperFieldSetMapper<Person> fieldSetMapper = new BeanWrapperFieldSetMapper<>();

        // Set destination type
        fieldSetMapper.setTargetType(Person.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    // Create Processor
    @Bean
    public PersonProcessor personProcessor() {
        return new PersonProcessor();
    }

    // Create Writer
    @Bean
    public RepositoryItemWriter<Person> personWriter() {
        RepositoryItemWriter<Person> writer = new RepositoryItemWriter<>();
        writer.setRepository(personRepository);
        writer.setMethodName(WRITER_METHOD_NAME);
        return writer;
    }

    // Create Task Executor
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(concurrencyLimit);
        return taskExecutor;
    }

    // Create Listener for fault-tolerant
    @Bean
    public PersonListener personListener() {
        return new PersonListener();
    }

    // Create Step
    @Bean
    public Step step1() {
        return new StepBuilder(STEP_1_NAME, jobRepository)
                .<Person, Person>chunk(chunkSize, transactionManager)
                .reader(personReader())
                .processor(personProcessor())
                .writer(personWriter())
                .faultTolerant()
                .skipLimit(skipLimit)
                .skip(NumberFormatException.class)
                .noSkip(IllegalArgumentException.class)
                .listener(personListener())
                .taskExecutor(taskExecutor())
                .build();
    }

    // Create Job
    @Bean
    public Job job() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(step1())
                .build();
    }
}
