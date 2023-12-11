package com.example.market.exception;

import com.example.market.api.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    INVALID_WRITER(ResultCode.UNAUTHORIZED, "작성자 정보가 일치하지 않습니다."),
    NOT_FOUND_USER(ResultCode.NOT_FOUND, "존재하지 않는 회원입니다."),

    NOT_MATCH_ITEM_AND_COMMENT(ResultCode.INVALID_ARGUMENT, "아이템 번호와 댓글 번호가 일치하지 않습니다."),
    NOT_MATCH_ITEM_STATUS_SOLD(ResultCode.INVALID_ARGUMENT, "판매 완료된 상품이 아닙니다."),
    NOT_FOUND_ITEM(ResultCode.NOT_FOUND, "존재하지 않는 아이템입니다."),
    NOT_FOUND_COMMENT(ResultCode.NOT_FOUND, "존재하지 않는 댓글입니다."),
    NOT_FOUND_NEGOTIATION(ResultCode.NOT_FOUND, "존재하지 않는 네고입니다."),
    NOT_FOUND_BUY(ResultCode.NOT_FOUND, "구매 내역이 존재하지 않습니다."),
    SERVER_ERROR(ResultCode.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다."),

    ALREADY_USER_USERNAME(ResultCode.CONFLICT, "이미 존재하는 회원입니다."),
    ALREADY_USER_NEGOTIATION(ResultCode.CONFLICT, "이미 제안을 요청했습니다."),
    ALREADY_ITEM_SOLD(ResultCode.CONFLICT, "이미 판매된 상품입니다."),
    ALREADY_REVIEW(ResultCode.CONFLICT, "이미 리뷰가 존재합니다."),

    CANNOT_NEGOTIATION_OWN_ITEM(ResultCode.FORBIDDEN, "본인 상품에는 제안을 할 수 없습니다."),

    NOT_FOUND_COORDINATE(ResultCode.INVALID_ARGUMENT, "좌표값이 제대로 입력되지 않았습니다.");

    private ResultCode resultCode;
    private String message;

}
