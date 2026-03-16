package com.aurora.aurora_was.member.dto.req;

public record SignupReq(
        String email,
        String password,
        String name,
        String phone
) {
}
