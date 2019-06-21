package org.unir.tfg.formulario.logica;


import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.unir.tfg.formulario.utilidades.Constantes;
import org.unir.tfg.formulario.utilidades.GeneradorCSV;
import org.unir.tfg.formulario.utilidades.Mail;
import org.unir.tfg.formulario.utilidades.Utils;
import org.unir.tfg.persistencia.fachadas.FachadaFirma;
import org.unir.tfg.persistencia.modelo.Documento;
import org.unir.tfg.persistencia.modelo.JustificanteFirma;
import org.unir.tfg.persistencia.modelo.Usuario;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

@Named("logicaBean")
@SessionScoped
public class LogicaBean implements Serializable {

	private static final long serialVersionUID = -575620218868403586L;
	private static Logger logger =  Logger.getLogger(LogicaBean.class);

	@EJB
	FachadaFirma fachadaGestionFirmas;
	
	private Locale locale;
	private String codigoFirma;
	private String  passwordValidacion;
	private String direccionIP;
	private String  navegador;
	
	
	private UploadedFile uploadedFile;
	
	private Usuario usuario;
	private List<Documento> documentos;
	
	
	Utils utilidades;

	public LogicaBean() {
		super();
		logger.warn("Inicializando LogicaBean");
		//Aquí aun no estan inicializados los objetos EJB se usa el método init
	}
	
	@PostConstruct
	public void init(){
		this.locale = new Locale("es");
		FacesContext.getCurrentInstance().getViewRoot().setLocale(this.locale);
		usuario = new Usuario();
		utilidades = new Utils();
		documentos = new ArrayList<Documento>();
		
		direccionIP= (String) MDC.get("ip");
		navegador= (String) MDC.get("navegador");
		
	}
	
	public boolean isSesionIniciada() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
		
