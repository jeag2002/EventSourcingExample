package org.crealogix.transaction.data.aggregates;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.crealogix.infraestructure.interfaces.entities.model.AggregateRoot;
import org.crealogix.transaction.data.entities.Account;
import org.crealogix.transaction.enums.TransactionState;
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
import lombok.Setter;

@Data
@Entity


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder")

@Table(name="Transactions")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Transaction implements AggregateRoot<UUID>, Serializable{
	
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Type(type = "uuid-char")
	private UUID id;
	
	@Column
    private String idStr;
    
	@Column
	private long version;
	
	@Column
	@Enumerated(EnumType.STRING)
	private TransactionState state;
    
	@Column
    private float importValue; 
	
	@ManyToMany(targetEntity = Account.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(
	name = "transaction_account", 
    joinColumns = @JoinColumn(name = "transaction_id", referencedColumnName = "id"), 
    inverseJoinColumns = @JoinColumn(name = "account_id", referencedColumnName = "id"))
	private List<Account> accounts_trans;
	
	
	@ManyToMany(targetEntity = Client.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(
	name = "transaction_client", 
    joinColumns = @JoinColumn(name = "transaction_id", referencedColumnName = "id"), 
    inverseJoinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"))
	private List<Client> accounts_client;
	
	//public Transaction() {super();}

}
