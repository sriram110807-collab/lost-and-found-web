package com.bufinder.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Automatically inspects DataSource configuration for Railway environments.
 * If Railway's MYSQL_URL or DATABASE_URL (in mysql:// format) is supplied,
 * this post-processor translates it into a standard JDBC URL (jdbc:mysql://)
 * and extracts the credentials seamlessly before connection pools initialize.
 */
@Component
public class RailwayDataSourcePostProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(RailwayDataSourcePostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSourceProperties properties) {
            configureFromRailway(properties);
        }
        return bean;
    }

    private void configureFromRailway(DataSourceProperties properties) {
        String rawUrl = System.getenv("MYSQL_URL");
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = System.getenv("DATABASE_URL");
        }
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = properties.getUrl();
        }

        if (rawUrl != null && rawUrl.startsWith("mysql://")) {
            log.info("Detected Railway mysql:// URI format. Converting to standard jdbc:mysql:// format...");
            try {
                URI uri = new URI(rawUrl);
                String userInfo = uri.getUserInfo();
                String host = uri.getHost();
                int port = uri.getPort() != -1 ? uri.getPort() : 3306;
                String path = uri.getPath(); // includes leading '/'
                String query = uri.getQuery();

                if (userInfo != null && userInfo.contains(":")) {
                    String[] userPass = userInfo.split(":", 2);
                    properties.setUsername(userPass[0]);
                    properties.setPassword(userPass[1]);
                }

                String jdbcUrl = "jdbc:mysql://" + host + ":" + port + path +
                        "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true" +
                        (query != null && !query.isEmpty() ? "&" + query : "");

                properties.setUrl(jdbcUrl);
                properties.setDriverClassName("com.mysql.cj.jdbc.Driver");
                log.info("Successfully configured JDBC connection for host: {}:{}", host, port);
            } catch (Exception e) {
                log.warn("Could not parse mysql:// URI as URI, falling back to prefixing with jdbc:: {}", e.getMessage());
                properties.setUrl("jdbc:" + rawUrl);
            }
        }
    }
}
