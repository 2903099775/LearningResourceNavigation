package com.learning.service.impl;

import com.learning.common.ResponseResult;
import com.learning.entity.Comment;
import com.learning.entity.User;
import com.learning.mapper.CommentMapper;
import com.learning.mapper.LearningPathMapper;
import com.learning.mapper.PostMapper;
import com.learning.mapper.UserMapper;
import com.learning.service.AchievementService;
import com.learning.service.CommentService;
import com.learning.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论服务实现类
 * 负责处理评论的创建、获取、点赞和审核等方法的具体实现
 * 引用文件：com.learning.common.ResponseResult, com.learning.entity.Comment, com.learning.entity.User, com.learning.mapper.CommentMapper, com.learning.mapper.UserMapper, com.learning.service.CommentService
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private LearningPathMapper learningPathMapper;

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public ResponseResult<String> createComment(Comment comment) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        if (!isVip(userId)) {
            return ResponseResult.error("VIP会员才可以使用评论功能，请先开通VIP");
        }
        comment.setUserId(userId);
        comment.setStatus(1);
        commentMapper.insert(comment);

        // 更新帖子评论数
        if ("post".equals(comment.getTargetType()) && comment.getTargetId() != null) {
            postMapper.updateCommentsCount(comment.getTargetId(), 1);
        }

        // 发送评论相关通知
        sendCommentNotifications(userId, comment);

        achievementService.checkAndUnlockAchievements(userId);

        return ResponseResult.success("评论创建成功");
    }

    @Override
    public ResponseResult<Object> getComments(String type, Long id) {
        List<Comment> allComments = commentMapper.findByTarget(type, id);
        
        // 为每个评论添加用户信息
        for (Comment comment : allComments) {
            User user = userMapper.findById(comment.getUserId());
            if (user != null) {
                comment.setUsername(user.getUsername());
            } else {
                comment.setUsername("未知用户");
            }
        }
        
        // 构建评论树结构
        List<Comment> rootComments = new ArrayList<>();
        Map<Long, Comment> commentMap = new HashMap<>();
        
        // 首先将所有评论放入map
        for (Comment comment : allComments) {
            commentMap.put(comment.getId(), comment);
            comment.setReplies(new ArrayList<>()); // 初始化回复列表
        }
        
        // 构建树形结构
        for (Comment comment : allComments) {
            if (comment.getParentId() == null || comment.getParentId() == 0) {
                // 根评论
                rootComments.add(comment);
            } else {
                // 回复评论
                Comment parentComment = commentMap.get(comment.getParentId());
                if (parentComment != null) {
                    parentComment.getReplies().add(comment);
                } else {
                    // 如果父评论不存在，作为根评论处理
                    rootComments.add(comment);
                }
            }
        }
        
        // 按置顶和时间排序根评论
        rootComments.sort((a, b) -> {
            if (a.getIsTop() != null && b.getIsTop() != null) {
                if (!a.getIsTop().equals(b.getIsTop())) {
                    return b.getIsTop() - a.getIsTop(); // 置顶优先
                }
            }
            return b.getCreatedAt().compareTo(a.getCreatedAt()); // 时间倒序
        });
        
        return ResponseResult.success(rootComments);
    }

    @Override
    public ResponseResult<String> likeComment(Long id) {
        Comment comment = commentMapper.findById(id);
        if (comment == null) {
            return ResponseResult.error("评论不存在");
        }

        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return ResponseResult.error("请先登录");
        }

        // 更新点赞数
        commentMapper.updateLikesCount(id);

        // 发送点赞通知（给评论作者）
        sendLikeNotification(currentUserId, comment);

        return ResponseResult.success("点赞成功");
    }

    @Override
    public ResponseResult<Object> getAllComments() {
        List<Comment> comments = commentMapper.findAll();
        
        // 为每个评论添加用户信息
        for (Comment comment : comments) {
            User user = userMapper.findById(comment.getUserId());
            if (user != null) {
                comment.setUsername(user.getUsername());
            } else {
                comment.setUsername("未知用户");
            }
        }
        
        return ResponseResult.success(comments);
    }

    @Override
    public ResponseResult<Object> getCommentsByStatus(Integer status) {
        List<Comment> comments = commentMapper.findByStatus(status);
        
        // 为每个评论添加用户信息
        for (Comment comment : comments) {
            User user = userMapper.findById(comment.getUserId());
            if (user != null) {
                comment.setUsername(user.getUsername());
            } else {
                comment.setUsername("未知用户");
            }
        }
        
        return ResponseResult.success(comments);
    }

    @Override
    public ResponseResult<String> approveComment(Long id) {
        Comment comment = commentMapper.findById(id);
        if (comment == null) {
            return ResponseResult.error("评论不存在");
        }
        commentMapper.updateStatus(id, 1);
        return ResponseResult.success("评论已批准");
    }

    @Override
    public ResponseResult<String> rejectComment(Long id) {
        Comment comment = commentMapper.findById(id);
        if (comment == null) {
            return ResponseResult.error("评论不存在");
        }
        commentMapper.updateStatus(id, 2);
        return ResponseResult.success("评论已拒绝");
    }

    @Override
    public ResponseResult<String> deleteComment(Long id) {
        Comment comment = commentMapper.findById(id);
        if (comment == null) {
            return ResponseResult.error("评论不存在");
        }
        commentMapper.delete(id);
        return ResponseResult.success("评论已删除");
    }

    @Override
    public ResponseResult<String> updateComment(Long id, String content) {
        Comment comment = commentMapper.findById(id);
        if (comment == null) {
            return ResponseResult.error("评论不存在");
        }
        commentMapper.updateContent(id, content);
        return ResponseResult.success("评论已更新");
    }

    @Override
    public ResponseResult<String> toggleTopComment(Long id) {
        Comment comment = commentMapper.findById(id);
        if (comment == null) {
            return ResponseResult.error("评论不存在");
        }
        Integer newTopStatus = comment.getIsTop() == 1 ? 0 : 1;
        commentMapper.updateTop(id, newTopStatus);
        return ResponseResult.success("置顶状态已更新");
    }

    @Override
    public ResponseResult<String> replyComment(Comment comment) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseResult.error("请先登录");
        }
        if (!isVip(userId)) {
            return ResponseResult.error("VIP会员才可以使用评论功能，请先开通VIP");
        }

        Comment parentComment = commentMapper.findById(comment.getParentId());
        if (parentComment == null) {
            return ResponseResult.error("原评论不存在");
        }

        comment.setUserId(userId);
        comment.setStatus(1);
        comment.setTargetType(parentComment.getTargetType());
        comment.setTargetId(parentComment.getTargetId());
        comment.setLikesCount(0);
        comment.setIsTop(0);

        commentMapper.insert(comment);

        // 更新帖子评论数
        if ("post".equals(comment.getTargetType()) && comment.getTargetId() != null) {
            postMapper.updateCommentsCount(comment.getTargetId(), 1);
        }

        // 发送评论相关通知（回复通知 + 一级评论通知）
        sendCommentNotifications(userId, comment);

        return ResponseResult.success("回复成功");
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String) {
            String username = (String) auth.getPrincipal();
            User user = userMapper.findByUsername(username);
            if (user != null) return user.getId();
        }
        return 1L;
    }

    /**
     * 发送评论相关通知（一级评论 + 回复）
     * 像抖音一样：被评论、被回复都会收到通知
     */
    private void sendCommentNotifications(Long currentUserId, Comment comment) {
        try {
            User currentUser = userMapper.findById(currentUserId);
            String commenterName = currentUser != null ? currentUser.getUsername() : "某用户";

            // 截取评论内容前20个字符作为预览
            String contentPreview = comment.getContent();
            if (contentPreview != null && contentPreview.length() > 20) {
                contentPreview = contentPreview.substring(0, 20) + "...";
            }

            // 场景1：回复其他用户的评论（parentId不为空）
            if (comment.getParentId() != null && comment.getParentId() > 0) {
                sendReplyNotification(currentUserId, comment, commenterName, contentPreview);
            }

            // 场景2：一级评论（通知内容作者）
            sendNewCommentNotification(currentUserId, comment, commenterName, contentPreview);

        } catch (Exception e) {
            System.err.println("发送评论通知失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 发送回复通知（当用户回复其他用户的评论时）
     */
    private void sendReplyNotification(Long currentUserId, Comment comment, String replierName, String contentPreview) {
        try {
            // 查找被回复的原始评论
            Comment parentComment = commentMapper.findById(comment.getParentId());
            if (parentComment == null) return;

            Long parentUserId = parentComment.getUserId();

            // 不给自己发通知
            if (parentUserId == null || parentUserId.equals(currentUserId)) return;

            // 获取来源类型名称
            String sourceType = getSourceTypeDisplayName(comment.getTargetType());

            notificationService.sendNotification(
                parentUserId,
                "COMMENT_REPLY",
                "💬 收到新回复",
                String.format("%s 在%s回复了你的评论：%s", replierName, sourceType, contentPreview),
                comment.getTargetType(),
                comment.getTargetId()
            );
        } catch (Exception e) {
            System.err.println("发送回复通知失败: " + e.getMessage());
        }
    }

    /**
     * 发送新评论通知（当有人评论帖子/路线/资源时，通知作者）
     */
    private void sendNewCommentNotification(Long currentUserId, Comment comment, String commenterName, String contentPreview) {
        try {
            Long targetAuthorId = null;

            // 根据targetType获取内容作者ID
            if ("post".equals(comment.getTargetType()) && comment.getTargetId() != null) {
                // 获取帖子作者
                com.learning.entity.Post post = postMapper.findById(comment.getTargetId());
                if (post != null) targetAuthorId = post.getUserId();
            } else if ("PATH".equals(comment.getTargetType()) && comment.getTargetId() != null) {
                // 获取学习路线创建者
                com.learning.entity.LearningPath path = learningPathMapper.findById(comment.getTargetId());
                if (path != null) targetAuthorId = path.getCreatedBy();
            }
            // 注意：UNIT类型通常没有明确的用户作者（是系统资源），所以不发送一级评论通知
            // 但回复通知仍然会正常发送

            // 不给自己发通知（如果作者是自己）
            if (targetAuthorId == null || targetAuthorId.equals(currentUserId)) return;

            // 获取来源类型名称
            String sourceType = getSourceTypeDisplayName(comment.getTargetType());

            notificationService.sendNotification(
                targetAuthorId,
                "NEW_COMMENT",
                "📝 收到新评论",
                String.format("%s 评论了你的%s：%s", commenterName, sourceType, contentPreview),
                comment.getTargetType(),
                comment.getTargetId()
            );
        } catch (Exception e) {
            System.err.println("发送新评论通知失败: " + e.getMessage());
        }
    }

    /**
     * 获取来源类型的显示名称
     */
    private String getSourceTypeDisplayName(String targetType) {
        switch (targetType) {
            case "post": return "帖子";
            case "PATH": return "学习路线";
            case "unit":
            case "UNIT": return "学习资源";
            default: return "内容";
        }
    }

    /**
     * 发送点赞通知（当有人点赞评论时）
     */
    private void sendLikeNotification(Long currentUserId, Comment comment) {
        try {
            Long commentAuthorId = comment.getUserId();

            // 不给自己发通知
            if (commentAuthorId == null || commentAuthorId.equals(currentUserId)) return;

            // 获取点赞者信息
            User liker = userMapper.findById(currentUserId);
            String likerName = liker != null ? liker.getUsername() : "某用户";

            // 获取来源类型名称
            String sourceType = getSourceTypeDisplayName(comment.getTargetType());

            notificationService.sendNotification(
                commentAuthorId,
                "COMMENT_LIKE",
                "❤️ 收到新点赞",
                String.format("%s 赞了你在%s的评论", likerName, sourceType),
                comment.getTargetType(),
                comment.getTargetId()
            );
        } catch (Exception e) {
            System.err.println("发送点赞通知失败: " + e.getMessage());
        }
    }

    private boolean isVip(Long userId) {
        if (userId == null) return false;
        User user = userMapper.findById(userId);
        if (user == null) return false;

        // 管理员自动拥有VIP权限
        if ("ADMIN".equals(user.getRole())) {
            return true;
        }

        if (user.getVipExpireDate() == null) return false;
        return user.getVipExpireDate().compareTo(java.time.LocalDate.now()) >= 0;
    }
}
