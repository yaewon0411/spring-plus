package org.example.expert.controller.manager

import jakarta.validation.Valid
import org.example.expert.config.security.loginuser.LoginUser
import org.example.expert.controller.manager.dto.request.ManagerCreateReqDto
import org.example.expert.controller.manager.dto.response.ManagerCreateRespDto
import org.example.expert.controller.manager.dto.response.ManagerInfoRespDto
import org.example.expert.service.ManagerService
import org.example.expert.util.api.ApiResult
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/todos")
class ManagerController(
    private val managerService: ManagerService
) {

    @PostMapping("/{todoId}/managers")
    fun saveManager(@Valid @RequestBody managerSaveReqDto: ManagerCreateReqDto,
                    @PathVariable(value = "todoId") todoId: Long,
                    @AuthenticationPrincipal loginUser: LoginUser)
    : ResponseEntity<ApiResult<ManagerCreateRespDto>> =
        ResponseEntity.ok(ApiResult.success(managerService.saveManager(loginUser.user, todoId, managerSaveReqDto)))


    @GetMapping("/{todoId}/managers")
    fun getManagerList(@PathVariable(value = "todoId")todoId: Long)
    : ResponseEntity<ApiResult<List<ManagerInfoRespDto>>> =
        ResponseEntity.ok(ApiResult.success(managerService.getManagerList(todoId)))


    @DeleteMapping("/{todoId}/managers/{managerId}")
    fun deleteManager(@PathVariable(value = "todoId")todoId: Long,
                      @PathVariable(value = "managerId")managerId: Long,
                      @AuthenticationPrincipal loginUser: LoginUser)
    : ResponseEntity<ApiResult<String>> {
        managerService.deleteManager(loginUser.user, todoId, managerId)
        return ResponseEntity.ok(ApiResult.success("해당 담당자가 성공적으로 삭제되었습니다"))
    }

}