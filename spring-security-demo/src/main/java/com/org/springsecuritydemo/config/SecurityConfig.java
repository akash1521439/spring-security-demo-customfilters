package com.org.springsecuritydemo.config;

import com.org.springsecuritydemo.filter.AuthoritiesLoggingAfterFilter;
import com.org.springsecuritydemo.filter.CsrfCookieFilter;
import com.org.springsecuritydemo.filter.RequestValidationBeforeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

//Default authentication provider DaoAuthenticationProvider
@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain defaultSecurityFilter(HttpSecurity http) throws Exception{
//        Deprecated
//        http.authorizeHttpRequests()
//                .requestMatchers("/home","/balance","/loans").permitAll()
//                .requestMatchers("/profile","/friends").authenticated()
//                .and().formLogin()
//                .and().httpBasic();
//        return http.build();
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http.securityContext(securityContext->securityContext.requireExplicitSave(false))
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .cors(httpSecurityCorsConfigurer -> corsConfigurationSource())
                .csrf((csrf)->csrf.csrfTokenRequestHandler(requestHandler).ignoringRequestMatchers("/register")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new RequestValidationBeforeFilter(),BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthoritiesLoggingAfterFilter(),BasicAuthenticationFilter.class)
                .authorizeHttpRequests((request-> request.requestMatchers("/register","/home","/notices").permitAll()
//                        .requestMatchers("/myCards").hasAuthority("CARDS")
                        .requestMatchers("/myCards").hasRole("USER")
                        .requestMatchers("/logins","/user","/myAccount").authenticated()));
        http.formLogin(withDefaults());
        http.httpBasic(withDefaults());
        return http.build();

    }

  /*  .configurationSource(new CorsConfigurationSource() {
        @Override
        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of("http://localhost:4200/"));
            configuration.setAllowedMethods(Arrays.asList("GET","POST"));
            configuration.setAllowCredentials(true);
            configuration.setAllowedHeaders(List.of("*"));
//                        CorsConfiguration source = new CorsConfiguration();
            //source.registerCorsConfiguration("/**", configuration);
            return configuration;
        }
    })*/
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200/"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    //Using in memory user details manager
    /*@Bean
    public InMemoryUserDetailsManager userDetailsManager(){
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("12345")
                .authorities("admin")
                .build();
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("12345")
                .authorities("read")
                .build();
        return new InMemoryUserDetailsManager(admin,user);
    }*/
    /*@Bean
    public  InMemoryUserDetailsManager userDetailsManager(){
        UserDetails admin = User.withUsername("admin")
                .password("12345")
                .authorities("admin")
                .build();
        UserDetails user = User.withUsername("user")
                .password("12345")
                .authorities("read")
                .build();
        return new InMemoryUserDetailsManager(admin,user);
    }*/
    //using jdbc user details manager
    /*@Bean
    public UserDetailsService userDetailsService(DataSource dataSource){
        return new JdbcUserDetailsManager(dataSource);
    }*/
    @Bean
    public PasswordEncoder passwordEncoder(){
        //return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder();
    }
}
