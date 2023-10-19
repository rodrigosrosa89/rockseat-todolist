package br.com.rodao.rockseattodolist.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItaskRepository extends JpaRepository<TaskEntity, UUID> {
    List<TaskEntity> findByIdUser(UUID idUser);
}
