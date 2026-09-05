package me.ifmo.backend.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import jakarta.transaction.UserTransaction;
import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.jta.JtaTransactionManager;


@Configuration
public class JtaDataSourceConfig {

    @Primary
    @Bean(initMethod = "init", destroyMethod = "close")
    public AtomikosDataSourceBean dataSource(
            @Value("${spring.datasource.xa.properties.user}") String username,
            @Value("${spring.datasource.xa.properties.password}") String password,
            @Value("${spring.datasource.xa.properties.serverName}") String serverName,
            @Value("${spring.datasource.xa.properties.portNumber}") int portNumber,
            @Value("${spring.datasource.xa.properties.databaseName}") String databaseName,
            @Value("${spring.datasource.xa.unique-resource-name}") String uniqueResourceName,
            @Value("${spring.datasource.xa.min-pool-size}") int minPoolSize,
            @Value("${spring.datasource.xa.max-pool-size}") int maxPoolSize,
            @Value("${spring.datasource.xa.borrow-connection-timeout}") int borrowConnectionTimeout,
            @Value("${spring.datasource.xa.max-idle-time}") int maxIdleTime,
            @Value("${spring.datasource.xa.maintenance-interval}") int maintenanceInterval,
            @Value("${spring.datasource.xa.test-query}") String testQuery
    ) {
        PGXADataSource xaDataSource = new PGXADataSource();
        xaDataSource.setUser(username);
        xaDataSource.setPassword(password);
        xaDataSource.setServerNames(new String[]{serverName});
        xaDataSource.setPortNumbers(new int[]{portNumber});
        xaDataSource.setDatabaseName(databaseName);

        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setXaDataSource(xaDataSource);
        dataSource.setUniqueResourceName(uniqueResourceName);
        dataSource.setMinPoolSize(minPoolSize);
        dataSource.setMaxPoolSize(maxPoolSize);
        dataSource.setBorrowConnectionTimeout(borrowConnectionTimeout);
        dataSource.setMaxIdleTime(maxIdleTime);
        dataSource.setMaintenanceInterval(maintenanceInterval);
        dataSource.setTestQuery(testQuery);

        return dataSource;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public UserTransactionManager atomikosTransactionManager() {
        UserTransactionManager manager = new UserTransactionManager();
        manager.setForceShutdown(false);
        return manager;
    }

    @Bean
    public UserTransaction atomikosUserTransaction() throws Exception {
        UserTransactionImp transaction = new UserTransactionImp();
        transaction.setTransactionTimeout(300);
        return transaction;
    }

    @Primary
    @Bean(name = "transactionManager")
    public JtaTransactionManager transactionManager(
            UserTransaction atomikosUserTransaction,
            UserTransactionManager atomikosTransactionManager
    ) {
        return new JtaTransactionManager(atomikosUserTransaction, atomikosTransactionManager);
    }
}