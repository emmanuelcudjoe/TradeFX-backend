package com.cjvisions.tradefx_backend.controllers;

import com.cjvisions.tradefx_backend.domain.dto.AuthResponse;
import com.cjvisions.tradefx_backend.domain.dto.UserRegistrationDetails;
import com.cjvisions.tradefx_backend.domain.models.UserLoginInfo;
import com.cjvisions.tradefx_backend.domain.models.UserRegistrationInfo;
import com.cjvisions.tradefx_backend.repositories.UserLoginRepository;
import com.cjvisions.tradefx_backend.repositories.UserRegistrationRepository;
import com.cjvisions.tradefx_backend.services.UserService;
import com.cjvisions.tradefx_backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserAuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private UserLoginRepository userLoginRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String registerUser(@RequestBody @Validated  UserRegistrationDetails userRegistrationDetails, BindingResult result){
        System.out.println(userRegistrationDetails);
        System.out.println(result);

        var existingUser = userLoginRepository.findByEmail(userRegistrationDetails.email());

        if (existingUser != null){
            return "User already exists";
        }

        UserRegistrationInfo userRegistrationInfo = new UserRegistrationInfo();
        userRegistrationInfo.setFirstName(userRegistrationDetails.firstName());
        userRegistrationInfo.setLastName(userRegistrationDetails.lastName());
        userRegistrationInfo.setEmail(userRegistrationDetails.email());
        userRegistrationInfo.setPassword(passwordEncoder.encode(userRegistrationDetails.password()));

        if (userService.registerUser(userRegistrationInfo)){

            return "Done";
        }
        return "User not saved";
    }

    @PostMapping("/login")
    public AuthResponse loginUser(@RequestBody @Validated  UserLoginInfo userLoginInfo) {

        return userService.authenticateUser(userLoginInfo);
    }
}
