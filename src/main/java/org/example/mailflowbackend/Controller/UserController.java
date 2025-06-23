package org.example.mailflowbackend.Controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.example.mailflowbackend.Dto.ProfileResponeDto;
import org.example.mailflowbackend.Entity.Users;
import org.example.mailflowbackend.Service.Imp.UserServiceImp;
import org.example.mailflowbackend.payload.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserServiceImp userServiceImp;

    @GetMapping("/my-profile")
    public ResponseEntity<ApiResponse<ProfileResponeDto>> myProfile(@AuthenticationPrincipal Users user){
        try {
            ProfileResponeDto profileResponeDto = userServiceImp.getProfile(user.getEmail());
            return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin thành công", profileResponeDto));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(400, "Lấy thông tin thành công", null));
        }
    }

    @PutMapping(value = "/update-profile", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<?>> updateProfile(@RequestPart("fullName") String fullName,
                                                        @RequestPart("phone") String phone,
                                                        @Parameter(description = "Avatar upload", schema = @Schema(type = "string", format = "binary"))
                                                            @RequestPart(value = "avatar", required = false) MultipartFile avatar,
                                                        @AuthenticationPrincipal Users users) {
        try {
            userServiceImp.updateUsers(users.getEmail(), fullName, phone, avatar);
            return ResponseEntity.ok(new ApiResponse<>(200, " Cập nhật thông tin thành công", null));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse<>(400, " Cập nhật thông tin thất bại", null));
        }

    }

    @PatchMapping(value = "/change-pass", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<?>> changePass(@RequestPart("Mật khẩu cũ") String oldPassword,
                                                     @RequestPart("Mật khẩu mới") String newPassword,
                                                     @AuthenticationPrincipal Users users) {
        try {
            userServiceImp.changePass(users.getEmail(),oldPassword ,newPassword);
            return ResponseEntity.ok(new ApiResponse<>(200,"Đổi mật khẩu thành công", null));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse<>(400,"Đổi mật khẩu thất bại", null));
        }
    }
}
