package org.marketdesignresearch.mechlib.winnerdetermination;

import org.marketdesignresearch.mechlib.domain.AuctionInstance;
import org.marketdesignresearch.mechlib.domain.Bidder;
import org.marketdesignresearch.mechlib.domain.BundleBid;
import org.marketdesignresearch.mechlib.domain.Good;
import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps an OR or OR* winner determination
 * 
 * @author Benedikt Buenz
 * 
 */
public class ORWinnerDetermination extends BidBasedWinnerDetermination {
    protected final Map<BundleBid, Variable> bidVariables = new HashMap<>();
    protected final MIPWrapper winnerDeterminationProgram;

    public ORWinnerDetermination(AuctionInstance auctionInstance) {
        super(auctionInstance);
        winnerDeterminationProgram = createWinnerDeterminationMIP(auctionInstance);

    }

    protected MIPWrapper createWinnerDeterminationMIP(AuctionInstance auctionInstance) {
        MIPWrapper winnerDeterminationProgram = MIPWrapper.makeNewMaxMIP();

        // Add decision variables and objective terms:
        for (Bidder bidder : auctionInstance.getBidders()) {
            for (BundleBid bundleBid : auctionInstance.getBid(bidder).getBundleBids()) {
                Variable bidI = winnerDeterminationProgram.makeNewBooleanVar("Bid_" + bundleBid.getId());
                winnerDeterminationProgram.addObjectiveTerm(bundleBid.getAmount().doubleValue(), bidI);
                bidVariables.put(bundleBid, bidI);
            }
        }
        Map<Good, Constraint> goods = new HashMap<>();

        for (Bidder bidder : auctionInstance.getBidders()) {
            for (BundleBid bundleBid : auctionInstance.getBid(bidder).getBundleBids()) {
                for (Map.Entry<Good, Integer> entry : bundleBid.getBundle().entrySet()) {
                    Constraint noDoubleAssignment = goods.computeIfAbsent(entry.getKey(), g -> new Constraint(CompareType.LEQ, g.available()));
                    noDoubleAssignment.addTerm(entry.getValue(), bidVariables.get(bundleBid));
                }
            }
        }
        goods.values().forEach(winnerDeterminationProgram::add);

        return winnerDeterminationProgram;
    }

    @Override
    public MIPWrapper getMIP() {
        return winnerDeterminationProgram;
    }

    @Override
    protected Variable getBidVariable(BundleBid bundleBid) {
        return bidVariables.get(bundleBid);
    }
}