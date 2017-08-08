package pro.hirooka.chukasa.chukasa_aaa.domain.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pro.hirooka.chukasa.chukasa_aaa.domain.service.IChukasaUserDetailsService;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MultiHttpSecurityConfiguration {

    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        AaaConfiguration aaaConfiguration;
        @Autowired
        IChukasaUserDetailsService chukasaUserDetailsService;

        @Override
        protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
            if(aaaConfiguration.getProfiles().contains("postgresql")
                    || aaaConfiguration.getProfiles().contains("mysql")
                    || aaaConfiguration.getProfiles().contains("hsqldb")){
                authenticationManagerBuilder.userDetailsService(this.chukasaUserDetailsService);//.passwordEncoder(passwordEncoder()); // TODO:
            }else{
                authenticationManagerBuilder.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");
                authenticationManagerBuilder.inMemoryAuthentication().withUser("user").password("user").roles("USER");
                authenticationManagerBuilder.inMemoryAuthentication().withUser("guest").password("guest").roles("GUEST");
            }
        }

        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/**")
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .httpBasic();
        }

        @Override
        public void configure(WebSecurity web){
            web.ignoring().antMatchers("/images/**", "/webjars/**");
        }
    }

    @Configuration
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        AaaConfiguration aaaConfiguration;
        @Autowired
        IChukasaUserDetailsService chukasaUserDetailsService;

        @Override
        protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
            if(aaaConfiguration.getProfiles().contains("postgresql")
                    || aaaConfiguration.getProfiles().contains("mysql")
                    || aaaConfiguration.getProfiles().contains("hsqldb")){
                authenticationManagerBuilder.userDetailsService(this.chukasaUserDetailsService);//.passwordEncoder(passwordEncoder()); // TODO:
            }else{
                authenticationManagerBuilder.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");
                authenticationManagerBuilder.inMemoryAuthentication().withUser("user").password("user").roles("USER");
                authenticationManagerBuilder.inMemoryAuthentication().withUser("guest").password("guest").roles("GUEST");
            }
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/menu")
                    .permitAll()
                    .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login")
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                    .permitAll();
        }

        @Override
        public void configure(WebSecurity web){
            web.ignoring().antMatchers("/images/**", "/webjars/**");
        }
    }
}
