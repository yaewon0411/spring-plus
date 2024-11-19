package org.example.expert.exception

class CustomApiException(
    val errorCode: ErrorCode
): RuntimeException(errorCode.message) {

}