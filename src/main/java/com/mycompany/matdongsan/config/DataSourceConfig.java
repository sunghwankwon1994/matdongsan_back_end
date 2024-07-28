package com.mycompany.matdongsan.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {
   @Bean
   public DataSource dataSource() {
      HikariConfig config = new HikariConfig();      
      config.setDriverClassName("oracle.jdbc.OracleDriver");
      config.setJdbcUrl("jdbc:oracle:thin:@125.131.208.226:1521:orcl");      
      //config.setDriverClassName("net.sf.log4jdbc.DriverSpy");
      //config.setJdbcUrl("jdbc:log4jdbc:oracle:thin:@kosa164.iptime.org:1521:orcl");      
      config.setUsername("user_final_team2");
      config.setPassword("oracle");
      config.setMaximumPoolSize(3);
      HikariDataSource hikariDataSource = new HikariDataSource(config);
      return hikariDataSource;
   }
}
