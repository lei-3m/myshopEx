package com.example.myShop.service;

import com.example.myShop.entity.Member;
import com.example.myShop.constant.Role;
import com.example.myShop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor

public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member){
        Member findMember=memberRepository.findByEmail(member.getEmail());
        if(findMember!=null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email)throws UsernameNotFoundException{
        Member member=memberRepository.findByEmail(email);
        if(member==null){
            throw new UsernameNotFoundException(email);
        }
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

    public Member saveOrUpdateOAuthMember(String email, String name){
        Member member = memberRepository.findByEmail(email);
        if(member == null){
            Member newMember = new Member();
            newMember.setEmail(email);
            newMember.setName(name != null ? name : "Kakao User");
            // Keep password non-empty for compatibility with existing Member/UserDetails flow.
            String randomPassword = UUID.randomUUID().toString();
            newMember.setPassword(passwordEncoder.encode(randomPassword));
            newMember.setAddress("KAKAO");
            newMember.setRole(Role.USER);
            return saveMember(newMember);
        }

        if(name != null && !name.isBlank()){
            member.setName(name);
        }
        return member;
    }
}
