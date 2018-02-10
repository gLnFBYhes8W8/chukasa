package pro.hirooka.chukasa.domain.config.aaa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pro.hirooka.chukasa.domain.config.SpringConfiguration;
import pro.hirooka.chukasa.domain.service.aaa.IChukasaUserDetailsService;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MultiHttpSecurityConfiguration {

    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        SpringConfiguration springConfiguration;
        @Autowired
        IChukasaUserDetailsService chukasaUserDetailsService;
        @Autowired
        AaaConfiguration aaaConfiguration;

        @Override
        protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
            if(aaaConfiguration.isEnabled()) {
                if (springConfiguration.getProfiles().contains("postgresql")
                        || springConfiguration.getProfiles().contains("mysql")
                        || springConfiguration.getProfiles().contains("hsqldb")) {
                    authenticationManagerBuilder.userDetailsService(this.chukasaUserDetailsService);//.passwordEncoder(passwordEncoder()); // TODO:
                } else {
                    final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
                    authenticationManagerBuilder.inMemoryAuthentication().withUser("admin").password(passwordEncoder.encode("admin")).roles("ADMIN");
                    authenticationManagerBuilder.inMemoryAuthentication().withUser("user").password(passwordEncoder.encode("user")).roles("USER");
                    authenticationManagerBuilder.inMemoryAuthentication().withUser("guest").password(passwordEncoder.encode("guest")).roles("GUEST");
                }
            }
        }

        protected void configure(HttpSecurity http) throws Exception {
            if(aaaConfiguration.isEnabled()) {
                http
                        .antMatcher("/api/**")
                        .authorizeRequests()
                        .anyRequest()
                        .authenticated()
                        .and()
                        .httpBasic()
                        .and()
                        .csrf().disable();
            }else{
                http
                        .antMatcher("/**")
                        .authorizeRequests()
                        .anyRequest()
                        .permitAll()
                        .and()
                        .httpBasic().disable();
            }
        }

        @Override
        public void configure(WebSecurity web){
            web.ignoring().antMatchers("/images/**", "/webjars/**");
        }
    }

    @Configuration
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        SpringConfiguration springConfiguration;
        @Autowired
        IChukasaUserDetailsService chukasaUserDetailsService;
        @Autowired
        AaaConfiguration aaaConfiguration;

        @Override
        protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
            if(aaaConfiguration.isEnabled()) {
                if (springConfiguration.getProfiles().contains("postgresql")
                        || springConfiguration.getProfiles().contains("mysql")
                        || springConfiguration.getProfiles().contains("hsqldb")) {
                    authenticationManagerBuilder.userDetailsService(this.chukasaUserDetailsService);//.passwordEncoder(passwordEncoder()); // TODO:
                } else {
                    final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
                    authenticationManagerBuilder.inMemoryAuthentication().withUser("admin").password(passwordEncoder.encode("admin")).roles("ADMIN");
                    authenticationManagerBuilder.inMemoryAuthentication().withUser("user").password(passwordEncoder.encode("user")).roles("USER");
                    authenticationManagerBuilder.inMemoryAuthentication().withUser("guest").password(passwordEncoder.encode("guest")).roles("GUEST");
                }
            }
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            if(aaaConfiguration.isEnabled()) {
                http
                        .authorizeRequests()
                        .anyRequest().authenticated()
                        .and()
                        .formLogin()
                        .loginPage("/login")
                        .defaultSuccessUrl("/chukasa")
                        .permitAll()
                        .and()
                        .logout()
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll();
            }else{
                http
                        .antMatcher("/**")
                        .authorizeRequests()
                        .anyRequest()
                        .permitAll()
                        .and()
                        .httpBasic().disable();
            }
        }

        @Override
        public void configure(WebSecurity web){
            web.ignoring().antMatchers("/images/**", "/webjars/**");
        }
    }
}
