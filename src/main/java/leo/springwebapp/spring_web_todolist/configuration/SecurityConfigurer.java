package leo.springwebapp.spring_web_todolist.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
//@EnableWebSecurity
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
//@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true,
//jsr250Enabled = true)

public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login", "/users/create")
                .permitAll().anyRequest()
                .authenticated()
//         .and()
//            .formLogin()
//            .loginPage("/login")
//            .defaultSuccessUrl("/home")
//            .failureUrl("/login?error=true")
//            .permitAll()
            .and()
                .logout()
//            .logoutUrl("/perform-logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/perform-logout", "POST"))
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/login").permitAll();


        http.exceptionHandling().authenticationEntryPoint(
                (request, response, authException) -> response.sendRedirect(request.getContextPath() + "/login"));

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

// for Spring Boot 3
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests().requestMatchers("/login", "/users/create").permitAll().anyRequest().authenticated()
////         .and()
////            .formLogin()
////            .loginPage("/login")
////            .defaultSuccessUrl("/home")
////            .failureUrl("/login?error=true")
////            .permitAll()
//                .and().logout()
////            .logoutUrl("/perform-logout")
//                .logoutRequestMatcher(new AntPathRequestMatcher("/perform-logout", "POST")).invalidateHttpSession(true)
//                .clearAuthentication(true).deleteCookies("JSESSIONID").logoutSuccessUrl("/login").permitAll().and()
//                .exceptionHandling().accessDeniedPage("/not-found");
//
//
//        http.exceptionHandling().authenticationEntryPoint(
//                (request, response, authException) -> response.sendRedirect(request.getContextPath() + "/login"));
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(12);
//    }
}
