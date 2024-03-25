package com.pfa.api.app.entity.user;

import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class TeamPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Project project;

    private int preferenceRank;

}