package com.javalord.MyPhotoAppApiUsers.controller;

import com.javalord.MyPhotoAppApiUsers.model.CreateUserRequestModel;
import com.javalord.MyPhotoAppApiUsers.model.CreateUserResponseModel;
import com.javalord.MyPhotoAppApiUsers.model.UserResponseModel;
import com.javalord.MyPhotoAppApiUsers.service.UsersService;
import com.javalord.MyPhotoAppApiUsers.shared.UserDto;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private Environment environment;

    @GetMapping("/status/check")
    public String status() {

        return "Working on port " + environment.getProperty("local.server.port") +
                ", with token = " + environment.getProperty("token.secret");
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequestModel userDetails) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        UserDto dto = usersService.createUser(userDto);

        CreateUserResponseModel userResponseModel = modelMapper.map(dto, CreateUserResponseModel.class);

        return ResponseEntity.created(null).body(userResponseModel);
    }

    @GetMapping(value = "/{userId}", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
    })
    public ResponseEntity<UserResponseModel> getUser(@PathVariable("userId") String userId)
    {

        UserDto userDto = usersService.getUserByUserId(userId);
        UserResponseModel returnValue = new ModelMapper().map(userDto, UserResponseModel.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }

}
