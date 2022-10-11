package com.fatmakahveci.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fatmakahveci.blog.UserNotFoundException;
import com.fatmakahveci.blog.model.User;
import com.fatmakahveci.blog.service.UserService;

@RestController
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/users")
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping(path = "/users/{id}")
    public User viewUserById(@PathVariable Integer id) {
        return userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    // @GetMapping("/users/add")
    // public ModelAndView addUser() {
    // }
    
    // @PostMapping(value="/users/save")
    // public ModelAndView saveUser(@ModelAttribute User user) {
    // }

    // @GetMapping("/users/delete/{id}")
    // public ModelAndView deleteUser(@PathVariable Integer id) {
    // }

    // @GetMapping("/users/edit/{id}")
    // public ModelAndView editUser(@PathVariable Integer id) {
    // }

    // @PostMapping("/users/update/{id}")
	// public ModelAndView updateUser(@PathVariable Integer id, User user) {
	// }
}
