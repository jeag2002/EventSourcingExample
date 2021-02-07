package org.crealogix.transaction.data.aggregates;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import javax.persistence.JoinColumn;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.crealogix.infraestructure.interfaces.entities.model.AggregateRoot;
import org.crealogix.transaction.data.entities.Account;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.domain.EntityScan;

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


@Table(name="Client")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Client implements AggregateRoot<UUID>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	//@GeneratedValue(generator = "UUID")
	//@GenericGenerator(
	//        name = "UUID",
	//        strategy = "org.hibernate.id.UUIDGenerator"
	//)
	//@Column(name = "id", updatable = false, nullable = false)
	//@ColumnDefault("random_uuid()")
	@Type(type = "uuid-char")
	private UUID id;
	
	@Column
	private String idStr;
    
	@Column
	private long version;
    
	@Column
	private boolean enabled;
	
	@Column
	private boolean enabledMultiAccountPay;
	
	
	@ManyToMany(targetEntity = Account.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(
	name = "client_account", 
    joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"), 
    inverseJoinColumns = @JoinColumn(name = "account_id", referencedColumnName = "id"))
	private List<Account> accounts;
	
	@ManyToMany(targetEntity = Transaction.class, mappedBy = "accounts_client", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Transaction> transactions;
	
	@NonNull
	public float totalAccountClient() {
		
		Float totFloat = new Float(0);
		
		if (accounts.size() > 0) {
		
			Double tot = accounts.
					stream().
					map(e->new Double(e.getImportValue())).
					collect(Collectors.summingDouble(Double::doubleValue));
			totFloat = new Float(tot);
		
		} else {
			
			totFloat = 0.0f;
		}
		
		return totFloat;
		
	}
	
	//public Client() {super();}
	
	
	
    

}
