package com.aixone.workbench.service.impl;

import com.aixone.workbench.api.dto.MenuDTO;
import com.aixone.workbench.service.MenuService;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 菜单聚合与个性化服务实现。
 */
@Service
public class MenuServiceImpl implements MenuService {
    @Override
    public List<MenuDTO> getVisibleMenus(UUID userId, UUID tenantId) {
        // TODO: 拉取 directory-serve 主数据，聚合本地个性化配置，返回可见菜单
        return Collections.emptyList();
    }

    @Override
    public void saveUserMenuCustom(UUID userId, String menuCustomJson) {
        // TODO: 持久化用户菜单个性化配置
    }
} 