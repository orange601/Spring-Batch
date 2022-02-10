package orange.spring.batch.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SequenceGenerator(
		name="PROGRESS_SEQ",
        sequenceName="DTD_PROGRESS_SEQ",
        initialValue=1,
        allocationSize=1
)
@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name="DTD_PROGRESS")
public class Progress {
	@GeneratedValue(
            strategy=GenerationType.SEQUENCE,
            generator="PROGRESS_SEQ"
    )
	@Id
	private Long seq;
	private String name;
	//private int rn;
	
	@Builder
	private Progress(String name) {
		this.name = name;
	}

}
