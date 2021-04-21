package at.timeguess.backend.configs;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import at.timeguess.backend.configs.handler.CustomAccessDeniedHandler;
import at.timeguess.backend.configs.handler.CustomAuthenticationFailureHandler;
import at.timeguess.backend.spring.CustomizedLogoutSuccessHandler;

/**
 * Spring configuration for web security.
 */
@Configuration
@EnableWebSecurity()
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Bean
    protected LogoutSuccessHandler logoutSuccessHandler() {
        return new CustomizedLogoutSuccessHandler();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        http.headers().frameOptions().disable(); // needed for H2 console

        http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).invalidateHttpSession(true)
                .deleteCookies("JSESSIONID").logoutSuccessUrl("/login.xhtml")
                .logoutSuccessHandler(this.logoutSuccessHandler());

        http.authorizeRequests()
                // Permit access to the H2 console
                .antMatchers("/h2-console/**").permitAll().antMatchers("/debug/**").permitAll()
                // Permit access for all to error pages
                .antMatchers("/error/**").permitAll()
                // Only access with admin role
                .antMatchers("/admin/**").hasAnyAuthority("ADMIN")
                // Only access with manager role
                .antMatchers("/manager/**").hasAnyAuthority("MANAGER")
                // Permit access only for some roles
                .antMatchers("/secured/**").hasAnyAuthority("ADMIN", "MANAGER", "PLAYER")
                // Allow only certain roles to use websockets (only logged in users)
                .antMatchers("/omnifaces.push/**").hasAnyAuthority("ADMIN", "MANAGER", "PLAYER").and().formLogin()
                .loginPage("/login.xhtml").loginProcessingUrl("/login").defaultSuccessUrl("/secured/welcome.xhtml")
                .failureHandler(authenticationFailureHandler());
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());
        http.sessionManagement().invalidSessionUrl("/error/invalid_session.xhtml");

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // Configure roles and passwords via datasource
        auth.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery("SELECT username, password, enabled FROM user WHERE username=?")
                .authoritiesByUsernameQuery("SELECT username, roles FROM user_role r JOIN user u ON r.user_id=u.id WHERE u.username=?");
    }
    
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
    
    @Bean
    public static PasswordEncoder passwordEncoder() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        ((DelegatingPasswordEncoder) encoder).setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
        return encoder;
    }
}
