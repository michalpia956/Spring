package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.SimpleUser;
import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.api.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserServiceImpl userService;

    private final UserMapper userMapper;

    private final UserSimpleMapper userSimpleMapper;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers()
                          .stream()
                          .map(userMapper::toDto)
                          .toList();
    }
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userMapper.toDto(userService.getUser(id).orElseThrow());
    }

    @PostMapping()
    public ResponseEntity<User> addUser(@RequestBody UserDto userDto){
        return new ResponseEntity<>(userService.createUser(userMapper.toEntity(userDto)), CREATED);
    }

    @GetMapping("/simple")
    public List<SimpleUser> getAllUserSimpleInformation(){
        return userService.findAllUsers().stream().map(userSimpleMapper::toDto).toList();
    }

    @GetMapping("/email")
    public List<UserDto> getUserByEmail(@RequestParam String email){
        return userService.getUsersWithEmail(email).stream().map(userMapper::toDto).toList();
    }
    @GetMapping("older/{date}")
    public List<UserDto> getUsersOlderThan(@PathVariable String date){
        return userService.bornAfter(date).stream().map(userMapper::toDto).toList();
    }
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        UserDto updatedUserDto = userService.updateUser(userId, userDto);
        return ResponseEntity.ok(updatedUserDto);
    }

}