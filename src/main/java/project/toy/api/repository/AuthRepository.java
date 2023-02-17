package project.toy.api.repository;

import org.springframework.data.repository.CrudRepository;
import project.toy.api.domain.Member;

import java.util.Optional;

public interface AuthRepository extends CrudRepository<Member, Long> {

    Optional<Member> findByEmailAndPassword(String email, String password);
}