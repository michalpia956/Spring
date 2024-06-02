package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.User;

import com.capgemini.wsb.fitnesstracker.user.api.SimpleUser;
import org.springframework.stereotype.Component;

@Component
public class UserSimpleMapper {
    public SimpleUser toDto(User user) {
        return new SimpleUser(user.getId(), user.getFirstName(), user.getLastName());
    }
}
