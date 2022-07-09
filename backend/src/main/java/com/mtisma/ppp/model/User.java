package com.mtisma.ppp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
    @Index(columnList = "username", unique = true)
})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @NotBlank
    private String username;

    @JsonIgnore
    @NotBlank
    private String password;

    @JsonIgnore
    private boolean enabled = true;

    private Role role;

    @Transient
    private String jwtToken;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }
}


