package control;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import model.Atendimento;
import model.Medico;
import model.Situacao;
import service.AtendimentoService;
import service.MedicoService;

@ManagedBean
@ViewScoped
public class RelatorioBean {

	@EJB
	private AtendimentoService atendimentoService;

	@EJB
	private MedicoService medicoService;

	private Date dataInicio;
	private Date dataTermino;
	private Atendimento atendimentoEscolhido;
	private Boolean selecionarTodosMedicos;
	private List<Medico> medicos;
	private Long idMedicoSelecionado;
	private String parecer;
	private List<Atendimento> atendimentos;
	private Situacao situacao;
	private Atendimento medicoAtendimento = new Atendimento();

	@PostConstruct
	public void init() {
		inicializarObjetos();
		carregarAtendimentos();
		carregarMedicos();
	}

	private void inicializarObjetos() {
		medicos = new ArrayList<>();
		atendimentos = new ArrayList<>();
		idMedicoSelecionado = 0L;
		selecionarTodosMedicos = false;
	}

	private void carregarAtendimentos() {
		try {
			atendimentos = atendimentoService.listarAtendimentos();
		} catch (Exception e) {
			msgErro("Erro ao carregar atendimentos: " + e.getMessage());
		}
	}

	private void carregarMedicos() {
		try {
			medicos = medicoService.listarMedicos();
		} catch (Exception e) {
			msgErro("Erro ao carregar médicos: " + e.getMessage());
		}
	}

	public void carregarMedico(Atendimento atendimento) {
		try {
			medicoAtendimento = medicoService.buscarAtendimentoComMedico(atendimento.getId());
		} catch (Exception e) {
			msgErro("Erro ao carregar médicos do atendimento: " + e.getMessage());
		}
	}

	private void msgErro(String mensagem) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, null));
	}

	private void msgInfo(String mensagem) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, mensagem, null));
	}

	public void filtrar() {
		try {
			if (dataInicio != null && dataTermino != null && dataInicio.after(dataTermino)) {
				msgErro("Data de início não pode ser posterior à data de fim.");
				return;
			}

			if (dataInicio == null && dataTermino == null
					&& (selecionarTodosMedicos || idMedicoSelecionado == null || idMedicoSelecionado == 0)) {
				atendimentos = atendimentoService.listarAtendimentos();
			} else if (selecionarTodosMedicos || idMedicoSelecionado == null || idMedicoSelecionado == 0) {
				atendimentos = atendimentoService.filtrarAtendimentos(dataInicio, dataTermino, null);
			} else {
				atendimentos = atendimentoService.filtrarAtendimentos(dataInicio, dataTermino, idMedicoSelecionado);
			}

			if (atendimentos.isEmpty()) {
				msgInfo("Nenhum atendimento encontrado!");
			}
		} catch (Exception e) {
			msgErro("Erro ao filtrar atendimentos: " + e.getMessage());
		}
	}

	public void finalizar(Atendimento atendimento) {
		this.atendimentoEscolhido = atendimento;
		this.parecer = "";
	}

	public void confirmarFinalizacao() {
		if (parecer == null || parecer.trim().isEmpty()) {
			msgErro("O parecer é obrigatório!");
			return;
		}
		try {
			atendimentoService.finalizarAtendimento(atendimentoEscolhido.getId(), parecer);
			msgInfo("Atendimento finalizado com sucesso!");
			filtrar();
			parecer = null;
		} catch (IllegalArgumentException e) {
			msgErro(e.getMessage());
		}
	}

	public void excluir(Atendimento atendimento) {
		this.atendimentoEscolhido = atendimento;
	}

	public void confirmarExclusao() {
		try {
			atendimentoService.excluirAtendimento(atendimentoEscolhido.getId());
			atendimentos.remove(atendimentoEscolhido);
			msgInfo("Atendimento excluído com sucesso!");
		} catch (IllegalStateException e) {
			msgErro(e.getMessage());
		} catch (Exception e) {
			msgErro("Erro ao excluir atendimento!");
		}
	}

	public List<Medico> getMedicos() {
		return medicos;
	}

	public void setMedicos(List<Medico> medicos) {
		this.medicos = medicos;
	}

	public List<Atendimento> getAtendimentos() {
		return atendimentos;
	}

	public void setAtendimentos(List<Atendimento> atendimentos) {
		this.atendimentos = atendimentos;
	}

	public Atendimento getMedicoAtendimento() {
		return medicoAtendimento;
	}

	public void setMedicoAtendimento(Atendimento medicoAtendimento) {
		this.medicoAtendimento = medicoAtendimento;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataTermino() {
		return dataTermino;
	}

	public void setDataTermino(Date dataTermino) {
		this.dataTermino = dataTermino;
	}

	public Long getIdMedicoSelecionado() {
		return idMedicoSelecionado;
	}

	public void setIdMedicoSelecionado(Long idMedicoSelecionado) {
		this.idMedicoSelecionado = idMedicoSelecionado;
	}

	public String getParecer() {
		return parecer;
	}

	public void setParecer(String parecer) {
		this.parecer = parecer;
	}

	public Atendimento getAtendimentoEscolhido() {
		return atendimentoEscolhido;
	}

	public void setAtendimentoEscolhido(Atendimento atendimentoEscolhido) {
		this.atendimentoEscolhido = atendimentoEscolhido;
	}

	public Boolean getSelecionarTodosMedicos() {
		return selecionarTodosMedicos;
	}

	public void setSelecionarTodosMedicos(Boolean selecionarTodosMedicos) {
		this.selecionarTodosMedicos = selecionarTodosMedicos;
	}

	public Situacao getSituacao() {
		return situacao;
	}

	public void setSituacao(Situacao situacao) {
		this.situacao = situacao;
	}

}
