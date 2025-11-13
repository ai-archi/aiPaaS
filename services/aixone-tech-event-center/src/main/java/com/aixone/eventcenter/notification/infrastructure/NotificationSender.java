package com.aixone.eventcenter.notification.infrastructure;

import com.aixone.eventcenter.notification.domain.Notification;

/**
 * 通知发送器接口
 * 定义通知发送的统一接口
 */
public interface NotificationSender {
    
    /**
     * 发送通知
     * @param notification 通知对象
     * @return 是否发送成功
     */
    boolean send(Notification notification);
}

