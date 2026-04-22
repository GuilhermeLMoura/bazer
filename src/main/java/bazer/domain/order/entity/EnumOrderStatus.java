package bazer.domain.order.entity;

public enum EnumOrderStatus {
    /** Carrinho ativo — ainda sendo montado pelo comprador */
    PENDING,
    /** Pedido enviado para pagamento — saiu do carrinho */
    AGUARDANDO_PAGAMENTO,
    /** Pagamento confirmado */
    CONFIRMED,
    /** Em separação/processamento no estoque */
    PROCESSING,
    /** Enviado para entrega */
    SHIPPED,
    /** Entregue ao comprador */
    DELIVERED,
    /** Cancelado */
    CANCELLED
}
