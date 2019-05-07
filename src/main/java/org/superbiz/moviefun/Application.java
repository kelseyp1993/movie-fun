package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

import javax.sql.DataSource;
import java.util.Properties;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager txManager) {
        this.transactionManager = txManager;
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean
    public DatabaseServiceCredentials databaseServiceCredentialsInstance() {
        return new DatabaseServiceCredentials(System.getenv("VCAP_SERVICES"));
    }

    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
        return dataSource;
    }

    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));
        return dataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        vendorAdapter.setDatabase(Database.MYSQL);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        vendorAdapter.setGenerateDdl(true);

        return vendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean albumsEntityManagerFactoryBean(DataSource albumsDataSource, HibernateJpaVendorAdapter vendorAdapter) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(albumsDataSource);
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setPackagesToScan("org.superbiz.moviefun.albums");
        factoryBean.setPersistenceUnitName("album-manager");
        return factoryBean;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean moviesEntityManagerFactoryBean(DataSource moviesDataSource, HibernateJpaVendorAdapter vendorAdapter) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(moviesDataSource);
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setPackagesToScan("org.superbiz.moviefun.movies");
        factoryBean.setPersistenceUnitName("movie-manager");
        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager albumsTransactionManager(LocalContainerEntityManagerFactoryBean  albumsEntityManagerFactoryBean){
        return new PlatformTransactionManager() {
            @Override
            public TransactionStatus getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
                return null;
            }

            @Override
            public void commit(TransactionStatus transactionStatus) throws TransactionException {

            }

            @Override
            public void rollback(TransactionStatus transactionStatus) throws TransactionException {

            }
        }
    }
}
