package org.marketdesignresearch.mechlib.domain;

import org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration.PotentialCoalition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * Immutable Wrapper for an allocation
 * 
 * @author Benedikt Buenz
 * 
 */
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Slf4j
public final class BidderAllocation {
    public static final BidderAllocation ZERO_ALLOCATION = new BidderAllocation(BigDecimal.ZERO, emptySet(), emptySet());
    @Getter
    private final BigDecimal value;
    @Getter
    private final Bundle bundle;
    @Getter
    private final Set<BundleBid> acceptedBids; // TODO: Check if this is needed

    public BidderAllocation(BigDecimal value, Set<Good> bundle, Set<BundleBid> acceptedBids) {
        this(value, Bundle.singleGoods(bundle), acceptedBids);
    }

    public PotentialCoalition getPotentialCoalition(Bidder bidder) {
        return new PotentialCoalition(bundle, bidder, value);
    }

}