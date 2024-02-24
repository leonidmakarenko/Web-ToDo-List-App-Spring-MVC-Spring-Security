# Web-ToDo-List-App-Spring-MVC-Spring-Security
Briefly about my pet project - ToDo list planner. Although the idea and logic are simple, the structure of the application, its functionality, dependencies, and Entity interaction with each other allow me to demonstrate the level of skills in using technologies and solutions for building Spring Boot applications that I have at the moment.
The application is built with Spring Boot, using Java11, Spring MVC, Hibernate, Spring Data JPA, Thymeleaf, JSP, Project Lombok, Spring Security, and PostgreSQL database.

The application has the following layers: entity model layer, layer for performing actions with entities, which consists of service interfaces and their implementations, repository layer for interacting with the database, and controller layer for processing requests. The DTO layer was made just for the purpose of demonstration, only for one entity, because I don't see the need for it in a monolith, unlike REST. There is also an error handling layer, which I made using the GlobalExceptionHandler, and a security layer.

Security is implemented using Spring Security (authentication and authorization), spring validation (validation of entity field input data), Thymeleaf security (control of display of web components). Basic access and authentication settings are defined in the SecurityConfigurer class extends WebSecurityConfigurerAdapter, more fine-grained settings can be made in the security classes CustomAuthenticationFilter, CustomAuthenticationManager, CustomAuthenticationProvider, CustomAuthenticationToken, CustomUserDetails, which I have overridden to demonstrate this feature.
Authorization is implemented at the controller layer, based on annotations using SpEL enabled by using the @EnableGlobalMethodSecurity annotation in @Configuration. Authorization depends on the static component - the role of the user in the system, and the dynamic component - the relationship of the interacting user to another entity, as of the moment of their interaction.

The jsp template display layer is implemented using html, css, thymeleaf.
	
The demo data was loaded into the database in two ways (by Spring, using the data.sql file at startup and automatically loading after the application is launched (bootstrap level). 

All this is combined with SpringBoot and configuration files.

Pre-loaded users (login, password, role):

nick@mail.com , 1111 , user;

nora@mail.com , 2222, user;

mike@mail.com , admin , admin;

server.port=9090

http://localhost:9090/

database:

username=postgres

password=1234
