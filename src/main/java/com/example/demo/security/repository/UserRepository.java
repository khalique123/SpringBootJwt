package com.example.demo.security.repository;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.example.demo.consume.dto.UserExtResponse;
import com.example.demo.security.config.Roles;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepository {
	private final RestTemplate restTemplate;

	/*
	private final static List<UserDetails> APPLICATION_USERS = Arrays.asList(
			new User("user"
					, "$2a$12$eDgrRbBJrEO90RJ2QyV1zuiIoCCrFUi4uTjefnaXOJ1RTmA0fGKn."
					, Collections.singleton(new SimpleGrantedAuthority(Roles.USER.getRole()))),
			new User("admin"
					, "$2a$12$Z1GWJss3I9CG9HDnLTZcVuTVtjQkLd18Ndg8oqw4tVpE89iaWiMpm"
					, Collections.singleton(new SimpleGrantedAuthority(Roles.USER.getRole())))
			);
	 */
	public UserDetails findUserByUserName(String userName) {
		/*
		return APPLICATION_USERS
				.stream()
				.filter(user -> user.getUsername().equals(userName))
				.findFirst()
				.orElseThrow(() -> new UsernameNotFoundException("User Name: \"" + userName + "\" not found"));

		 */

		UserExtResponse userExtResponse = restTemplate.getForObject("http://localhost:8080/users/user/" + userName, UserExtResponse.class);

		User user =  new User(
				userExtResponse.getUserName()
				, userExtResponse.getPassword()
				, Collections.singleton(new SimpleGrantedAuthority(Roles.USER.getRole()))
				);

		return (UserDetails) user;
	}
}
