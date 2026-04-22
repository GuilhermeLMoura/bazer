package bazer.domain.user.entity;

import lombok.Getter;

@Getter
public enum EnumRole {

    ADMIN("ROLE_ADMIN"),
    VENDEDOR("ROLE_VENDEDOR"),
    COMPRADOR("ROLE_COMPRADOR");

    private final String authority;

    EnumRole(String authority){
        this.authority = authority;
    }

    public String getAuthority(){
        return authority;
    }
}