package service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import model.Atendimento;
import model.Medico;

@Stateless
public class MedicoService extends GenericService<Medico> {

	public MedicoService() {
		super(Medico.class);

	}

	private List<Order> obterOrdenacao(CriteriaBuilder criteriaBuilder, Root<Medico> medicoRoot) {
		return Arrays.asList(criteriaBuilder.asc(medicoRoot.get("primeiroNome")),
				criteriaBuilder.asc(medicoRoot.get("sobrenome")));
	}

	public List<Medico> listarMedicos() {

		CriteriaBuilder cBuilder = getEntityManager().getCriteriaBuilder();

		CriteriaQuery<Medico> cQuery = cBuilder.createQuery(Medico.class);

		Root<Medico> rootMedico = cQuery.from(Medico.class);

		cQuery.select(rootMedico).orderBy(obterOrdenacao(cBuilder, rootMedico));

		return getEntityManager().createQuery(cQuery).getResultList();
	}

	public Atendimento buscarAtendimentoComMedico(Long idAtendimento) {

		CriteriaBuilder cBuilder = getEntityManager().getCriteriaBuilder();

		CriteriaQuery<Atendimento> cQuery = cBuilder.createQuery(Atendimento.class);

		Root<Atendimento> rootAtendimento = cQuery.from(Atendimento.class);

		rootAtendimento.fetch("medicos", JoinType.INNER);

		cQuery.select(rootAtendimento).where(cBuilder.equal(rootAtendimento.get("id"), idAtendimento))
				.orderBy(cBuilder.asc(rootAtendimento.get("numero")));

		return getEntityManager().createQuery(cQuery).getSingleResult();
	}

}
