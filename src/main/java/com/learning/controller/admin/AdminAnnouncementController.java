package com.learning.controller.admin;

import com.learning.common.ResponseResult;
import com.learning.entity.Announcement;
import com.learning.entity.Notification;
import com.learning.entity.User;
import com.learning.entity.Wish;
import com.learning.entity.Feedback;
import com.learning.mapper.AnnouncementMapper;
import com.learning.mapper.WishMapper;
import com.learning.mapper.FeedbackMapper;
import com.learning.mapper.UserMapper;
import com.learning.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员公告/期待/反馈控制器
 */
@RestController
@RequestMapping("/api/admin/announcements")
public class AdminAnnouncementController {

    @Autowired
    private AnnouncementMapper announcementMapper;
    @Autowired
    private WishMapper wishMapper;
    @Autowired
    private FeedbackMapper feedbackMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;

    // ==================== 公告管理 ====================

    @GetMapping
    public ResponseResult<Object> listAnnouncements(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status) {
        List<Announcement> list = announcementMapper.findAdminList(keyword, type, status);
        Integer total = announcementMapper.countAdminList(keyword, type, status);
        return ResponseResult.success(Map.of("list", list, "total", total));
    }

    @GetMapping("/{id}")
    public ResponseResult<Object> getAnnouncement(@PathVariable Long id) {
        Announcement a = announcementMapper.findById(id);
        if (a == null) return ResponseResult.error(404, "公告不存在");
        return ResponseResult.success(a);
    }

    @PostMapping
    public ResponseResult<Object> createAnnouncement(@RequestBody Announcement announcement) {
        announcementMapper.insert(announcement);

        if (announcement.getStatus() != null && announcement.getStatus() == 1) {
            sendAnnouncementNotification(announcement);
        }

        return ResponseResult.success("创建成功", announcement);
    }

    private void sendAnnouncementNotification(Announcement announcement) {
        List<User> users = userMapper.findAll();
        if (users == null || users.isEmpty()) return;

        String typeLabel = getTypeLabel(announcement.getType());
        for (User user : users) {
            Notification notification = new Notification();
            notification.setUserId(user.getId());
            notification.setType("NEW_ANNOUNCEMENT");
            notification.setTitle("📢 新" + typeLabel + "发布");
            notification.setContent(announcement.getTitle());
            notification.setRelatedType("ANNOUNCEMENT");
            notification.setRelatedId(announcement.getId());
            notification.setIsRead(0);
            notificationMapper.insert(notification);
        }
    }

    private String getTypeLabel(String type) {
        if (type == null) return "公告";
        switch (type) {
            case "announcement": return "课程预告";
            case "feature": return "功能更新";
            case "notice": return "系统通知";
            default: return "公告";
        }
    }

    @PutMapping("/{id}")
    public ResponseResult<Object> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        Announcement existing = announcementMapper.findById(id);
        if (existing == null) return ResponseResult.error(404, "公告不存在");

        announcement.setId(id);
        announcementMapper.update(announcement);

        boolean wasDraft = (existing.getStatus() == null || existing.getStatus() == 0);
        boolean isNowPublished = (announcement.getStatus() != null && announcement.getStatus() == 1);
        if (wasDraft && isNowPublished) {
            sendAnnouncementNotification(announcement);
        }

        return ResponseResult.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Object> deleteAnnouncement(@PathVariable Long id) {
        announcementMapper.delete(id);
        return ResponseResult.success("删除成功", null);
    }

    @PostMapping("/{id}/pin")
    public ResponseResult<Object> togglePin(@PathVariable Long id) {
        Announcement a = announcementMapper.findById(id);
        if (a == null) return ResponseResult.error(404, "公告不存在");

        Integer newPinStatus = a.getIsPinned() != null && a.getIsPinned() == 1 ? 0 : 1;
        announcementMapper.updatePinned(id, newPinStatus);
        return ResponseResult.success(newPinStatus == 1 ? "已置顶" : "已取消置顶", null);
    }

    // ==================== 用户期待管理 ====================

    @GetMapping("/wishes")
    public ResponseResult<Object> listWishes(@RequestParam(required = false) Integer status) {
        List<Wish> list = wishMapper.findAdminList(status);
        Integer pending = wishMapper.countPending();
        return ResponseResult.success(Map.of("list", list, "pendingCount", pending));
    }

    @PutMapping("/wishes/{id}")
    public ResponseResult<Object> updateWish(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer status = (Integer) body.get("status");
        String adminReply = (String) body.get("adminReply");
        wishMapper.updateStatus(id, status, adminReply);
        return ResponseResult.success("更新成功", null);
    }

    @DeleteMapping("/wishes/{id}")
    public ResponseResult<Object> deleteWish(@PathVariable Long id) {
        wishMapper.delete(id);
        return ResponseResult.success("删除成功", null);
    }

    // ==================== 反馈管理 ====================

    @GetMapping("/feedback")
    public ResponseResult<Object> listFeedback(@RequestParam(required = false) String status) {
        List<Feedback> list = feedbackMapper.findAll(status);
        return ResponseResult.success(list);
    }

    @PutMapping("/feedback/{id}")
    public ResponseResult<Object> replyFeedback(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String reply = body.get("reply");
        String status = body.get("status");
        if (status == null || status.isEmpty()) {
            status = "RESOLVED";
        }
        feedbackMapper.updateReply(id, reply, status);

        Feedback feedback = feedbackMapper.findById(id);
        if (feedback != null && feedback.getUserId() != null && reply != null && !reply.isEmpty()) {
            Notification notification = new Notification();
            notification.setUserId(feedback.getUserId());
            notification.setType("FEEDBACK_REPLY");
            notification.setTitle("📬 您的反馈已收到回复");
            notification.setContent(reply.length() > 100 ? reply.substring(0, 100) + "..." : reply);
            notification.setRelatedType("FEEDBACK");
            notification.setRelatedId(id);
            notification.setIsRead(0);
            notificationMapper.insert(notification);
        }

        return ResponseResult.success("已回复", null);
    }

    @DeleteMapping("/feedback/{id}")
    public ResponseResult<Object> deleteFeedback(@PathVariable Long id) {
        feedbackMapper.delete(id);
        return ResponseResult.success("删除成功", null);
    }
}
