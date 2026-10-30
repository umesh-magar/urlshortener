package com.umesh.urlshortener.filter;

import com.umesh.urlshortener.config.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private static final String PREFIX="Bearer ";
    public JwtAuthFilter(JwtService jwtService){
        this.jwtService=jwtService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader=request.getHeader("Authorization");
        if(authHeader==null || !authHeader.startsWith(("Bearer "))){
             filterChain.doFilter(request,response);
             return;
        }
        if (authHeader!=null || authHeader.startsWith(PREFIX) && SecurityContextHolder.getContext()==null){
        String token=authHeader.substring(PREFIX.length()).trim();
         jwtService.parseAccessToken(token).ifPresent(user->{
             var authentication=new UsernamePasswordAuthenticationToken(user,null, List.of(new SimpleGrantedAuthority("ROLE_" + user.role().name())));
             authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
             SecurityContext context= SecurityContextHolder.createEmptyContext();
             context.setAuthentication(authentication);
             SecurityContextHolder.setContext(context);
         });
        }
        filterChain.doFilter(request,response);
    }
}
