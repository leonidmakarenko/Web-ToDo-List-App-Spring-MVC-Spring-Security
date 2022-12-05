package leo.springwebapp.spring_web_todolist.configuration;

import leo.springwebapp.spring_web_todolist.security.CustomAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
//@EnableWebSecurity
@EnableWebSecurity(debug = true)
@EnableMethodSecurity(prePostEnabled = true)
//@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true,
//jsr250Enabled = true)

public class SecurityConfigurer {
    private final CustomAuthenticationFilter authenticationFilter;
//	private final UsernamePasswordAuthenticationFilter authenticationFilter;

    @Autowired
    public SecurityConfigurer(@Lazy CustomAuthenticationFilter authenticationFilter) {
//		public WebSecurityConfigurer(@Lazy UsernamePasswordAuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().requestMatchers("/login", "/users/create").permitAll().anyRequest().authenticated()
//         .and()
//            .formLogin()
//            .loginPage("/login")
//            .defaultSuccessUrl("/home")
//            .failureUrl("/login?error=true")
//            .permitAll()
                .and().logout()
//            .logoutUrl("/perform-logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/perform-logout", "POST")).invalidateHttpSession(true)
                .clearAuthentication(true).deleteCookies("JSESSIONID").logoutSuccessUrl("/login").permitAll().and()
                .exceptionHandling().accessDeniedPage("/not-found");

        http.addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling().authenticationEntryPoint(
                (request, response, authException) -> response.sendRedirect(request.getContextPath() + "/login"));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
