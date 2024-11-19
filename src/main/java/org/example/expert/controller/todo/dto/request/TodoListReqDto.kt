package org.example.expert.controller.todo.dto.request

import com.sun.jna.platform.win32.OaIdl.DATE
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class TodoListReqDto(
    @field: Min(value = 0, message = "0 이상의 정수를 입력해야 합니다")
    var page: Int = 0,

    @field: Min(value = 1, message = "1 이상의 정수를 입력해야 합니다")
    var size: Int = 10,

    val weather: String? = "",

    @field: Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다")
    val startModifiedAt: String? = null,

    @field: Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다")
    val endModifiedAt: String? = null
) {
    companion object{
        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }

    val startDateTime: LocalDateTime?
        get() = startModifiedAt?.let{ LocalDateTime.parse("$it 00:00:00", DATE_TIME_FORMATTER) }
    val endDateTime: LocalDateTime?
        get() = endModifiedAt?.let{ LocalDateTime.parse("$it 23:59:59", DATE_TIME_FORMATTER) }
}