package leo.springwebapp.spring_web_todolist.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationManager implements AuthenticationManager {
	private final List<AuthenticationProvider> authenticationProviders;

	@Override
	public Authentication authenticate(Authentication authentication) {
		Authentication webAuthentication;
		for(AuthenticationProvider authenticationProvider: authenticationProviders) {
			webAuthentication = authenticationProvider.authenticate(authentication);
			if (webAuthentication != null) {
				webAuthentication.setAuthenticated(true);
				return webAuthentication;
			}
		}
		throw new BadCredentialsException("Bad user Credentials!");
	}	
}