package com.aixone.directory.organization.infrastructure.persistence;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.aixone.directory.organization.application.dto.PositionDto;
import com.aixone.directory.organization.domain.aggregate.Position;
import com.aixone.directory.organization.infrastructure.persistence.dbo.PositionDbo;
import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;
import java.util.Set;
import java.util.HashSet;

@Mapper(componentModel = "spring")
public interface PositionMapper {

    PositionDto toDto(Position position);

    @Mapping(target = "users", ignore = true)
    Position toDomain(PositionDbo dbo);

    @Mapping(target = "users", ignore = true)
    PositionDbo toDbo(Position domain);

    default Set<String> userDboSetToStringSet(Set<UserDbo> users) {
        if (users == null) return null;
        Set<String> ids = new HashSet<>();
        for (UserDbo user : users) {
            ids.add(user.getId());
        }
        return ids;
    }

    default Set<UserDbo> stringSetToUserDboSet(Set<String> ids) {
        // 这里只能返回空集，实际填充应由Service层或Repository完成
        return new HashSet<>();
    }
} 