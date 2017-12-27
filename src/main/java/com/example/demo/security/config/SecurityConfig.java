package com.example.demo.security.config;

import com.example.demo.security.handler.MyAuthenticationFailureHandler;
import com.example.demo.security.handler.MyAuthenticationSuccessHandler;
import com.example.demo.security.validate.imageCode.ImageCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {

        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        //tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
    }

    protected void configure(HttpSecurity http) throws Exception {
        ImageCodeFilter imageCodeFilter = new ImageCodeFilter();
        imageCodeFilter.setAuthenticationFailureHandler(myAuthenticationFailureHandler);
        imageCodeFilter.afterPropertiesSet();

        http.addFilterBefore(imageCodeFilter, UsernamePasswordAuthenticationFilter.class)
                /**
                 * 表单登录配置
                 */
                .formLogin() //表单登录
                .loginPage("/authentication/login.html") //自定义登录页面
                .loginProcessingUrl("/authentication/form") //与自定义登录页面处理路径一致
//        http.httpBasic()   basic模式弹出框登录
                .successHandler(myAuthenticationSuccessHandler)  //自定义认证成功处理
                .failureHandler(myAuthenticationFailureHandler)  //自定义认证失败处理
                .and()
                /**
                 * 需要认证的请求配置，
                 * 注:最为具体的请求路径放在前面，而最不具体的路径（如anyRequest()）放在最后面。
                 * 如果不这样做的话，那不具体的路径配置将会覆盖掉更为具体的路径配置
                 */
                .authorizeRequests()
                //允许这样请求通过，需要将登录所需路径配好，不然会一直重定向
                .antMatchers("/authentication/*","/image").permitAll()
                .anyRequest().authenticated()//任何请求都需要认证
                .and()
                .rememberMe()
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(3600)
                .userDetailsService(userDetailsService)
                .and()
                .csrf().disable(); //关闭csrf防御机制
    }

}
