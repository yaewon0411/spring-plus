package org.example.expert.aop.dto

import org.example.expert.controller.manager.dto.request.ManagerCreateReqDto
import org.example.expert.domain.log.manager.ManagerLogMessage
import org.example.expert.domain.user.User
import org.example.expert.exception.LogContextValidationException
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class ManagerLogContext private constructor(
    val user: User,
    val todoId: Long,
    val request: ManagerCreateReqDto
) {
    companion object{
        private const val ARGS_COUNT = 3
        private const val USER_POSITION = 0
        private const val TODO_ID_POSITION = 1
        private const val REQUEST_POSITION = 2
        private val log = LoggerFactory.getLogger(this::class.java)

        fun validateAndCreate(args: Array<Any>): ManagerLogContext{
            validateArgs(args)
            return ManagerLogContext(
                user = args[USER_POSITION] as User,
                todoId = args[TODO_ID_POSITION] as Long,
                request = args[REQUEST_POSITION] as ManagerCreateReqDto
            )
        }

        private fun validateArgs(args: Array<Any>){
            validateArgsCount(args)
            validateArgsType(args)
        }

        private fun validateArgsCount(args: Array<Any>){
            if(args.size != ARGS_COUNT){
                LogContextValidationException.InvalidManagerContextArgsCount(
                    required = ARGS_COUNT,
                    actual = args.size
                ).also {e ->
                    log.error("ManagerLogContext 생성 실패: {}", e.message)
                    throw e
                }
            }
        }

        private fun validateArgsType(args: Array<Any>){

            fun validateType(
                value: Any,
                position: Int,
                expectedType: KClass<*>
            ){
                if(!expectedType.isInstance(value)){
                    LogContextValidationException.InvalidManagerContextArgumentType(
                        position = position,
                        expectedType = expectedType.simpleName ?: expectedType.toString(),
                        actualType = value::class.simpleName ?: value::class.toString()
                    ).also {e ->
                        log.error("ManagerLogContext 생성 실패: {}", e.message)
                        throw e
                    }
                }
            }

            with(args){
                validateType(get(USER_POSITION), USER_POSITION, User::class)
                validateType(get(TODO_ID_POSITION), TODO_ID_POSITION, Long::class)
                validateType(get(REQUEST_POSITION), REQUEST_POSITION, ManagerCreateReqDto::class)
            }
        }

    }

}