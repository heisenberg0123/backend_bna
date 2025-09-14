package com.example.reporting.services.auth;

import com.example.reporting.dtos.SignupRequest;
import com.example.reporting.dtos.UserDto;
import com.example.reporting.entities.User;
import com.example.reporting.entities.UserRole;
import com.example.reporting.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImplementation implements AuthService {


    public final UserRepository userRepository;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    public AuthServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @PostConstruct
    public  void  createAdminAccount(){
        User adminacount=userRepository.findByUserRole(UserRole.Admin);
        if(adminacount==null){
            User user=new User();
            user.setUserRole(UserRole.Admin);
            user.setName("admin");
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));
            user.setEmail("admin@test.com");
            user.setProfile("admin");
            userRepository.save(user);
        }}
    @Override
    public UserDto createUser(SignupRequest signupRequest) {
        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(encoder.encode(signupRequest.getPassword()));
        user.setUserRole(UserRole.Customer);
        user.setProfile(signupRequest.getProfile());
        User createUser = userRepository.save(user);
        UserDto userDto = new UserDto();
        userDto.setName(createUser.getName());
        userDto.setEmail(createUser.getEmail());
        userDto.setId(createUser.getId());
        userDto.setUserRole(createUser.getUserRole());
        userDto.setProfile(createUser.getProfile());
        return userDto;


    }
}
