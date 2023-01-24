package leo.springwebapp.spring_web_todolist.service.impl;

import leo.springwebapp.spring_web_todolist.model.User;
import leo.springwebapp.spring_web_todolist.repository.UserRepository;
import leo.springwebapp.spring_web_todolist.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Autowired
	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	User user = userRepository.findByEmail(username);
    	if (user == null){
    		throw new UsernameNotFoundException(String.format("User '%s' not found", username));
    	}
    	CustomUserDetails userDetails = new CustomUserDetails();
    	userDetails.setAuthorities(Collections.singleton(new SimpleGrantedAuthority(user.getRole().getName())));
    	userDetails.setPassword(user.getPassword());
    	userDetails.setUserId(user.getId());
    	userDetails.setUserName(user.getEmail());
    	userDetails.setUserRole(user.getRole().getName());
    	return userDetails;
    }
}