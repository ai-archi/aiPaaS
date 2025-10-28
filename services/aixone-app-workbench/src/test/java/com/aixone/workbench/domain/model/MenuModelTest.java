package com.aixone.workbench.domain.model;

import com.aixone.workbench.menu.domain.model.Menu;
import com.aixone.workbench.menu.domain.model.UserMenuCustom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Menu领域模型测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@DisplayName("Menu领域模型测试")
class MenuModelTest {
    
    @Test
    @DisplayName("测试Menu - isVisible方法")
    void testMenu_IsVisible() {
        // Given
        Menu visibleMenu = Menu.builder()
                .visible(true)
                .build();
        
        Menu hiddenMenu = Menu.builder()
                .visible(false)
                .build();
        
        Menu nullVisibleMenu = Menu.builder()
                .visible(null)
                .build();
        
        // When & Then
        assertThat(visibleMenu.isVisible()).isTrue();
        assertThat(hiddenMenu.isVisible()).isFalse();
        assertThat(nullVisibleMenu.isVisible()).isFalse();
    }
    
    @Test
    @DisplayName("测试Menu - isRoot方法")
    void testMenu_IsRoot() {
        // Given
        Menu rootMenu = Menu.builder()
                .parentId(null)
                .build();
        
        Menu childMenu = Menu.builder()
                .parentId(UUID.randomUUID())
                .build();
        
        // When & Then
        assertThat(rootMenu.isRoot()).isTrue();
        assertThat(childMenu.isRoot()).isFalse();
    }
    
    @Test
    @DisplayName("测试UserMenuCustom - isHidden方法")
    void testUserMenuCustom_IsHidden() {
        // Given
        UserMenuCustom hiddenCustom = UserMenuCustom.builder()
                .isHidden(true)
                .build();
        
        UserMenuCustom visibleCustom = UserMenuCustom.builder()
                .isHidden(false)
                .build();
        
        // When & Then
        assertThat(hiddenCustom.isHidden()).isTrue();
        assertThat(visibleCustom.isHidden()).isFalse();
    }
    
    @Test
    @DisplayName("测试UserMenuCustom - isQuickEntry方法")
    void testUserMenuCustom_IsQuickEntry() {
        // Given
        UserMenuCustom quickEntryCustom = UserMenuCustom.builder()
                .isQuickEntry(true)
                .build();
        
        UserMenuCustom normalCustom = UserMenuCustom.builder()
                .isQuickEntry(false)
                .build();
        
        // When & Then
        assertThat(quickEntryCustom.isQuickEntry()).isTrue();
        assertThat(normalCustom.isQuickEntry()).isFalse();
    }
}

