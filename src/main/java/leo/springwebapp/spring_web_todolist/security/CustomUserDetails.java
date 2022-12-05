package leo.springwebapp.spring_web_todolist.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("webUserDetails")
public class CustomUserDetails implements UserDetails {
	
	private String userName;
	private String password;
	private long userId;
	private String userRole;
	private Collection<? extends GrantedAuthority> authorities;
	
	@Autowired
	public CustomUserDetails() {
	}
	
//	@Autowired
//	public WebUserDetails(User user) {
//		this.userName = user.getEmail();
//		this.userId = user.getId();
//		this.password = user.getPassword();
//		this.userRole = user.getRole().getName();
//		this.authorities = Collections.singleton(new SimpleGrantedAuthority(user.getRole().getName()));
//	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String getUsername() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public long getUserId() {
		return userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public String getUserRole() {
		return userRole;
	}
	
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	
	@Override
	public boolean isAccountNonExpired() {

		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {

		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {

		return true;
	}
	
	@Override
	public boolean isEnabled() {

		return true;
	}
	
	

}