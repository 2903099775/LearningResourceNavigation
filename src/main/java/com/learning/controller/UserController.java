package com.learning.controller;

import com.learning.common.ResponseResult;
import com.learning.config.FileUploadConfig;
import com.learning.entity.Payment;
import com.learning.entity.RefundRequest;
import com.learning.entity.User;
import com.learning.mapper.PaymentMapper;
import com.learning.mapper.RefundRequestMapper;
import com.learning.mapper.UserMapper;
import com.learning.service.UserService;
import com.learning.service.VerificationCodeService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

/**
 * 用户控制器
 * 负责处理用户相关的API请求，包括获取用户信息、更新用户信息、VIP充值等
 * 引用文件：c:/Users/29030/Documents/trae_projects/src/main/java/com/learning/common/ResponseResult.java, c:/Users/29030/Documents/trae_projects/src/main/java/com/learning/entity/User.java, c:/Users/29030/Documents/trae_projects/src/main/java/com/learning/service/UserService.java
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private RefundRequestMapper refundRequestMapper;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private FileUploadConfig fileUploadConfig;

    @PostConstruct
    public void init() {
        fileUploadConfig.init();
    }

    @GetMapping("/profile")
    public ResponseResult<Object> getProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseResult.error(401, "用户未登录");
        }
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseResult.error(401, "用户不存在，请重新登录");
        }
        user.setPassword(null);
        return ResponseResult.success(user);
    }

    @GetMapping("/{id}")
    public ResponseResult<Object> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseResult.success(user);
    }

    @PutMapping
    public ResponseResult<String> updateUser(Authentication authentication, @RequestBody User user) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseResult.error(401, "用户未登录");
        }
        
        String username = authentication.getName();
        User existingUser = userService.findByUsername(username);
        if (existingUser == null) {
            return ResponseResult.error(401, "用户不存在，请重新登录");
        }

        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            if (!user.getPhone().equals(existingUser.getPhone())) {
                User userByPhone = userMapper.findByPhone(user.getPhone());
                if (userByPhone != null && !userByPhone.getId().equals(existingUser.getId())) {
                    return ResponseResult.error(400, "该手机号已被其他用户使用");
                }
            }
        }
        
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setAvatar(user.getAvatar());

        userService.update(existingUser);
        return ResponseResult.success("用户信息更新成功");
    }

    @PostMapping("/verify-password")
    public ResponseResult<Object> verifyPassword(Authentication authentication, @RequestBody Map<String, String> body) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseResult.error(401, "用户未登录");
        }

        String username = authentication.getName();
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return ResponseResult.error(401, "用户不存在，请重新登录");
        }

        String password = body.get("password");
        if (password == null || password.isEmpty()) {
            return ResponseResult.error(400, "密码不能为空");
        }

        boolean isValid = userService.verifyPassword(password, user.getPassword());
        if (isValid) {
            return ResponseResult.success("密码验证成功");
        } else {
            return ResponseResult.error(401, "密码错误");
        }
    }

    @PostMapping("/send-verification-code")
    public ResponseResult<Object> sendVerificationCode(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String phone = body.get("phone");
            if (phone == null || phone.isEmpty()) {
                return ResponseResult.error(400, "手机号不能为空");
            }

            if (!phone.matches("^1[3-9]\\d{9}$")) {
                return ResponseResult.error(400, "手机号格式不正确");
            }

            User user = userMapper.findByPhone(phone);
            if (user == null) {
                return ResponseResult.error(404, "该手机号未注册");
            }

            if (username != null && !username.equals(user.getUsername())) {
                return ResponseResult.error(400, "用户名与手机号不匹配");
            }

            String code = verificationCodeService.generateCode(phone);
            logger.info("Generated verification code for phone: {}", phone);
            return ResponseResult.success(code, "验证码：" + code);
        } catch (Exception e) {
            logger.error("Error in sendVerificationCode", e);
            return ResponseResult.error(500, "发送验证码失败，请稍后重试");
        }
    }

    @PostMapping("/change-password")
    public ResponseResult<Object> changePassword(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String phone = body.get("phone");
        String verificationCode = body.get("verificationCode");
        String newPassword = body.get("newPassword");

        if (phone == null || phone.isEmpty()) {
            return ResponseResult.error(400, "手机号不能为空");
        }
        if (verificationCode == null || verificationCode.isEmpty()) {
            return ResponseResult.error(400, "验证码不能为空");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseResult.error(400, "新密码不能为空");
        }
        if (newPassword.length() < 6) {
            return ResponseResult.error(400, "新密码长度不能少于6位");
        }

        User user = userMapper.findByPhone(phone);
        if (user == null) {
            return ResponseResult.error(404, "该手机号未注册");
        }

        if (username != null && !username.equals(user.getUsername())) {
            return ResponseResult.error(400, "用户名与手机号不匹配");
        }

        if (!verificationCodeService.verifyCode(phone, verificationCode)) {
            return ResponseResult.error(400, "验证码错误或已过期");
        }

        user.setPassword(newPassword);
        userMapper.update(user);

        return ResponseResult.success("密码修改成功");
    }

    @GetMapping("/username/{username}")
    public ResponseResult<Object> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return ResponseResult.success(user);
    }

    @GetMapping("/email/{email}")
    public ResponseResult<Object> getUserByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        return ResponseResult.success(user);
    }

    /**
     * VIP充值接口
     * 根据选择的套餐月数，计算新的VIP到期日期并更新数据库
     * @param authentication 当前登录用户认证信息
     * @param body 包含 months（充值月数）和 planName（套餐名称）的请求体
     * @return 充值结果，包含新的VIP到期日期
     */
    @PostMapping("/vip/upgrade")
    public ResponseResult<Object> upgradeVip(Authentication authentication, @RequestBody Map<String, Object> body) {
        // 验证用户是否已登录
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseResult.error(401, "请先登录后再充值");
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseResult.error(401, "用户不存在，请重新登录");
        }

        // 获取充值月数参数
        Object monthsObj = body.get("months");
        if (monthsObj == null) {
            return ResponseResult.error(400, "缺少充值月数参数");
        }

        int months;
        try {
            months = ((Number) monthsObj).intValue();
        } catch (Exception e) {
            return ResponseResult.error(400, "充值月数参数格式错误");
        }

        if (months <= 0 || months > 36) {
            return ResponseResult.error(400, "充值月数需在1-36之间");
        }

        // 计算新的VIP到期日期：在当前到期日或今天（取较晚者）基础上加上充值月数
        LocalDate baseDate = LocalDate.now();
        if (user.getVipExpireDate() != null && user.getVipExpireDate().isAfter(baseDate)) {
            baseDate = user.getVipExpireDate();
        }
        LocalDate newExpireDate = baseDate.plusMonths(months);

        // 更新用户VIP到期日期
        user.setVipExpireDate(newExpireDate);
        userService.update(user);

        // 创建支付订单记录
        Payment payment = new Payment();
        payment.setUserId(user.getId());
        payment.setOrderNo("VIP" + System.currentTimeMillis());
        payment.setAmount(new BigDecimal(String.valueOf(months * 9.9))); // 假设每月9.9元
        payment.setMonths(months);
        payment.setStatus("SUCCESS");
        payment.setPayTime(LocalDateTime.now());
        payment.setExpireDate(newExpireDate);
        payment.setCreatedAt(LocalDateTime.now());
        paymentMapper.insert(payment);

        // 返回更新后的用户信息（隐藏密码）
        // 注意：不能直接改缓存对象，需要重新查库
        User updatedUser = userService.findByUsername(username);
        updatedUser.setPassword(null);
        return ResponseResult.success("充值成功，VIP有效期至 " + newExpireDate, updatedUser);
    }

    @PostMapping("/vip/refund")
    public ResponseResult<Object> refundVip(Authentication authentication, @RequestBody Map<String, String> body) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseResult.error(401, "请先登录后再申请退款");
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseResult.error(401, "用户不存在，请重新登录");
        }

        if (user.getVipExpireDate() == null || !user.getVipExpireDate().isAfter(LocalDate.now())) {
            return ResponseResult.error(400, "您当前不是VIP会员，无法申请退款");
        }

        Payment latestPayment = paymentMapper.findLatestSuccessByUserId(user.getId());
        if (latestPayment == null) {
            return ResponseResult.error(400, "未找到有效的支付记录，无法申请退款");
        }

        long daysSincePayment = ChronoUnit.DAYS.between(latestPayment.getCreatedAt().toLocalDate(), LocalDate.now());
        if (daysSincePayment > 7) {
            return ResponseResult.error(400, "退款申请需在开通后7天内提交，您的开通时间已超过7天");
        }

        String reason = body.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            return ResponseResult.error(400, "请填写退款原因");
        }

        // 创建退款申请记录
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setUserId(user.getId());
        refundRequest.setOrderNo(latestPayment.getOrderNo());
        refundRequest.setReason(reason);
        refundRequest.setStatus("PENDING");
        refundRequest.setCreatedAt(LocalDateTime.now());
        refundRequestMapper.insert(refundRequest);

        logger.info("用户 {} 提交VIP退款申请，订单号: {}, 原因: {}", username, latestPayment.getOrderNo(), reason);

        return ResponseResult.success("退款申请已提交，请等待管理员审核", null);
    }

    @PostMapping("/avatar")
    public ResponseResult<Object> uploadAvatar(Authentication authentication, @RequestParam("file") MultipartFile file) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseResult.error(401, "用户未登录");
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseResult.error(401, "用户不存在，请重新登录");
        }

        if (file.isEmpty()) {
            return ResponseResult.error(400, "上传文件不能为空");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseResult.error(400, "文件大小不能超过10MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return ResponseResult.error(400, "文件名不能为空");
        }

        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        if (!isAllowedExtension(fileExtension)) {
            return ResponseResult.error(400, "不支持的文件格式，仅支持 jpg, jpeg, png, gif, webp");
        }

        try {
            String uploadPath = fileUploadConfig.getAvatarUploadPath();
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String newFileName = UUID.randomUUID().toString() + fileExtension;
            Path filePath = Paths.get(uploadPath, newFileName);

            Files.copy(file.getInputStream(), filePath);

            String avatarUrl = "/avatars/" + newFileName;
            user.setAvatar(avatarUrl);
            userService.update(user);

            logger.info("用户 {} 上传头像成功: {}", username, avatarUrl);

            User updatedUser = userService.findByUsername(username);
            updatedUser.setPassword(null);

            return ResponseResult.success("头像上传成功", updatedUser);
        } catch (IOException e) {
            logger.error("头像上传失败", e);
            return ResponseResult.error(500, "头像上传失败: " + e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }

    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}