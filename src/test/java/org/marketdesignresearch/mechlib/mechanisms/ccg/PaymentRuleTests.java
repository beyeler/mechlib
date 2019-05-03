package org.marketdesignresearch.mechlib.mechanisms.ccg;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.marketdesignresearch.mechlib.domain.*;
import org.marketdesignresearch.mechlib.domain.bidder.BundleValue;
import org.marketdesignresearch.mechlib.domain.bidder.SimpleBidder;
import org.marketdesignresearch.mechlib.domain.bidder.Value;
import org.marketdesignresearch.mechlib.domain.bidder.ValueType;
import org.marketdesignresearch.mechlib.mechanisms.AuctionMechanism;
import org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules.*;
import org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint.BidsReferencePointFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint.VCGReferencePointFactory;
import org.marketdesignresearch.mechlib.mechanisms.vcg.ORVCGAuction;
import org.marketdesignresearch.mechlib.utils.CPLEXUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentRuleTests {
    @Test
    public void testEqualRule() {
        CPLEXUtils.SOLVER.initializeSolveParams();
        Good west = new SimpleGood("west");
        Good east = new SimpleGood("east");

        BundleValue westBundle = new BundleValue(BigDecimal.valueOf(1), ImmutableSet.of(west), "west");
        SimpleBidder westBidder = new SimpleBidder("west", new Value(ImmutableSet.of(westBundle), ValueType.LOCAL_WEST));

        BundleValue eastBundle = new BundleValue(BigDecimal.valueOf(2.5), ImmutableSet.of(east), "east");
        SimpleBidder eastBidder = new SimpleBidder("east", new Value(ImmutableSet.of(eastBundle), ValueType.LOCAL_EAST));

        BundleValue globalBundle = new BundleValue(BigDecimal.valueOf(2), ImmutableSet.of(west, east), "global");
        SimpleBidder globalBidder = new SimpleBidder("global", new Value(ImmutableSet.of(globalBundle), ValueType.GLOBAL));

        Domain domain = new Domain(ImmutableSet.of(westBidder, eastBidder, globalBidder), ImmutableSet.of(west, east));
        MechanismFactory equalNorm = new VariableNormCCGFactory(new BidsReferencePointFactory(), new NormFactory(Norm.MANHATTAN, new EqualWeightsFactory(), Payment.ZERO),
                NormFactory.withEqualWeights(Norm.EUCLIDEAN));
        AuctionMechanism mechanism = equalNorm.getMechanism(domain.toAuction());
        Payment payment = mechanism.getPayment();
        assertThat(payment.paymentOf(eastBidder).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1.75));
        assertThat(payment.paymentOf(westBidder).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(.25));

    }

    @Test
    public void testLubinParkesNames() {
        ParameterizableMechanismFactory mechFactory = new VariableNormCCGFactory(new BidsReferencePointFactory(), new NormFactory(Norm.MANHATTAN, new EqualWeightsFactory()),
                NormFactory.withEqualWeights(Norm.EUCLIDEAN));
        System.out.println(mechFactory.lubinParkesName());
    }

    @Test
    public void paperExample() throws IOException {
        CPLEXUtils.SOLVER.initializeSolveParams();
        AuctionInstance auctionInstance = Domain.fromCatsFile(Paths.get("src/test/resources/supersimple.txt")).toAuction();
        MechanismFactory quadratic = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN),
                NormFactory.withEqualWeights(Norm.EUCLIDEAN));
        MechanismFactory large = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN), new NormFactory(Norm.MANHATTAN,
                new PayoffWeightsFactory()));
        MechanismFactory small = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN), new NormFactory(Norm.MANHATTAN,
                new InversePayoffWeightsFactory()));
        MechanismFactory fractional = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN), new NormFactory(Norm.EUCLIDEAN,
                new InversePayoffWeightsFactory()));
        System.out.println(quadratic.getMechanism(auctionInstance).getPayment());
        System.out.println(fractional.getMechanism(auctionInstance).getPayment());
        System.out.println(small.getMechanism(auctionInstance).getPayment());
        System.out.println(large.getMechanism(auctionInstance).getPayment());
        System.out.println(new ORVCGAuction(auctionInstance).getPayment());

    }
}