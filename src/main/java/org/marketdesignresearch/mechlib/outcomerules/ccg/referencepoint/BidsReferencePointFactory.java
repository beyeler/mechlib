package org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.marketdesignresearch.mechlib.core.*;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;

import java.util.Map;

public class BidsReferencePointFactory implements ReferencePointFactory {

    @Override
    public Payment computeReferencePoint(Bids bids, Allocation allocation) {
        Map<Bidder, BidderPayment> paymentMap = ImmutableMap.copyOf(Maps.transformValues(allocation.getTradesMap(), ba -> new BidderPayment(ba.getValue())));
        return new Payment(paymentMap, new MetaInfo());
    }

    @Override
    public String getName() {
        return "BIDS";
    }

    @Override
    public boolean belowCore() {
        return false;
    }

}
