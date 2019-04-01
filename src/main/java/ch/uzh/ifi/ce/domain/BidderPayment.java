package ch.uzh.ifi.ce.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class BidderPayment implements Comparable<BidderPayment> {
    public static final BidderPayment ZERO_PAYMENT = new BidderPayment(BigDecimal.ZERO);
    @Getter
    private final BigDecimal amount;

    @Override
    public int compareTo(BidderPayment o) {
        return getAmount().compareTo(o.getAmount());
    }
}
