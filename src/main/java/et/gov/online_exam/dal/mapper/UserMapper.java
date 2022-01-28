package et.gov.online_exam.dal.mapper;


import et.gov.online_exam.dal.dto.RoleDto;
import et.gov.online_exam.dal.dto.UserDto;
import et.gov.online_exam.dal.entity.Role;
import et.gov.online_exam.dal.entity.User;
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
