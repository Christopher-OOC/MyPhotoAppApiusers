package com.javalord.MyPhotoAppApiUsers.service;

import com.javalord.MyPhotoAppApiUsers.shared.UserDto;

public interface UsersService extends UserDetailsService {

    UserDto createUser(UserDto userDetails);

    UserDto getUserDetailsByEmail(String email);

    UserDto getUserByUserId(String userId);
}
