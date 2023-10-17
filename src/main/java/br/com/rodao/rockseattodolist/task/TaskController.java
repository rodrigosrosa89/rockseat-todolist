package br.com.rodao.rockseattodolist.task;

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
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        LocalDateTime currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data inicio/data fim deve ser maior que a data atual");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data inicio deve ser menor que a data término");
        }

        TaskModel taskCriada = repository.save(taskModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskCriada);
    }

    @GetMapping("/")
    public List<TaskModel> listTasksFromIdUser(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        List<TaskModel> listTasks = repository.findByIdUser((UUID) idUser);
        return listTasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity updateTask(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);
        taskModel.setId(id);
        return ResponseEntity.status(200).body(repository.save(taskModel));
    }

    @PatchMapping("/{id}")
    public ResponseEntity patchTask(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);
        taskModel.setId(id);
        return ResponseEntity.status(200).body(repository.save(taskModel));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity deleteTask(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);
        taskModel.setId(id);
        repository.delete(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body("Task deletada com sucesso!");
    }
}