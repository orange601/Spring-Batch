package orange.spring.batch.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Statistics {
	private Long wbsSeqNo;
	private Long actvtSeqNo;
	private String wbsLvl6;
	private String wbsLvl7;
	private String wbsLvl7Cd;
	private String quantUnit;
	private String zoneVal;
	private String plnLchYmd;
	private String plnCmpltYmd;
	private int plnQuantVal;
	private BigDecimal totalQuantVal; 
	private BigDecimal actualQuantVal;
	private BigDecimal quantVal;
	private String camPos;
	private String revitElement;
	//private String actualYmd;
}
