package com.dery.lecatro.entity;

import java.time.LocalDateTime;

import com.dery.lecatro.entity.enums.HistoryEvent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History {

	@Id
	@SequenceGenerator(name = "history_seq", sequenceName = "history_seq", allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "history_seq")
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "request_id")
	private Request request;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private HistoryEvent event;

	@Column(name = "event_description", nullable = false)
	private String description;

	@Column(nullable = false, updatable = false)
	private LocalDateTime occurredAt;

	@PrePersist
	private void prePersist() {
		this.occurredAt = LocalDateTime.now();
	}
}