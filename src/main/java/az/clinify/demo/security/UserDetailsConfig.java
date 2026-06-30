package az.clinify.demo.security;


import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@Configuration
public class UserDetailsConfig {

    private final DataSource dataSource;

    public UserDetailsConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        JdbcUserDetailsManager jdbcDao = new JdbcUserDetailsManager(dataSource);

        jdbcDao.setUsersByUsernameQuery(
                "SELECT fin, password, has_account FROM users WHERE fin = ?");

        jdbcDao.setAuthoritiesByUsernameQuery(
                "SELECT fin, CONCAT('ROLE_', role) FROM users WHERE fin = ?");

        return jdbcDao;
    }
}