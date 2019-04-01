package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.domain.Payment;
import ch.uzh.ifi.ce.mechanisms.MetaInfo;
import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.mip.*;

import java.math.BigDecimal;

public class ManhattenNorm extends PaymentNorm implements CorePaymentNorm {
    private final CorePaymentWeights weigths;
    private final AuctionResult referencePoint;

    public ManhattenNorm(AuctionResult referencePoint, CorePaymentWeights weights) {
        this.referencePoint = referencePoint;
        this.weigths = weights;
    }

    @Override
    public PrimaryObjective getPrimaryObjectiveConstraint(IMIP program) {
        MetaInfo metaInfo = new MetaInfo();
        IMIP newProgram = MIPWrapper.makeMIPWithoutObjective(program);
        addNormObjective(newProgram);
        IMIPResult result = solveProgram(newProgram);
        metaInfo.setLpSolveTime(result.getSolveTime());
        metaInfo.setNumberOfLPs(1);
        setProposedValues(result, program);
        double objectiveValue = result.getObjectiveValue();
        Constraint primaryObjectiveConstraint = new Constraint(CompareType.LEQ, objectiveValue);
        newProgram.getLinearObjectiveTerms().forEach(primaryObjectiveConstraint::addTerm);
        return new PrimaryObjective(primaryObjectiveConstraint, metaInfo);
    }

    @Override
    public Payment minimizeDistance(IMIP program) {
        IMIP newProgram = MIPWrapper.makeMIPWithoutObjective(program);
        addNormObjective(newProgram);
        IMIPResult result = solveProgram(newProgram);
        setProposedValues(result, program);
        MetaInfo metaInfo = new MetaInfo();
        metaInfo.setNumberOfLPs(1);
        metaInfo.setLpSolveTime(result.getSolveTime());
        return adaptProgram(referencePoint.getWinners(), result, metaInfo);

    }

    @Override
    public Void addNormObjective(IMIP mip) {
        for (Bidder winner : referencePoint.getWinners()) {
            BigDecimal payoff = referencePoint.payoffOf(winner);
            if (payoff.signum() != 0) {
                Variable variable = mip.getVar(BIDDER + winner.getId());
                LinearTerm objectiveTerm = new LinearTerm(payoff.signum() * weigths.getWeight(winner), variable);
                mip.addObjectiveTerm(objectiveTerm);
                Constraint constraint = new Constraint(CompareType.GEQ, referencePoint.getPayment().paymentOf(winner).getAmount().doubleValue() * payoff.signum());
                constraint.addTerm(payoff.signum(), variable);
            }
        }

        return null;
    }

    @Override
    public AuctionResult getReferencePoint() {
        return referencePoint;
    }

}
