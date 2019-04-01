package ch.uzh.ifi.ce.domain.cats;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
public class CATSBid implements Serializable {
    private static final long serialVersionUID = 7736295648019090804L;
    private int id;
    private BigDecimal amount;
    private List<Integer> goodIds;
}
