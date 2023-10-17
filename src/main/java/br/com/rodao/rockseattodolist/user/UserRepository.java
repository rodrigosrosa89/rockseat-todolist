package br.com.rodao.rockseattodolist.user;

import br.com.rodao.rockseattodolist.user.dto.UserLombokModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserLombokModel, UUID> {

    UserLombokModel findByUsername(String userName);
}
