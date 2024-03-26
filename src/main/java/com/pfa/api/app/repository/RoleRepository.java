package com.pfa.api.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.user.Role;
import com.pfa.api.app.entity.user.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleName name);
}
