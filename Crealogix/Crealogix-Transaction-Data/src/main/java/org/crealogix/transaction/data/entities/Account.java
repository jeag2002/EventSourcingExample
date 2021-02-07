package org.crealogix.transaction.data.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.crealogix.infraestructure.interfaces.entities.model.Entity;
import org.crealogix.transaction.data.aggregates.Client;
import org.crealogix.transaction.data.aggregates.Transaction;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Data
@javax.persistence.Entity


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder")

@Table(name="Account")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Account implements Entity<UUID>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Type(type = "uuid-char")
	private UUID id;
	
	@Column
	private String idStr;	
	
	@Column
	private String ccc;
	
	@Column
	private float importValue;

	@ManyToMany(targetEntity = Client.class, mappedBy = "accounts", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Client> clients;
	
	@ManyToMany(targetEntity = Transaction.class, mappedBy = "accounts_trans", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Transaction> transactions;
	
	
	@NonNull
	public Account clone() {
		
		Account account = new Account();
		
		account.setId(id);
		account.setIdStr(idStr);
		account.setCcc(ccc);
		account.setImportValue(importValue);
		
		account.setClients(this.getClients());
		account.setTransactions(this.getTransactions());
		
		return account;
		
	}
	
	
}
