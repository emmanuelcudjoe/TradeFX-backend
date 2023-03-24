package com.cjvisions.tradefx_backend.services;

import com.cjvisions.tradefx_backend.domain.dto.AuthResponse;
import com.cjvisions.tradefx_backend.domain.models.UserLoginInfo;
import com.cjvisions.tradefx_backend.domain.models.UserRegistrationInfo;
import com.cjvisions.tradefx_backend.repositories.UserLoginRepository;
import com.cjvisions.tradefx_backend.repositories.UserRegistrationRepository;
import com.cjvisions.tradefx_backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {


    @Autowired
    JwtUtil jwtUtil;


    private UserRegistrationRepository userRegistrationRepository;
    private UserLoginRepository userLoginRepository;

    public UserService(
            UserRegistrationRepository userRegistrationRepository,
            UserLoginRepository userLoginRepository
    ){
        this.userRegistrationRepository = userRegistrationRepository;
        this.userLoginRepository = userLoginRepository;
    }

    public boolean registerUser(UserRegistrationInfo userRegistrationInfo){
        var savedRegisteredUser = userRegistrationRepository.save(userRegistrationInfo);
        if (savedRegisteredUser != null){
            return true;
        }

        return false;
    }

    public AuthResponse authenticateUser(UserLoginInfo userLoginInfo){
        return this.getUserToken(userLoginInfo);
    }

    public AuthResponse getUserToken(UserLoginInfo user){

        var existingUser = userRegistrationRepository.findUserByEmail(user.getEmail());

        if (existingUser == null){
            return new AuthResponse("","");
        }

        try {

            String accessToken = jwtUtil.generateAccessToken(existingUser);
            System.out.println("User token " + accessToken);
            AuthResponse authResponse = new AuthResponse(user.getEmail(), accessToken);
            System.out.println("Response payload " + authResponse);
            return authResponse;
        }catch(BadCredentialsException exception){
            System.out.println("Bad credentials " + exception.getMessage());
            return new AuthResponse("","");
        }

    }
}
