package kr.or.futur.futurcertification.domain.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@MappedSuperclass
public class BaseEntity {

	@CreatedDate
	@Column(name = "created_dt", nullable = false, updatable = false)
	protected LocalDateTime createdDt;

	@LastModifiedDate
	@Column(name = "updated_dt")
	protected LocalDateTime updatedDt;

	/**
	 * LocalDate -> yyyy-MM-dd(String)
	 * @param time LocalDate
	 * @return String
	 */
	protected String toTimeString(LocalDate time) {
		return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
}
