package com.ekino.team2.punctuality.service;

import com.ekino.team2.punctuality.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) {
//        return personRepository.findByUsername(userName)
//                .map(CustomUserDetails::new)
//                .orElseThrow(() -> new UsernameNotFoundException(userName));
        return  new CustomUserDetails();
    }

}
