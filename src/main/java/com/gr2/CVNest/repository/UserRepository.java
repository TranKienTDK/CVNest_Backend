package com.gr2.CVNest.repository;

import com.gr2.CVNest.entity.Company;
import com.gr2.CVNest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
    User findByRefreshTokenAndEmail(String token, String email);

    List<User> findByCompany(Company company);
}
