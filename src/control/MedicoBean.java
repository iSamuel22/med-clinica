package control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import model.Endereco;
import model.Medico;
import service.EnderecoService;
import service.MedicoService;

@ViewScoped
@ManagedBean
public class MedicoBean {

	@EJB
	private MedicoService medicoService;

	@EJB
	private EnderecoService enderecoService;

	private Medico medico;
	private List<Medico> medicos;

	@PostConstruct
	public void init() {
		inicializarObjetos();
		atualizarListaMedicos();
	}

	public void inicializarObjetos() {
		medico = new Medico();
		medico.setEndereco(new Endereco());
		medicos = new ArrayList<>();
	}

	public void atualizarListaMedicos() {
		medicos = medicoService.listAll();
		Collections.sort(medicos, Comparator.comparing(Medico::getPrimeiroNome, String.CASE_INSENSITIVE_ORDER));
	}

	public void gravar() {
		String mensagem;

		if (isNovoMedico()) {
			medicoService.create(medico);
			mensagem = "Médico(a) cadastrado(a) com sucesso!";
		} else {
			medicoService.merge(medico);
			mensagem = "Médico(a) editado(a) com sucesso!";
		}

		exibirMensagem(mensagem);
		limparFormulario();
		atualizarListaMedicos();
		medico = new Medico();
	}

	public void editarMedico(Medico m) {
		medico = medicoService.obtemPorId(m.getId());
	}

	public void deletarMedico(Medico m) {
		String mensagem;
		try {
			medicoService.remove(m);
			mensagem = "Médico(a) deletado(a) com sucesso.";
		} catch (Exception e) {
			mensagem = "Não foi possível deletar o(a) médico(a): " + e.getMessage();
		}
		exibirMensagem(mensagem);
		atualizarListaMedicos();
	}

	private boolean isNovoMedico() {
		return medico.getId() == null;
	}

	private void limparFormulario() {
		medico = new Medico();
		medico.setEndereco(new Endereco());
	}

	private void exibirMensagem(String mensagem) {
		FacesContext.getCurrentInstance().addMessage("", new FacesMessage(mensagem));
	}

	public Medico getMedico() {
		return medico;
	}

	public void setMedico(Medico medico) {
		this.medico = medico;
	}

	public List<Medico> getMedicos() {
		return medicos;
	}

	public void setMedicos(List<Medico> medicos) {
		this.medicos = medicos;
	}
}
