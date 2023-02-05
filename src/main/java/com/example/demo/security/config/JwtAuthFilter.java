package com.example.demo.security.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.security.repository.UserRepository;
import com.example.demo.security.util.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private static final String AUTH_HEADER_START = "Bearer ";
	private static final String AUTH_HEADER_NAME = "Authorization";
	private final UserRepository userRepository;
	private JwtUtils jwtUtils = new JwtUtils();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String authorizationHeader = request.getHeader(AUTH_HEADER_NAME);

		String username = null;
		String jwtToken = null;

		if (authorizationHeader == null || !authorizationHeader.startsWith(AUTH_HEADER_START)) {
			log.info("Header with Key: " + AUTH_HEADER_NAME + " and starting with \"" + AUTH_HEADER_START + "\" not found");
			filterChain.doFilter(request, response);
			return;
		}

		jwtToken = authorizationHeader.substring(AUTH_HEADER_START.length());
		username = jwtUtils.extractUsername(jwtToken);

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = this.userRepository.findUserByUserName(username);

			if (jwtUtils.validateToken(jwtToken, userDetails)) {

				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());

				usernamePasswordAuthenticationToken.setDetails(
						new WebAuthenticationDetailsSource()
						.buildDetails(request)
						);

				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			} else {
				log.info("Invalid token");
			}
		} else {
			log.info("User Name is empty");
		}

		filterChain.doFilter(request, response);
	}

}