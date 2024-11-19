package org.example.expert.service

import org.example.expert.controller.manager.dto.request.ManagerCreateReqDto
import org.example.expert.controller.manager.dto.response.ManagerCreateRespDto
import org.example.expert.controller.manager.dto.response.ManagerInfoRespDto
import org.example.expert.domain.manager.Manager
import org.example.expert.domain.manager.ManagerRepository
import org.example.expert.domain.todo.Todo
import org.example.expert.domain.user.User
import org.example.expert.domain.user.UserRepository
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrElse

@Service
@Transactional(readOnly = true)
class ManagerService(
    private val userRepository: UserRepository,
    private val todoService: TodoService,
    private val managerRepository: ManagerRepository
) {

    @Transactional
    fun saveManager(user: User, todoId: Long, managerCreateReqDto: ManagerCreateReqDto): ManagerCreateRespDto {
        val targetUser = userRepository.findById(managerCreateReqDto.targetUserId)
            .getOrElse { throw CustomApiException(ErrorCode.TARGET_USER_NOT_FOUND) }

        val todo = todoService.findByIdOrFail(todoId)
        todo.isOwner(user)
        validateManagerAssignment(targetUser, todo)

        return ManagerCreateRespDto(
            managerRepository.save(managerCreateReqDto.toEntity(targetUser, todo))
        )
    }

    private fun validateManagerAssignment(targetUser: User, todo: Todo) {
        managerRepository.findByUserAndTodo(targetUser, todo)?.let {
            throw CustomApiException(ErrorCode.ALREADY_ASSIGNED_USER)
        }
        todo.validateManagerAssignment(targetUser)
    }

    fun getManagerList(todoId: Long): List<ManagerInfoRespDto> {
        val todo = todoService.findByIdOrFail(todoId)
        return managerRepository.findByTodoWithUser(todo)
            .map(::ManagerInfoRespDto)
    }

    @Transactional
    fun deleteManager(user: User, todoId: Long, managerId: Long){
        val todo = todoService.findByIdOrFail(todoId)
        todo.isOwner(user)

        val manager = findByIdOrFail(managerId)
        manager.isAssigned(todoId)

        managerRepository.delete(manager)
    }

    fun findByIdOrFail(managerId: Long): Manager = managerRepository
        .findById(managerId)
        .getOrElse{ throw CustomApiException(ErrorCode.MANAGER_NOT_FOUND) }
}