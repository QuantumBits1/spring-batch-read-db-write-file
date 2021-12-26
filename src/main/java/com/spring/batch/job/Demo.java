package com.spring.batch.job;

import com.spring.batch.dto.EmployeeDTO;
import com.spring.batch.mapper.EmployeeDBRowMapper;
import com.spring.batch.model.Employee;
import com.spring.batch.processor.EmployeeProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

@Configuration
public class Demo {

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private EmployeeProcessor employeeProcessor;
    private DataSource dataSource;
    private Resource outputFileResource = new FileSystemResource("output/employee_output.csv");

    @Autowired
    public Demo(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                EmployeeProcessor employeeProcessor, DataSource dataSource){
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.employeeProcessor = employeeProcessor;
        this.dataSource = dataSource;
    }

    @Qualifier(value = "demo")
    @Bean
    public Job demoJob() throws Exception {
        return this.jobBuilderFactory.get("demo")
                .start(stepDemo())
                .build();
    }

    @Bean
    public Step stepDemo() throws Exception {
        return this.stepBuilderFactory.get("step")
                .<Employee, EmployeeDTO>chunk(10)
                .reader(employeeDBReader())
                .writer(employeeFileWriter())
                .build();
    }

    @Bean
    public ItemStreamReader<Employee> employeeDBReader() {
        JdbcCursorItemReader<Employee> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("select * from employee");
        reader.setRowMapper(new EmployeeDBRowMapper());
        return reader;
    }

    @Bean
    public ItemWriter<EmployeeDTO> employeeFileWriter() throws Exception {
        FlatFileItemWriter<EmployeeDTO> writer = new FlatFileItemWriter<>();
        writer.setResource(outputFileResource);
        writer.setLineAggregator(new DelimitedLineAggregator<EmployeeDTO>() {
            {
                setFieldExtractor(new BeanWrapperFieldExtractor<EmployeeDTO>() {
                    {
                        setNames(new String[]{"employeeId", "firstName", "lastName", "email", "age"});
                    }
                });
            }
        });
        writer.setShouldDeleteIfExists(true);
        return writer;
    }

}
