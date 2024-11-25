package com.moliveira.demo_park_api.repository;

import com.moliveira.demo_park_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
