package com.pfa.api.app.repository;
import com.pfa.api.app.entity.user.TeamPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectPreferenceRepository extends JpaRepository<TeamPreference, Long> {
}