		Object loggedIn = session.getAttribute("USUARIO");
		if(loggedIn == null) {
			return false;
		}
		return true;
	}	
	
	public void isLogged() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
		
		Object loggedIn = session.getAttribute("USUARIO");
		if(loggedIn == null) {
			FacesContext.getCurrentInstance().getExternalContext().redirect(Constantes.PAGINA_INICIO);
		}
	}	

	
	public String registrarUsuario() {
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		try {
			
			//Comprobar que no existe ningun usuario con ese número de isentificacion
			Usuario usuarioDTO = fachadaGestionFirmas.buscarUsuarioPorNumeroIdentificacion(usuario.getNumeroIdentificacion());
			if(usuarioDTO.getNumeroIdentificacion() == null) {
				//Generar la imagen de google autenticator a mostrar
				GoogleAuthenticatorKey key = gAuth.createCredentials();
				usuario.setClaveSecreta(key.getKey());
				usuario.setContenidoQR(GoogleAuthenticatorQRGenerator.getOtpAuthURL(Constantes.NOMBRE_APLICACION, usuario.getNombreUsuario(), key));
				
				//Crear usuario en la BBDD
				usuario.setFechaRegistro(new Date());
				fachadaGestionFirmas.crearUsuario(usuario);
				
				//Validar el campo extra de validacion utilizando el servicio SCSP
				//TODO
				//Enviar correo validación
				new Mail().enviarCorreoElectronicoActivacion(usuario.getCorreoElectronico(), usuario.getNumeroIdentificacion());

				utilidades.addMessage(FacesMessage.SEVERITY_INFO, "Registro", "Cuenta registrada correctamente. Valide su usuario.");
			}else {
				utilidades.addMessage(FacesMessage.SEVERITY_WARN, "Registro", "Usuario ya registrado. Utilice la opción de recuperar contraseña");
			}			
		}catch(Exception e) {
			e.printStackTrace();
			utilidades.addMessage(FacesMessage.SEVERITY_ERROR, "Registro", e.getMessage());
		}
		return Constantes.PAGINA_INICIO;
	}


	public String autorizaUsuario() {
		if (usuario.getNombreUsuario() != null && usuario.getPassword()!= null) {
			Usuario usuarioDAO = fachadaGestionFirmas.buscarUsuarioPorNombreUsuario(usuario.getNombreUsuario());
			if(usuario.getPassword().equalsIgnoreCase(usuarioDAO.getPassword()) && usuarioDAO.getCuentaAutorizada()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
				session.setAttribute("USUARIO",  usuario.getNombreUsuario());
				this.usuario = usuarioDAO;
				utilidades.addMessage(FacesMessage.SEVERITY_INFO, "Bienvenid@", usuario.getNombreUsuario());
				return Constantes.PAGINA_PERFILUSUARIO;
			}
		}
		utilidades.addMessage(FacesMessage.SEVERITY_WARN, "Identificación", "Credenciales no válidas");
		return Constantes.PAGINA_ACTUAL;
	}
	

	public String terminarAutorizacion() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session.invalidate();
		utilidades.addMessage(FacesMessage.SEVERITY_INFO, "No olvide cerra el navegador usuario ", usuario.getNombreUsuario());
		this.usuario = new Usuario();
		logger.debug("Terminando sesion");
		return Constantes.PAGINA_INICIO;		
	}
	
	public String changePassword() {
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		
		if(usuario.getNumeroIdentificacion() != null && usuario.getPassword() != null && this.codigoFirma != null) {
			//1.- Buscar el usuario en la BBDD
			Usuario usuarioDAO = fachadaGestionFirmas.buscarUsuarioPorNumeroIdentificacion(usuario.getNumeroIdentificacion());
			//2.- Cmprobar el código de firma
			if(gAuth.authorize(usuarioDAO.getClaveSecreta(), Integer.parseInt(this.codigoFirma))) {
				//3.- Comprobar que la contraseña nueva no coincide
				if(!usuarioDAO.getPassword().equalsIgnoreCase(usuario.getPassword())) {
					//4.- Actualizar la nueva contraseña
					usuarioDAO.setPassword(usuario.getPassword());
					fachadaGestionFirmas.actualizaUsuario(usuarioDAO);
					utilidades.addMessage(FacesMessage.SEVERITY_INFO, "Cambiar password", "Contraseña actualizada correctamente");
				}else {
					utilidades.addMessage(FacesMessage.SEVERITY_WARN, "Cambiar password", "La nueva contraseña coincide con la anterior");
				}
			}else {
				utilidades.addMessage(FacesMessage.SEVERITY_INFO, "Cambiar password", "Código de firma incorrecto");
				return Constantes.PAGINA_ACTUAL;
			}
			
		}
		
		return Constantes.PAGINA_LOGIN;
	}
	
	public void preGenerarCodigo() {
		
		//Generamos una nueva clave secreta
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		GoogleAuthenticatorKey key = gAuth.createCredentials();
		usuario.setClaveSecreta(key.getKey());
		usuario.setContenidoQR(GoogleAuthenticatorQRGenerator.getOtpAuthURL(Constantes.NOMBRE_APLICACION, usuario.getNombreUsuario(), key));
	}
	
	public String generarCodigo() {
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
	
		if(gAuth.authorize(usuario.getClaveSecreta(), Integer.parseInt(codigoFirma))) {
			Usuario usuarioDAO = fachadaGestionFirmas.buscarUsuarioPorNumeroIdentificacion(usuario.getNumeroIdentificacion());
			usuarioDAO.setClaveSecreta(usuario.getClaveSecreta());
			usuario.setContenidoQR(usuario.getContenidoQR());
			fachadaGestionFirmas.actualizaUsuario(usuarioDAO);
			utilidades.addMessage(FacesMessage.SEVERITY_INFO, "Generar código", "Clave secreta actualizada con éxito");
			return Constantes.PAGINA_INICIO;
		}else {
			utilidades.addMessage(FacesMessage.SEVERITY_WARN, "Generar código", "Clave secreta incorrecta");	
			return Constantes.PAGINA_ACTUAL;
		}	
	}
	
	public void upload(FileUploadEvent event) {
	    this.uploadedFile = event.getFile();
	   
	    try {
	    	Documento documento = new Documento();
	 	    documento.setNombreDocumento(uploadedFile.getFileName());
	 	    documento.setContenido(uploadedFile.getContents());
			documento.setCodigoSeguroVerificacion(new GeneradorCSV(uploadedFile.getContents()).getCodigoSeguroVerificacion());
			this.documentos.add(documento);
			utilidades.addMessage(FacesMessage.SEVERITY_INFO, "Upload documento", "Documento incluido correctamente");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			utilidades.addMessage(FacesMessage.SEVERITY_ERROR, "Upload documento", e.getMessage());
		}
	   
	}
	
	public void delete() {
		
	}
	
	public String firmarDocumento() throws IOException {
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
	
		if(gAuth.authorize(usuario.getClaveSecreta(), Integer.parseInt(codigoFirma))) {
			Usuario usuarioDAO = fachadaGestionFirmas.buscarUsuarioPorNumeroIdentificacion(usuario.getNumeroIdentificacion());

			for(Documento documento: documentos) {
				
				//CreaR el documento en la BBDD
				documento.setUsuario(usuarioDAO);
				fachadaGestionFirmas.crearDocumento(documento);
				
				//Crear la firma en la BBDD
				JustificanteFirma firma = new JustificanteFirma();
				firma.setCodigoFirma(codigoFirma);
				firma.setFechaFirma(new Date());
				firma.setIpOrigenFirma((String) MDC.get("ip"));
				firma.setNavegador((String) MDC.get("navegador"));
				String contenidoJustificante = "Contenido" + usuario.getNombreUsuario() + MDC.get("ip") + MDC.get("navegador");
				firma.setContenidoJustificante(contenidoJustificante.getBytes());
				firma.setUsuario(usuarioDAO);
				firma.setDocumento(documento);
				fachadaGestionFirmas.crearFirma(firma);
			}
			
			utilidades.addMessage(FacesMessage.SEVERITY_INFO, "Firmar documentos", "Clave secreta actualizada con éxito");
			documentos = new ArrayList<Documento>();
			return Constantes.PAGINA_ACTUAL;
		}else {
			utilidades.addMessage(FacesMessage.SEVERITY_WARN, "Firmar documentos", "Clave secreta incorrecta");	
			return Constantes.PAGINA_ACTUAL;
		}	
	}
	
	
	public String cambiarDatos() {
		logger.debug("Modificando datos de usuario");
		try {
			Usuario usuarioDAO = fachadaGestionFirmas.buscarUsuarioPorNumeroIdentificacion(usuario.getNumeroIdentificacion());
			
			//Insertar campo o modificables
			usuarioDAO.setNombre(usuario.getNombre());
			usuarioDAO.setApellido1(usuario.getApellido1());
			usuarioDAO.setApellido2(usuario.getApellido2());
			usuarioDAO.setCampoExtraValidacion(usuario.getCampoExtraValidacion());
			usuarioDAO.setFechaNacimiento(usuario.getFechaNacimiento());
			
			//Comprobar si se ha modificado la cuenta de correo
			if(!usuarioDAO.getCorreoElectronico().equalsIgnoreCase(usuario.getCorreoElectronico())) {
				usuario.setCuentaAutorizada(false);
				usuarioDAO.setCorreoElectronico(usuario.getCorreoElectronico());
				//Enviar correo validación
				new Mail().enviarCorreoElectronicoActivacion(usuario.getCorreoElectronico(), usuario.getNumeroIdentificacion());
				utilidades.addMessage(FacesMessage.SEVERITY_WARN, "Modificación", "Valide su usuario mediante el enlace enviado");
			}
			utilidades.addMessage(FacesMessage.SEVERITY_INFO, "Modificación", "Modificación realizada correctamente");
			fachadaGestionFirmas.actualizaUsuario(usuarioDAO);
		
		}catch(Exception e) {
			e.printStackTrace();
			utilidades.addMessage(FacesMessage.SEVERITY_ERROR, "Modificación", e.getMessage());
		}
		return Constantes.PAGINA_INICIO;
	}
	
	public String verificarDocumento() {
		return Constantes.PAGINA_ACTUAL;
	}
	
	public void preVerDocumentosUsuario() {
		documentos = fachadaGestionFirmas.getDocumentosUsuario(usuario.getNumeroIdentificacion());
	}
	

	
	public String getLanguage() {
        return locale.getLanguage();
    }
	

	public String getLanguageDescription() {
		return locale.getDisplayName();
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getCodigoFirma() {
		return codigoFirma;
	}

	public void setCodigoFirma(String codigoFirma) {
		this.codigoFirma = codigoFirma;
	}

	public String getPasswordValidacion() {
		return passwordValidacion;
	}

	public void setPasswordValidacion(String passwordValidacion) {
		this.passwordValidacion = passwordValidacion;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Documento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<Documento> documentos) {
		this.documentos = documentos;
	}

	public String getDireccionIP() {
		return direccionIP;
	}

	public void setDireccionIP(String direccionIP) {
		this.direccionIP = direccionIP;
	}

	public String getNavegador() {
		return navegador;
	}

	public void setNavegador(String navegador) {
		this.navegador = navegador;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}


}
