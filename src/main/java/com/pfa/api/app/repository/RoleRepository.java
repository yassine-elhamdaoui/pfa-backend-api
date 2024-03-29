package com.pfa.api.app.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.user.Role;
import com.pfa.api.app.entity.user.RoleName;

<<<<<<< HEAD
public interface RoleRepository extends JpaRepository<Role, Long> {
=======

public interface RoleRepository extends JpaRepository<Role , Long>{
>>>>>>> parent of ae32e42 (adding the ability to create team ans assign members to it)
    Optional<Role> findByName(RoleName name);
}
