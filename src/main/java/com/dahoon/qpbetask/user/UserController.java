package com.dahoon.qpbetask.user;

import com.dahoon.qpbetask.user.dto.JwtTokenDto;
import com.dahoon.qpbetask.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "사용자 등록", description = "사용자이름, 비밀번호를 입력받고 사용자를 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "등록 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "이미 등록된 사용자 이름입니다.")
    })
    public ResponseEntity<UserDto> join(@RequestBody @Valid UserDto userDto) {
        log.info("사용자 등록 컨트롤러 - 사용자 이름 : {}", userDto.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.join(userDto));
    }

    @PostMapping("/token")
    @Operation(summary = "사용자 로그인", description = "로그인 후 JWT 토큰 생성 및 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "로그인 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 형식의 입력," +
                    "사용자 이름은 공백일 수 없음" +
                    "비밀번호는 8자 이상, 숫자 및 특수문자(@$!%*?&#) 포함, 영어,숫자,특수문자만 가능"),
            @ApiResponse(responseCode = "401", description = "비밀번호 틀림"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자")
    })
    public ResponseEntity<JwtTokenDto> login(@RequestBody @Valid UserDto userDto) {
        log.info("로그인 컨트롤러 - 사용자 이름 : {}", userDto.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.login(userDto));
    }

    @PostMapping("/token/refresh")
    @Operation(summary = "토큰 재발급", description = "AccessToken이 만료되었을 때, UX를 위해 RefreshToken 검증 후 토큰 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "재발급 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "토큰을 입력하세요"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 RefreshToken")
    })
    public ResponseEntity<JwtTokenDto> refreshToken(@RequestBody @Valid JwtTokenDto jwtTokenDto) {
        log.info("토큰 재발급 컨트롤러 - RFToken : {}", jwtTokenDto.getRefreshToken());

        return ResponseEntity.ok(userService.refreshToken(jwtTokenDto));
    }

    @DeleteMapping("/token")
    @Operation(summary = "로그아웃", description = "사용자의 토큰 제거, RefreshToken을 서버에서 관리하기 때문에 구현함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401")
    })
    public ResponseEntity<String> logout(@AuthenticationPrincipal String username) {
        log.info("로그아웃 컨트롤러 - 사용자 이름 : {}", username);

        userService.logout(username);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "모든 사용자 조회", description = "모든 사용자 정보를 리스트로 반환")
    @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공")
    public ResponseEntity<List<UserDto>> showUserList() {
        log.info("사용자 목록 조회 컨트롤러");

        return ResponseEntity.ok(userService.showUserList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "특정 사용자 조회", description = "Id로 사용자 정보 조회 후 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404")
    })
    public ResponseEntity<UserDto> showUser(
            @Parameter(description = "사용자 ID", example = "1", in = ParameterIn.PATH)
            @PathVariable(value = "id")
            @Min(1) @NotNull(message = "사용자 ID를 입력하세요") Long userId) {
        log.info("특정 사용자 조회 컨트롤러 - ID : {}", userId);

        return ResponseEntity.ok(userService.showUser(userId));
    }
}
