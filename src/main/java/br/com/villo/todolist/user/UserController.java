package br.com.villo.todolist.user;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
  
  @PostMapping("/")
  public void create(@RequestBody UserModel userData) {
    System.out.println(userData.name);
  }

}
