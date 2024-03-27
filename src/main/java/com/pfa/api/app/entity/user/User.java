package com.pfa.api.app.entity.user;

import java.util.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.pfa.api.app.entity.Branch;
import com.pfa.api.app.entity.Project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "user",
    uniqueConstraints = @UniqueConstraint(columnNames = {"email","cin","inscription_number"})
)
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id" , length = 45)
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email" , nullable = false)
    private String email;

    @Column(name = "phone_number" )
    private String phoneNumber;

    @Column(name = "cin" )
    private String cin;

    @Column(name = "inscription_number" )
    private String inscriptionNumber;

    @Column(name = "password")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;
    
    @Column(name = "enabled")
    private Boolean enabled;

    @ManyToOne(targetEntity = Branch.class)
    @JoinColumn(name = "branch_id")
    private Branch studiedBranch;
    
    @OneToOne(mappedBy = "headOfBranch")
    private Branch branch;

    @OneToOne(mappedBy = "user")
    private Confirmation confirmation;

    @ManyToMany(mappedBy = "profs")
    private List<Branch> branches;
    
    @ManyToMany(mappedBy = "supervisors")
    @JsonIgnore
    private List<Project>  projects ;


    @Override
   public Collection<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName().toString()));
        }
        return authorities;
    }


    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;

    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
       
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }


    // @Override
    // public int hashCode() {
    //     final int prime = 31;
    //     int result = 1;
    //     result = prime * result + ((email == null) ? 0 : email.hashCode());
    //     return result;
    // }



}
