package org.crealogix.transaction.data.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.crealogix.infraestructure.interfaces.entities.model.Entity;
import org.crealogix.transaction.data.aggregates.Client;
import org.crealogix.transaction.data.aggregates.Transaction;
import org.crealogix.transaction.enums.ActionState;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@javax.persistence.Entity


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder")

@Table(name="History")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class History implements Entity<UUID>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Type(type = "uuid-char")
	private UUID id;
	
	@Column(name = "timestampHistory", columnDefinition = "TIMESTAMP")
	private LocalDateTime timestampHistory;
	
	@Column
	private String description;
	
	@Column
	@Enumerated(EnumType.STRING)
	private ActionState state;
	
	@Column
	@Type(type = "uuid-char")
	private UUID refId;

}
