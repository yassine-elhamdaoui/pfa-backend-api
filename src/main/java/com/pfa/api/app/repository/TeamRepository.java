package com.pfa.api.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfa.api.app.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Team findByName(String teamName);

}
