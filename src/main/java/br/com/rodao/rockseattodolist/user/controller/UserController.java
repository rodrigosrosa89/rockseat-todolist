package br.com.rodao.rockseattodolist.user.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.rodao.rockseattodolist.user.UserRepository;
import br.com.rodao.rockseattodolist.user.dto.UserLombokModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody UserLombokModel user) {
        UserLombokModel userBanco = userRepository.findByUsername(user.getUsername());
        if (userBanco != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe");
        }

        String passwordCrypt = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());

        user.setPassword(passwordCrypt);
        UserLombokModel userCriado = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCriado);
    }
}
