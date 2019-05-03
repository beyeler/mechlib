package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.domain.Bidder;

public class FixedCorePaymentWeights implements CorePaymentWeights {
    private final double constant;

    public FixedCorePaymentWeights(double constant) {
        this.constant = constant;
    }

    @Override
    public double getWeight(Bidder bidder) {
        return constant;
    }

}