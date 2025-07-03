package com.aixone.directory.organization.application;

import java.util.Set;
import com.aixone.directory.organization.infrastructure.persistence.dbo.PositionDbo;
import com.aixone.directory.organization.infrastructure.persistence.PositionJpaRepository;
import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;
import com.aixone.directory.user.infrastructure.persistence.UserJpaRepository;
import org.springframework.transaction.annotation.Transactional;

public class PositionApplicationService {

    private final PositionJpaRepository positionJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public PositionApplicationService(PositionJpaRepository positionJpaRepository, UserJpaRepository userJpaRepository) {
        this.positionJpaRepository = positionJpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @Transactional
    public void assignUsersToPosition(String positionId, Set<String> userIds) {
        PositionDbo position = positionJpaRepository.findById(positionId).orElseThrow();
        Set<UserDbo> users = new java.util.HashSet<>(userJpaRepository.findAllById(userIds));
        position.getUsers().addAll(users);
        positionJpaRepository.save(position);
    }

    @Transactional
    public void removeUsersFromPosition(String positionId, Set<String> userIds) {
        PositionDbo position = positionJpaRepository.findById(positionId).orElseThrow();
        position.getUsers().removeIf(u -> userIds.contains(u.getId()));
        positionJpaRepository.save(position);
    }
} 