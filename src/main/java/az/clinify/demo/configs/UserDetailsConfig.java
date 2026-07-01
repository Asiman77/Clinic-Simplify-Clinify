package az.clinify.demo.configs;

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
                "SELECT fin, password, is_enabled FROM users WHERE fin = ?");

        jdbcDao.setAuthoritiesByUsernameQuery(
                """
                        SELECT u.fin, CONCAT('ROLE_', r.role)
                        FROM users u
                        JOIN user_roles ur ON u.id = ur.user_id
                        JOIN roles r ON ur.role_id = r.id
                        WHERE u.fin = ?
                        """);

        return jdbcDao;
    }
}