package com.pfa.api.app.entity.user;

import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.user.User;
import jakarta.persistence.*;

@Entity
public class TeamPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public int getPreferenceRank() {
        return preferenceRank;
    }

    public void setPreferenceRank(int preferenceRank) {
        this.preferenceRank = preferenceRank;
    }

    @ManyToOne
    private Project project;

    private int preferenceRank;

    // Getters and setters
}