package com.example.demo.security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.security.dto.AuthenticationReqest;
import com.example.demo.security.service.UserService;
import com.example.demo.security.util.JwtUtils;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
	private final AuthenticationManager authenticationManager;
	private final UserService userService;
	private final JwtUtils jwtUtils;

	@PostMapping("/authenticate")
	public ResponseEntity<String> authenticate(@RequestBody AuthenticationReqest request) {
		log.info("AuthenticationController.authenticate() method was called");

		String userName = request.getUserName();
		String password = request.getPassword();

		if(null == userName || userName.trim().isEmpty()) {
			log.debug("Empty UserName passed");
		}

		if(null == password || password.trim().isEmpty()) {
			log.debug("Empty password passed");
		}

		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(userName, password)
				);

		final UserDetails user = userService.loadUserByUsername(userName);

		if(user != null) {
			log.info("Token generated and passed to client");
			return ResponseEntity.ok(jwtUtils.generateToken(user));
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Some error occured");
	}
}
