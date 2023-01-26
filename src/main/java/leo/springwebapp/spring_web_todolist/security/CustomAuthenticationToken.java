package leo.springwebapp.spring_web_todolist.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationToken implements Authentication {
	private final UserDetails userDetails;
	private boolean isAuthenticated;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return userDetails.getAuthorities();
	}
	
	@Override
	public Object getCredentials() {
		return userDetails.getPassword();
	}
	
	@Override
	public Object getDetails() {
		return userDetails;
	}
	
	@Override
	public Object getPrincipal() {
		return userDetails.getUsername();
	}
	
	@Override
	public boolean isAuthenticated() {
		return isAuthenticated;
	}
	
	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		this.isAuthenticated = isAuthenticated;
	}
	
	@Override
	public String getName() {
		return userDetails.getUsername();
	}
}