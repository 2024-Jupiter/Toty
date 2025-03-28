package com.toty.springconfig.security.authentication;

import java.util.Collection;
import java.util.List;

import com.toty.user.domain.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;


@Getter
public class AccountAdapter extends org.springframework.security.core.userdetails.User {

    private User user;

    public AccountAdapter(User user) {
        super(user.getEmail(), user.getPassword(), List.of(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole().toString();
            }
        }));
        this.user = user;
    }

    public AccountAdapter(String username, String password,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

}