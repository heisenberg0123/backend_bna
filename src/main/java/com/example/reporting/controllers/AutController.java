package com.example.reporting.controllers;

import com.example.reporting.dtos.AuthenticationRequest;
import com.example.reporting.dtos.AuthenticationResponse;
import com.example.reporting.dtos.SignupRequest;
import com.example.reporting.dtos.UserDto;
import com.example.reporting.entities.User;
import com.example.reporting.repositories.UserRepository;
import com.example.reporting.services.auth.AuthService;
import com.example.reporting.services.auth.jwt.UserDetailsServiceImpl;
import com.example.reporting.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")

@RestController
@RequestMapping("/api/auth")



public class AutController {
     final AuthService authService;
     final AuthenticationManager authenticationManager;

     final UserDetailsServiceImpl userDetailsService;
     final JwtUtil jwtUtil;
     final UserRepository user;


    public AutController(AuthService authService, @Lazy AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, UserRepository user) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.user = user;
    }


    @PostMapping("/signup")

    public ResponseEntity<?> singupUser(@RequestBody SignupRequest SignupRequest){

        UserDto createuserDto= authService.createUser(SignupRequest);

        if(createuserDto==null){
            return  new ResponseEntity<>("user not created try again", HttpStatus.BAD_REQUEST);
        }
        {
            return new ResponseEntity<>(createuserDto,HttpStatus.CREATED);
        }

    }
@PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response)throws IOException {
        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),authenticationRequest.getPassword()));
        }catch (BadCredentialsException e){
            throw new BadCredentialsException("incorrect username or Password");
        }catch (DisabledException disabledException){
            response.sendError(HttpServletResponse.SC_NOT_FOUND,"user not active ");
            return null;
        }
        final UserDetails userDetails1=userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
    final  String jwt=jwtUtil.generateToken(userDetails1.getUsername());
    Optional<User> optionalUser=user.findFirstByEmail(userDetails1.getUsername());
    AuthenticationResponse authtificationResponse=new AuthenticationResponse();
    if(optionalUser.isPresent()){
        authtificationResponse.setJwt(jwt);
        authtificationResponse.setUserRole(optionalUser.get().getUserRole());
        authtificationResponse.setUserId(optionalUser.get().getId());
        authtificationResponse.setProfile(optionalUser.get().getProfile());
    }
    return  authtificationResponse;
}

}
