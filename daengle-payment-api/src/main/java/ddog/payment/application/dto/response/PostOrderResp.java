package ddog.payment.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostOrderResp {
    Long orderId;
    Long accountId;
    Long estimateId;
    String orderUId;
}
