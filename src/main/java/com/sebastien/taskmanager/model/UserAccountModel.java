package com.sebastien.taskmanager.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity (name = "UserAccount")
public class UserAccountModel {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<RoleModel> roles = new HashSet<>();
}
