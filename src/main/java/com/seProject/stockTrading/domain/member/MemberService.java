package com.seProject.stockTrading.domain.member;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface MemberService extends UserDetailsService {
    Member CreateMember(MemberRequestDTO.MemberForm memberForm, String encodedPassword);

    boolean checkUsername(String username);

    boolean checkPerson(String name, Long number);

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
