package com.exam.dal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.util.List;

@Data
public class UserDto {

    @JsonProperty("username")
    @ApiModelProperty(example = "john.doe", required = true, value = "The username")
    @NotNull
    @Size(min = 1, max = 80)
    private String username;;

    @JsonProperty("password")
    @Size(min = 1, max=80)
    @ApiModelProperty(value ="The password", example = "1234")
    private String password;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("roles")
    @Valid
    private List<RoleDto> roles = null;

}
