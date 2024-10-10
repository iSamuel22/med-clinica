package service;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import model.Paciente;

@Stateless
public class PacienteService extends GenericService<Paciente> {

	public PacienteService() {
		super(Paciente.class);

	}

	public Paciente buscarPorCpf(String cpf) {

		CriteriaBuilder cBuilder = getEntityManager().getCriteriaBuilder();

		CriteriaQuery<Paciente> cQuery = cBuilder.createQuery(Paciente.class);

		Root<Paciente> rootPaciente = cQuery.from(Paciente.class);

		cQuery.select(rootPaciente);

		cQuery.where(cBuilder.equal(rootPaciente.get("cpf"), cpf));

		TypedQuery<Paciente> tQuery = getEntityManager().createQuery(cQuery);

		try {
			return tQuery.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			throw new RuntimeException("Erro ao buscar paciente por CPF.", e);
		}
	}

}
