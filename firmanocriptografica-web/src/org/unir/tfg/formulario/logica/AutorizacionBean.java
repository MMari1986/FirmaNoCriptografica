package org.unir.tfg.formulario.logica;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.unir.tfg.formulario.utilidades.Constantes;
import org.unir.tfg.formulario.utilidades.Utils;
import org.unir.tfg.persistencia.fachadas.FachadaFirma;
import org.unir.tfg.persistencia.modelo.Usuario;

import com.warrenstrange.googleauth.GoogleAuthenticator;

@Named("autorizacionBean")
@RequestScoped
public class AutorizacionBean implements Serializable {

	private static final long serialVersionUID = -2836783233658676408L;
	
	@EJB
	FachadaFirma fachadaGestionFirmas;
	
	Usuario usuario;
	String codigoFirma;
	String numeroIdentificacion;
	
	Utils utilidades;
	
	public AutorizacionBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@PostConstruct
	public void init(){	
		utilidades = new Utils();
		
		//Buscar el usuario que se esta autorizando
		try {
			HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
			this.numeroIdentificacion = req.getParameter("numeroIdentificacion");
			if(numeroIdentificacion!= null && !numeroIdentificacion.isEmpty()) {
				this.usuario = fachadaGestionFirmas.buscarUsuarioPorNumeroIdentificacion(numeroIdentificacion);
			}
		}catch(Exception e) {
			e.printStackTrace();	
		}
	}
	
	public String autorizaCuentaUsuario() {
		this.usuario = fachadaGestionFirmas.buscarUsuarioPorNumeroIdentificacion(numeroIdentificacion);
		GoogleAuthenticator gAuth = new GoogleAuthenticator();

		try {
			if(gAuth.authorize(usuario.getClaveSecreta(), Integer.parseInt(codigoFirma))) {
				//Guardar la información en la BBDD
				usuario = fachadaGestionFirmas.buscarUsuarioPorNumeroIdentificacion(usuario.getNumeroIdentificacion());
				usuario.setCuentaAutorizada(true);
				usuario = fachadaGestionFirmas.actualizaUsuario(usuario);
				utilidades.addMessage(FacesMessage.SEVERITY_INFO, "Autorización", "Cuenta activada correctamente");
			}else {
				utilidades.addMessage(FacesMessage.SEVERITY_WARN, "Autorización", "Cuenta NO activada correctamente");
				return Constantes.PAGINA_AUTORIZAUSUARIO + "&numeroIdentificacion=" + usuario.getNumeroIdentificacion();
			}
		}catch(Exception e) {
			utilidades.addMessage(FacesMessage.SEVERITY_ERROR, "Autorización", e.getMessage());

		}
		return Constantes.PAGINA_INICIO;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getCodigoFirma() {
		return codigoFirma;
	}

	public void setCodigoFirma(String codigoFirma) {
		this.codigoFirma = codigoFirma;
	}

	public String getNumeroIdentificacion() {
		return numeroIdentificacion;
	}

	public void setNumeroIdentificacion(String numeroIdentificacion) {
		this.numeroIdentificacion = numeroIdentificacion;
	}

	
	

}
