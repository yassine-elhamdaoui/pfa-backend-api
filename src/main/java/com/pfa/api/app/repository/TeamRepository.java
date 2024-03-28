package com.pfa.api.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.Team;
import com.pfa.api.app.entity.user.User;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Team findByName(String teamName);

    List<Team> findByMembersContaining(User user);

}
