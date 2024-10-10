package model;

import javax.persistence.Entity;

@Entity
public class Paciente extends Pessoa {

	private String cpf;

	public Paciente() {

	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

}
