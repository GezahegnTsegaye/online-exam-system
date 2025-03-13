package com.exam.dal.mapper;


import com.exam.dal.dto.RoleDto;
import com.exam.dal.dto.UserDto;
import com.exam.dal.entity.Role;
import com.exam.dal.entity.User;
import org.mapstruct.Mapper;

import java.util.Collection;

@Mapper
public interface UserMapper {

    Role toRole(RoleDto roleDto);
    RoleDto toRoleDto(Role role);
    Collection<RoleDto> toRoleDto(Collection<Role> roles);
    User toUser(UserDto userDto);
    UserDto toUserDto(User user);
    Collection<Role> toRoles(Collection<RoleDto> roleDtos);

}
