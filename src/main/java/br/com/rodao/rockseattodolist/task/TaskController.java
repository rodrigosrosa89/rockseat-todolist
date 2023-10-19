package br.com.rodao.rockseattodolist.task;

import br.com.rodao.rockseattodolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ItaskRepository repository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskEntity taskEntity, HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        taskEntity.setIdUser((UUID) idUser);

        LocalDateTime currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskEntity.getStartAt()) || currentDate.isAfter(taskEntity.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data inicio/data fim deve ser maior que a data atual");
        }

        if (taskEntity.getStartAt().isAfter(taskEntity.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data inicio deve ser menor que a data término");
        }

        TaskEntity taskCriada = repository.save(taskEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskCriada);
    }

    @GetMapping("/")
    public List<TaskEntity> listTasksFromIdUser(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        List<TaskEntity> listTasks = repository.findByIdUser((UUID) idUser);
        return listTasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity updateTask(@RequestBody TaskEntity taskRequest, HttpServletRequest request, @PathVariable UUID id) {
        var idUserRequest = request.getAttribute("idUser");
        var taskOfData = repository.findById(id).orElse(null);
        if (taskOfData == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada");
        }

        if (taskOfData.getIdUser().equals(idUserRequest)) {
            Utils.copyNonNullProperties(taskRequest, taskOfData);
            return ResponseEntity.status(200).body(repository.save(taskOfData));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não tem permissão para alterar a tarefa");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity patchTask(@RequestBody TaskEntity taskEntity, HttpServletRequest request, @PathVariable UUID id) {
        var idUser = request.getAttribute("idUser");
        taskEntity.setIdUser((UUID) idUser);
        taskEntity.setId(id);
        return ResponseEntity.status(200).body(repository.save(taskEntity));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity deleteTask(@RequestBody TaskEntity taskEntity, HttpServletRequest request, @PathVariable UUID id) {
        var idUser = request.getAttribute("idUser");
        taskEntity.setIdUser((UUID) idUser);
        taskEntity.setId(id);
        repository.delete(taskEntity);
        return ResponseEntity.status(HttpStatus.OK).body("Task deletada com sucesso!");
    }
}