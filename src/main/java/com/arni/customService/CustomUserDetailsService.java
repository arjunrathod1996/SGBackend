package com.arni.customService;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.arni.Role.Role;
import com.arni.user.User;
import com.arni.user.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(username);

        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        return buildUserDetails(user);
    }

    public UserDetails loadUserByMobileAndOtp(String mobileNumber) {
        User user = userRepository.findByPhoneNumber(mobileNumber);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with mobile number: " + mobileNumber);
        }

        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(User user) {
        Role role = user.getRole();
        GrantedAuthority authority = new SimpleGrantedAuthority(role.getName().name());

        // Use the email if it exists, otherwise use the phone number
        String username = (user.getEmail() != null && !user.getEmail().isEmpty()) ? user.getEmail() : user.getPhoneNumber();

        return org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(user.getPassword() != null ? user.getPassword() : "") // Password can be empty for OTP auth
                .authorities(Collections.singletonList(authority))
                .build();
    }
}
