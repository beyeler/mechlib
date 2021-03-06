package org.marketdesignresearch.mechlib.winnerdetermination;

import com.google.common.base.Preconditions;
import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import org.marketdesignresearch.mechlib.core.BundleBid;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps an OR or OR* winner determination
 * 
 * @author Benedikt Buenz
 * 
 */
public class ORWinnerDetermination extends BidBasedWinnerDetermination {

    protected final MIPWrapper winnerDeterminationProgram;

    public ORWinnerDetermination(Bids bids) {
        super(bids);
        winnerDeterminationProgram = createWinnerDeterminationMIP(bids);
    }

    protected MIPWrapper createWinnerDeterminationMIP(Bids bids) {
        MIPWrapper winnerDeterminationProgram = MIPWrapper.makeNewMaxMIP();

        // Add decision variables and objective terms:
        for (Bidder bidder : bids.getBidders()) {
            for (BundleBid bundleBid : bids.getBid(bidder).getBundleBids()) {
                Variable bidI = winnerDeterminationProgram.makeNewBooleanVar("Bid_" + bundleBid.getId());
                winnerDeterminationProgram.addObjectiveTerm(bundleBid.getAmount().doubleValue(), bidI);
                bidVariables.put(bundleBid, bidI);
            }
        }
        Map<Good, Constraint> goods = new HashMap<>();

        for (Bidder bidder : bids.getBidders()) {
            for (BundleBid bundleBid : bids.getBid(bidder).getBundleBids()) {
                for (BundleEntry entry : bundleBid.getBundle().getBundleEntries()) {
                    Constraint noDoubleAssignment = goods.computeIfAbsent(entry.getGood(), g -> new Constraint(CompareType.LEQ, g.getQuantity()));
                    noDoubleAssignment.addTerm(entry.getAmount(), bidVariables.get(bundleBid));
                }
            }
        }
        goods.values().forEach(winnerDeterminationProgram::add);

        return winnerDeterminationProgram;
    }

    @Override
    public WinnerDetermination join(WinnerDetermination other) {
        Preconditions.checkArgument(other instanceof BidBasedWinnerDetermination);
        BidBasedWinnerDetermination otherBidBased = (BidBasedWinnerDetermination) other;
        return new ORWinnerDetermination(otherBidBased.getBids().join(getBids()));
    }

    @Override
    public MIPWrapper getMIP() {
        return winnerDeterminationProgram;
    }

}
