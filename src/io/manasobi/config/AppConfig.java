package io.manasobi.config;

import com.zaxxer.hikari.HikariDataSource;
import io.manasobi.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by twjang on 15. 9. 9.
 */
@Configuration
public class AppConfig {

    @Autowired
    Environment env;


    @Bean
    public DataSource dataSource() {

        Properties props = yamlProps().getObject();

        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setDriverClassName(props.getProperty("datasource.driver-class-name"));
        dataSource.setJdbcUrl(props.getProperty("datasource.jdbc-url"));
        dataSource.setUsername(props.getProperty("datasource.username"));
        dataSource.setPassword(props.getProperty("datasource.password"));
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {

        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource());

        return jdbcTemplate;
    }

    @Bean
    public YamlPropertiesFactoryBean yamlProps() {

        String configFilePath;

        String userDir = System.getProperty("user.dir");

        String configFile = "/build/classes/application.yml";

        if (FileUtils.existsFile(userDir + configFile)) {
            configFilePath = userDir + configFile;
        } else {
            configFilePath = userDir + "/anypoint-kafka-producer.conf";
        }

        Resource resource = new FileSystemResource(configFilePath);

        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(resource);

        return factoryBean;
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {

        PropertySource propertySource = new PropertiesPropertySource("yamlProps", yamlProps().getObject());

        MutablePropertySources mutablePropertySources = new MutablePropertySources();
        mutablePropertySources.addFirst(propertySource);

        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setPropertySources(mutablePropertySources);

        return configurer;
    }
}
