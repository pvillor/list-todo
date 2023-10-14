package br.com.villo.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.villo.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  
  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity create(@RequestBody TaskModel taskData, HttpServletRequest request) {
    var idUser = request.getAttribute("idUser");
    taskData.setIdUser((UUID) idUser);

    var currentDate = LocalDateTime.now();

    if(currentDate.isAfter(taskData.getStartsAt()) || currentDate.isAfter(taskData.getEndsAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início/término deve ser maior do que a data atual.");
    }
    
    if(taskData.getStartsAt().isAfter(taskData.getEndsAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início deve ser menor do que a data de término.");
    }

    var task = this.taskRepository.save(taskData);
    return ResponseEntity.status(HttpStatus.OK).body(task);
  }

  @GetMapping("/")
  public List<TaskModel> list(HttpServletRequest request) {
    var idUser = request.getAttribute("idUser");
    var tasks = this.taskRepository.findByIdUser((UUID) idUser);

    return tasks;
  }

  @PutMapping("/{id}")
  public ResponseEntity update(@RequestBody TaskModel taskData, HttpServletRequest request, @PathVariable UUID id) {

    var task = this.taskRepository.findById(id).orElse(null);

    if(task == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada.");
    }

    var idUser = request.getAttribute("idUser");

    if(!task.getIdUser().equals(idUser)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não tem permissão.");
    }

    Utils.copyNonNullProperties(taskData, task);
    var taskUpdated = this.taskRepository.save(task);

    return ResponseEntity.status(HttpStatus.OK).body(taskUpdated);
  }
}
