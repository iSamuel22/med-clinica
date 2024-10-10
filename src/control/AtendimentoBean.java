package control;

import javax.faces.bean.ViewScoped;
import javax.faces.bean.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import model.Atendimento;
import model.Endereco;
import model.Medico;
import model.Paciente;
import model.Situacao;
import service.AtendimentoService;
import service.EnderecoService;
import service.MedicoService;
import service.PacienteService;

import java.util.List;
import java.util.Random;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;

@ViewScoped
@ManagedBean
public class AtendimentoBean {

	@EJB
	private PacienteService pacienteService;

	@EJB
	private MedicoService medicoService;

	@EJB
	private AtendimentoService atendimentoService;

	@EJB
	private EnderecoService enderecoService;

	private Paciente pacienteAtual;

	private List<Atendimento> atendimentos = new ArrayList<Atendimento>();
	private List<Medico> medicos;
	private Atendimento atendimento;
	private List<Medico> medicosSelecionados;
	private List<Paciente> pacientes;
	private Paciente paciente = new Paciente();
	private Paciente pacienteAux = new Paciente();
	private boolean showCampos;

	@PostConstruct
	public void init() {
		inicializarObjetos();
		carregarAtendimentos();
		atualizarPaciente();
	}

	private void inicializarObjetos() {
		medicos = medicoService.listAll();
		atendimento = new Atendimento();
		paciente = new Paciente();
		showCampos = false;
		paciente.setEndereco(new Endereco());
		medicosSelecionados = new ArrayList<Medico>();
		paciente = new Paciente();
		paciente.setEndereco(new Endereco());
	}

	private void carregarAtendimentos() {
		atendimentos = atendimentoService.listAll();
	}

	private boolean isCpfValido(String cpf) {
		return cpf != null && !cpf.trim().isEmpty();
	}

	private boolean isMedicosSelecionadosValidos() {
		return medicosSelecionados != null && !medicosSelecionados.isEmpty();
	}

	public void buscarPaciente() {

		String mensagem = "";

		if (isCpfValido(pacienteAux.getCpf())) {
			try {
				Paciente pacienteAchado = pacienteService.buscarPorCpf(pacienteAux.getCpf());

				if (pacienteAchado != null) {
					paciente = pacienteAchado;
					mensagem = "Paciente encontrado!";
					showCampos = true;
				} else {
					inicializarNovoPaciente(pacienteAux.getCpf());
					mensagem = "Paciente não encontrado, faça o cadastro!";
				}

				pacienteAux = new Paciente();
				msgInfo(mensagem);

			} catch (Exception e) {
				mensagem = "Falha ao buscar o paciente. Tipo: " + e.getClass().getName() + " - Mensagem: "
						+ e.getMessage();
				msgErro(mensagem);
			}
		} else {
			mensagem = "O CPF fornecido é inválido!";
			msgErro(mensagem);
		}
	}

	private void inicializarNovoPaciente(String cpf) {
		paciente = new Paciente();
		paciente.setCpf(cpf);
		paciente.setEndereco(new Endereco());
		showCampos = true;
		pacienteAux = new Paciente();
	}

	private String gerarNumeroAtendimento() {
		Random random = new Random();

		int numeroAtendimento = 10000000 + random.nextInt(90000000);

		return String.valueOf(numeroAtendimento);
	}

	public void salvar() {

		String mensagem = "";

		try {
			if (isMedicosSelecionadosValidos()) {
				prepararAtendimento();

				if (paciente.getId() == null) {
					atendimentoService.create(atendimento);
					mensagem = String.format("Atendimento " + atendimento.getNumero() + " gravado com sucesso!");
				} else {
					atendimentoService.merge(atendimento);
					mensagem = String.format("Atendimento " + atendimento.getNumero()) + " atualizado com sucesso!";
				}

				limparCampos();
			} else {
				mensagem = "Seleciona um médico!";
			}

			msgInfo(mensagem);
		} catch (Exception e) {
			mensagem = "Ocorreu um erro ao gravar o atendimento: " + e.getMessage();
			msgErro(mensagem);
		}
		
		carregarAtendimentos();
	}

	private void prepararAtendimento() {
		String numeroGerado = gerarNumeroAtendimento();
		atendimento.setNumero(Integer.valueOf(numeroGerado));
		atendimento.setPaciente(paciente);
		atendimento.setMedicos(medicosSelecionados);
		atendimento.setSituacao(Situacao.EM_ABERTO);
	}

	private void limparCampos() {
		paciente = new Paciente();
		paciente.setEndereco(new Endereco());
		medicosSelecionados = new ArrayList<>();
		atendimento = new Atendimento();
	}

	private void msgErro(String mensagem) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, null));
	}

	private void msgInfo(String mensagem) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, mensagem, null));
	}

	private void exibirMensagem(String mensagem) {
		FacesContext.getCurrentInstance().addMessage("", new FacesMessage(mensagem));
	}

	// area paciente

	private void limparFormulario() {
		paciente = new Paciente();
		paciente.setEndereco(new Endereco());
	}

	public void atualizarPaciente() {
		pacientes = pacienteService.listAll();
	}

	public void gravar() {
		String mensagem = "";

		try {
			if (paciente.getId() == null) {
				pacienteService.create(paciente);
				mensagem = "Paciente cadastrado com sucesso.";
			} else {
				pacienteService.merge(paciente);
				mensagem = "Paciente editado com sucesso.";
			}
			exibirMensagem(mensagem);
		} catch (Exception e) {
			exibirMensagem("Erro ao gravar paciente: " + e.getMessage());
		} finally {
			limparFormulario();
			atualizarPaciente();
		}
	}

	public List<Medico> getMedicos() {
		return medicos;
	}

	public void setMedicos(List<Medico> medicos) {
		this.medicos = medicos;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}

	public Atendimento getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(Atendimento atendimento) {
		this.atendimento = atendimento;
	}

	public boolean isShowCampos() {
		return showCampos;
	}

	public void setShowCampos(boolean showCampos) {
		this.showCampos = showCampos;
	}

	public Paciente getPacienteParaEncontrar() {
		return pacienteAux;
	}

	public void setPacienteParaEncontrar(Paciente pacienteParaEncontrar) {
		this.pacienteAux = pacienteParaEncontrar;
	}

	public List<Medico> getMedicosSelecionados() {
		return medicosSelecionados;
	}

	public void setMedicosSelecionados(List<Medico> medicosSelecionados) {
		this.medicosSelecionados = medicosSelecionados;
	}

	public List<Atendimento> getAtendimentos() {
		return atendimentos;
	}

	public void setAtendimentos(List<Atendimento> atendimentos) {
		this.atendimentos = atendimentos;
	}

	public Paciente getPacienteAtual() {
		return pacienteAtual;
	}

	public void setPacienteAtual(Paciente pacienteAtual) {
		this.pacienteAtual = pacienteAtual;
	}

	public List<Paciente> getPacientes() {
		return pacientes;
	}

	public void setPacientes(List<Paciente> pacientes) {
		this.pacientes = pacientes;
	}

}
