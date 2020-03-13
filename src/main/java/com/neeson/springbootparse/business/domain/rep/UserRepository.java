package com.neeson.springbootparse.business.domain.rep;

import com.neeson.springbootparse.business.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}