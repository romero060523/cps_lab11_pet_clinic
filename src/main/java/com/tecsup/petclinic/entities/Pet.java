package com.tecsup.petclinic.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.time.LocalDate;

/**
 * 
 * @author jgomezm
 *
 */
@Entity(name = "pets")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Pet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	@Column(name = "type_id")
	private int typeId;
	@Column(name = "owner_id")
	private int ownerId;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "birth_date")
	private LocalDate birthDate;

	/*
	public Pet() {
	}

	public Pet(Integer id, String name, int type_id, int owner_id, Date birthDate) {
		super();
		this.id = id;
		this.name = name;
		this.typeId = type_id;
		this.ownerId = owner_id;
		this.birthDate = birthDate;

	}

	public Pet(String name, int type_id, int owner_id, Date birthDate) {
		super();
		this.name = name;
		this.typeId = type_id;
		this.ownerId = owner_id;
		this.birthDate = birthDate;
	}
	 */


}
