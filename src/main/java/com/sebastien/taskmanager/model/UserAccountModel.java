package com.sebastien.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity (name = "UserAccount")
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uc_username", columnNames = {"username"})
})
public class UserAccountModel implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleModel> roles = new HashSet<>();
}
