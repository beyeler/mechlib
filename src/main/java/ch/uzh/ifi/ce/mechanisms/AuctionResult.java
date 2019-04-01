package ch.uzh.ifi.ce.mechanisms;

import ch.uzh.ifi.ce.domain.Allocation;
import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.domain.Payment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Set;

@EqualsAndHashCode
@ToString
public class AuctionResult implements MechanismResult {
    @Getter
    private final Payment payment;
    @Getter
    private final Allocation allocation;
    @EqualsAndHashCode.Exclude
    @Getter
    private final MetaInfo metaInfo;

    public AuctionResult(Payment payment, Allocation allocation) {
        this.payment = payment;
        this.allocation = allocation;
        this.metaInfo = allocation.getMetaInfo().join(payment.getMetaInfo());
    }

    public BigDecimal payoffOf(Bidder winner) {
        return allocation.allocationOf(winner).getValue().subtract(payment.paymentOf(winner).getAmount());
    }

    public Set<Bidder> getWinners() {
        return allocation.getWinners();
    }
}
