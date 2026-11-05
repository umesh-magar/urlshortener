package com.umesh.urlshortener.services.impl;

import com.umesh.urlshortener.config.JwtService;
import com.umesh.urlshortener.dto.AuthResponseDto;
import com.umesh.urlshortener.dto.LoginRequestDto;
import com.umesh.urlshortener.dto.RegisterRequestDto;
import com.umesh.urlshortener.entity.User;
import com.umesh.urlshortener.exceptions.ConflictException;
import com.umesh.urlshortener.exceptions.UnAuthorizedException;
import com.umesh.urlshortener.mappers.AuthMapper;
import com.umesh.urlshortener.repository.UserRepository;
import com.umesh.urlshortener.services.AuthService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private  final String dummyHash;
    private final JwtService jwtService;
    public AuthServiceImpl(UserRepository userRepository,AuthMapper authMapper,PasswordEncoder passwordEncoder,JwtService jwtService){
        this.userRepository=userRepository;
        this.authMapper=authMapper;
        this.passwordEncoder=passwordEncoder;
        this.dummyHash=passwordEncoder.encode("dkadalkdhkadkkjla");
        this.jwtService=jwtService;

    }
    @Override
    @Transactional
    public AuthResponseDto register(RegisterRequestDto requestDto) {
        if(userRepository.existsByEmail(requestDto.getEmail())){
            throw new ConflictException("email already registered");
        }
        String hashedPassword=passwordEncoder.encode(requestDto.getPassword());
        User user=authMapper.toEntity(requestDto,hashedPassword);
        try{
            User savedUser=userRepository.save(user);
            return authMapper.toResponse(savedUser);
        }catch (DataIntegrityViolationException exception){
            throw  new ConflictException("email is already registered");
        }

    }

    @Override
    @Transactional
    public AuthResponseDto login(LoginRequestDto requestDto) {
        if(!userRepository.existsByEmail(requestDto.getEmail())){
            throw new ConflictException("user doesnot exist");
        }
        User user=userRepository.findByEmail(requestDto.getEmail());
        String storeHash=user!=null? user.getPassword() : dummyHash;
        boolean passwordOk=passwordEncoder.matches(requestDto.getPassword(),storeHash);
        if(user!=null || !passwordOk){
            throw new UnAuthorizedException("invalid email or password");
        }

        String accessToken= jwtService.generateAccessToken(user);
        return authMapper.toResponse(user);
    }


}
