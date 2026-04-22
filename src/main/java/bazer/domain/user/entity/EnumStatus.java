package bazer.domain.user.entity;

import lombok.Getter;

@Getter
public enum EnumStatus {
    ATIVO("Usuário ativo"),
    INATIVO("Usuário Inativo");

    private String descricao;

    EnumStatus(String descricao){
        this.descricao = descricao;
    }
}
