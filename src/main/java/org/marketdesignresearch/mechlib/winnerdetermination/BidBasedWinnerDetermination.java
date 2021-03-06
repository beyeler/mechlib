package org.marketdesignresearch.mechlib.winnerdetermination;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.math.DoubleMath;
import edu.harvard.econcs.jopt.solver.ISolution;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import org.marketdesignresearch.mechlib.core.*;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;

import java.math.BigDecimal;
import java.util.*;

public abstract class BidBasedWinnerDetermination extends WinnerDetermination {

    private Bids bids;
    // TODO: Make sure we're not running in the same issue as back with SATS with this HashMap
    protected Map<BundleBid, Variable> bidVariables = new HashMap<>();

    public BidBasedWinnerDetermination(Bids bids) {
        this.bids = bids;
    }

    protected Bids getBids() {
        return bids;
    }

    @Override
    protected Allocation solveWinnerDetermination() {
        if (bids.getBidders().isEmpty()) {
            return Allocation.EMPTY_ALLOCATION;
        }
        return super.solveWinnerDetermination();
    }

//    @Override
//    public List<Allocation> getBestAllocations(int k) {
//        if (k == 1) return Lists.newArrayList(getAllocation());
//        getMIP().setSolveParam(SolveParam.SOLUTION_POOL_CAPACITY, k);
//        getMIP().setSolveParam(SolveParam.SOLUTION_POOL_MODE, 4);
//        getMIP().setVariablesOfInterest(getBidVariables());
//        List<Allocation> allocations = getIntermediateSolutions();
//        getMIP().setSolveParam(SolveParam.SOLUTION_POOL_MODE, 0);
//        return allocations;
//    }

    @Override
    public Allocation adaptMIPResult(ISolution mipResult) {
        ImmutableMap.Builder<Bidder, BidderAllocation> trades = ImmutableMap.builder();
        for (Bidder bidder : bids.getBidders()) {
            BigDecimal totalValue = BigDecimal.ZERO;
            HashSet<BundleEntry> bundleEntries = new HashSet<>();
            ImmutableSet.Builder<BundleBid> bundleBids = ImmutableSet.builder();
            for (BundleBid bundleBid : bids.getBid(bidder).getBundleBids()) {
                if (DoubleMath.fuzzyEquals(mipResult.getValue(getBidVariable(bundleBid)), 1, 1e-3)) {
                    bundleEntries.addAll(bundleBid.getBundle().getBundleEntries());
                    bundleBids.add(bundleBid);
                    totalValue = totalValue.add(bundleBid.getAmount());
                }
            }
            if (!bundleEntries.isEmpty()) {
                trades.put(bidder, new BidderAllocation(totalValue, new Bundle(bundleEntries), bundleBids.build()));
            }
        }

        MetaInfo metaInfo = new MetaInfo();
        metaInfo.setNumberOfMIPs(1);
        metaInfo.setMipSolveTime(mipResult.getSolveTime());
        return new Allocation(trades.build(), bids, metaInfo);
    }

    protected Variable getBidVariable(BundleBid bundleBid) {
        return bidVariables.get(bundleBid);
    }

    protected Collection<Variable> getBidVariables() {
        return bidVariables.values();
    }
}