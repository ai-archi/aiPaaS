package com.aixone.directory.organization.infrastructure.persistence;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.aixone.directory.organization.application.dto.DepartmentDto;
import com.aixone.directory.organization.domain.aggregate.Department;
import com.aixone.directory.organization.infrastructure.persistence.dbo.DepartmentDbo;
import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentDto toDto(Department department);

    @Mapping(target = "users", ignore = true)
    @Mapping(target = "orgId", expression = "java(dbo.getOrganization() != null ? dbo.getOrganization().getId() : null)")
    Department toDomain(DepartmentDbo dbo);

    @Mapping(target = "users", ignore = true)
    @Mapping(target = "organization", ignore = true)
    DepartmentDbo toDbo(Department domain);

    default Set<String> userDboSetToUuidSet(Set<UserDbo> users) {
        if (users == null) return null;
        Set<String> ids = new HashSet<>();
        for (UserDbo user : users) {
            ids.add(user.getId());
        }
        return ids;
    }

    default Set<UserDbo> uuidSetToUserDboSet(Set<String> ids) {
        // 这里只能返回空集，实际填充应由Service层或Repository完成
        return new HashSet<>();
    }
} 