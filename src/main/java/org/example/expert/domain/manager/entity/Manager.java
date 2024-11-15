//package org.example.expert.domain.manager.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.example.expert.domain.common.exception.InvalidRequestException;
//import org.example.expert.domain.todo.entity.Todo;
//import org.example.expert.domain.user.entity.User;
//import org.springframework.util.ObjectUtils;
//
//@Getter
//@Entity
//@NoArgsConstructor
//@Table(name = "managers")
//public class Manager {
//
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false) // 일정에 배정된 유저 (일정 생성자는 일정 생성 시 cascade에 따라 자동 배정)
//    private User user;
//    @ManyToOne(fetch = FetchType.LAZY) // 일정 id
//    @JoinColumn(name = "todo_id", nullable = false)
//    private Todo todo;
//
//    public Manager(User user, Todo todo) {
//        this.user = user;
//        this.todo = todo;
//    }
//
//    public void isAssigned(Long todoId){
//        if (!ObjectUtils.nullSafeEquals(todoId, this.getTodo().getId())) {
//            throw new InvalidRequestException("해당 일정에 등록된 담당자가 아닙니다.");
//        }
//    }
//}
