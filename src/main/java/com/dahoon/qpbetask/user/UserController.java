package com.dahoon.qpbetask.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "사용자 등록", description = "사용자이름, 비밀번호를 입력받고 사용자를 등록 한 후 JWT 토큰 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "등록 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "이미 등록된 사용자 이름입니다.")
    })
    public ResponseEntity<UserDto> join(@RequestBody @Valid UserDto userDto) {
        log.info("사용자 등록 컨트롤러 - 사용자 이름 : {}", userDto.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.join(userDto));
    }
}
