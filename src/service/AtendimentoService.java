package service;

import model.Atendimento;
import model.Medico;
import model.Situacao;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Stateless
public class AtendimentoService extends GenericService<Atendimento> {

	public AtendimentoService() {
		super(Atendimento.class);
	}

	public Atendimento buscarAtendimentoPorId(Long id) {
		return getEntityManager().find(Atendimento.class, id);
	}

	public List<Atendimento> filtrarAtendimentos(Date dataInicio, Date dataTermino, Long idMedico) {
		final CriteriaBuilder cBuilder = getEntityManager().getCriteriaBuilder();
		final CriteriaQuery<Atendimento> cQuery = cBuilder.createQuery(Atendimento.class);
		final Root<Atendimento> rootAtendimento = cQuery.from(Atendimento.class);

		List<Predicate> condicoes = new ArrayList<>();

		final Date[] dataTerminoAjustada = new Date[1];
		if (dataTermino != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataTermino);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			dataTerminoAjustada[0] = cal.getTime();
		}

		Optional.ofNullable(dataInicio).ifPresent(dataInicial -> {
			if (dataTerminoAjustada[0] != null) {
				condicoes
						.add(cBuilder.between(rootAtendimento.get("dataEntrada"), dataInicial, dataTerminoAjustada[0]));
			} else {
				condicoes.add(cBuilder.greaterThanOrEqualTo(rootAtendimento.get("dataEntrada"), dataInicial));
			}
		});

		Optional.ofNullable(dataTerminoAjustada[0]).ifPresent(dataFinal -> {
			if (dataInicio == null) {
				condicoes.add(cBuilder.lessThanOrEqualTo(rootAtendimento.get("dataEntrada"), dataFinal));
			}
		});

		Optional.ofNullable(idMedico).filter(id -> id != 0L).ifPresent(id -> {
			Join<Atendimento, Medico> joinMedico = rootAtendimento.join("medicos");
			condicoes.add(cBuilder.equal(joinMedico.get("id"), id));
		});

		cQuery.where(cBuilder.and(condicoes.toArray(new Predicate[0])));
		cQuery.orderBy(cBuilder.desc(rootAtendimento.get("dataEntrada")));

		return getEntityManager().createQuery(cQuery).getResultList();
	}

	public Atendimento buscarAtendimentoPaciente(Long idPaciente) {

		CriteriaBuilder cBuilder = getEntityManager().getCriteriaBuilder();

		CriteriaQuery<Atendimento> cQuery = cBuilder.createQuery(Atendimento.class);

		Root<Atendimento> rootAtendimento = cQuery.from(Atendimento.class);

		cQuery.select(rootAtendimento).where(cBuilder.equal(rootAtendimento.get("paciente").get("id"), idPaciente))
				.orderBy(cBuilder.desc(rootAtendimento.get("dataEntrada")));

		List<Atendimento> resultados = getEntityManager().createQuery(cQuery).getResultList();

		return resultados.isEmpty() ? null : resultados.get(0);
	}

	public List<Atendimento> listarAtendimentos() {

		final CriteriaBuilder cBuilder = getEntityManager().getCriteriaBuilder();

		final CriteriaQuery<Atendimento> cQuery = cBuilder.createQuery(Atendimento.class);

		final Root<Atendimento> rootAtendimento = cQuery.from(Atendimento.class);

		cQuery.select(rootAtendimento);

		return getEntityManager().createQuery(cQuery).getResultList();
	}

	public void finalizarAtendimento(Long idAtendimento, String parecer) {

		Optional<Atendimento> atendimentoOptional = Optional
				.ofNullable(getEntityManager().find(Atendimento.class, idAtendimento));

		Atendimento atendimento = atendimentoOptional
				.orElseThrow(() -> new IllegalArgumentException("Atendimento não encontrado"));

		Optional.ofNullable(parecer).filter(p -> !p.trim().isEmpty())
				.orElseThrow(() -> new IllegalArgumentException("Parecer é obrigatório"));

		atendimento.setSituacao(Situacao.FINALIZADO);

		atendimento.setParecer(parecer);

		getEntityManager().merge(atendimento);
	}

	public void excluirAtendimento(Long idAtendimento) {

		Atendimento atendimento = Optional.ofNullable(getEntityManager().find(Atendimento.class, idAtendimento))
				.orElseThrow(() -> new IllegalArgumentException("Atendimento não encontrado"));


		getEntityManager().remove(atendimento);
	}
}
