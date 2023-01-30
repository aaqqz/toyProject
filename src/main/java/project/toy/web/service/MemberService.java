package project.toy.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import project.toy.web.domain.Member;
import project.toy.web.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Long join(Member member) {
        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원 없음")); // todo 상황에 맞는 exception 생성
    }
}
