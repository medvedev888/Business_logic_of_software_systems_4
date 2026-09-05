package me.ifmo.backend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


@Configuration
public class CamundaDataSourceConfig {

    @Bean(name = "camundaBpmDataSource")
    public DataSource camundaBpmDataSource(
            @Value("${spring.datasource.xa.properties.user}") String username,
            @Value("${spring.datasource.xa.properties.password}") String password,
            @Value("${spring.datasource.xa.properties.serverName}") String serverName,
            @Value("${spring.datasource.xa.properties.portNumber}") int portNumber,
            @Value("${spring.datasource.xa.properties.databaseName}") String databaseName,
            @Value("${spring.datasource.xa.properties.currentSchema}") String currentSchema
    ) {
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl("jdbc:postgresql://%s:%d/%s?currentSchema=%s"
                .formatted(serverName, portNumber, databaseName, currentSchema));
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMinimumIdle(1);
        dataSource.setMaximumPoolSize(5);
        dataSource.setPoolName("camunda-pool");

        return dataSource;
    }

    @Bean(name = "camundaBpmTransactionManager")
    public PlatformTransactionManager camundaBpmTransactionManager(
            @Qualifier("camundaBpmDataSource") DataSource dataSource
    ) {
        return new DataSourceTransactionManager(dataSource);
    }
}