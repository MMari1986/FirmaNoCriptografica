package org.unir.tfg.persistencia.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the USUARIOS database table.
 * 
 */
@Entity
@Table(name="USUARIOS")
@NamedQuery(name="Usuario.findAll", query="SELECT u FROM Usuario u")
public class Usuario implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="USUARIOS_ID_GENERATOR", sequenceName="TFG_SECUENCIA", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="USUARIOS_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=19)
	private long id;

	@Column(nullable=false, length=255)
	private String apellido1;

	@Column(length=255)
	private String apellido2;

	@Column(length=255)
	private String campoExtraValidacion;

	@Column(nullable=false, length=1023)
	private String claveSecreta;

	@Column(nullable=false, length=1023)
	private String contenidoQR;

	@Column(nullable=false, length=255)
	private String correoElectronico;

	@Column(nullable=false, precision=22)
	private boolean cuentaAutorizada;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date fechaNacimiento;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date fechaRegistro;

	@Column(nullable=false, length=255)
	private String nombre;

	@Column(nullable=false, length=255)
	private String nombreUsuario;

	@Column(nullable=false, length=255)
	private String numeroIdentificacion;

	@Column(nullable=false, length=255)
	private String password;

	@Column(nullable=false, precision=22)
	private boolean validacionPresencial;

	//bi-directional many-to-one association to Documento
	@OneToMany(mappedBy="usuario", fetch=FetchType.LAZY)
	private List<Documento> documentos;

	//bi-directional many-to-one association to JustificanteFirma
	@OneToMany(mappedBy="usuario", fetch=FetchType.LAZY)
	private List<JustificanteFirma> justificantesfirmas;

	public Usuario() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getApellido1() {
		return this.apellido1;
	}

	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}

	public String getApellido2() {
		return this.apellido2;
	}

	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}

	public String getCampoExtraValidacion() {
		return this.campoExtraValidacion;
	}

	public void setCampoExtraValidacion(String campoExtraValidacion) {
		this.campoExtraValidacion = campoExtraValidacion;
	}

	public String getClaveSecreta() {
		return this.claveSecreta;
	}

	public void setClaveSecreta(String claveSecreta) {
		this.claveSecreta = claveSecreta;
	}

	public String getContenidoQR() {
		return this.contenidoQR;
	}

	public void setContenidoQR(String contenidoQR) {
		this.contenidoQR = contenidoQR;
	}

	public String getCorreoElectronico() {
		return this.correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	public boolean getCuentaAutorizada() {
		return this.cuentaAutorizada;
	}

	public void setCuentaAutorizada(boolean cuentaAutorizada) {
		this.cuentaAutorizada = cuentaAutorizada;
	}

	public Date getFechaNacimiento() {
		return this.fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public Date getFechaRegistro() {
		return this.fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNombreUsuario() {
		return this.nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getNumeroIdentificacion() {
		return this.numeroIdentificacion;
	}

	public void setNumeroIdentificacion(String numeroIdentificacion) {
		this.numeroIdentificacion = numeroIdentificacion;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getValidacionPresencial() {
		return this.validacionPresencial;
	}

	public void setValidacionPresencial(boolean validacionPresencial) {
		this.validacionPresencial = validacionPresencial;
	}

	public List<Documento> getDocumentos() {
		return this.documentos;
	}

	public void setDocumentos(List<Documento> documentos) {
		this.documentos = documentos;
	}

	public Documento addDocumento(Documento documento) {
		getDocumentos().add(documento);
		documento.setUsuario(this);

		return documento;
	}

	public Documento removeDocumento(Documento documento) {
		getDocumentos().remove(documento);
		documento.setUsuario(null);

		return documento;
	}

	public List<JustificanteFirma> getJustificantesfirmas() {
		return this.justificantesfirmas;
	}

	public void setJustificantesfirmas(List<JustificanteFirma> justificantesfirmas) {
		this.justificantesfirmas = justificantesfirmas;
	}

	public JustificanteFirma addJustificantesfirma(JustificanteFirma justificantesfirma) {
		getJustificantesfirmas().add(justificantesfirma);
		justificantesfirma.setUsuario(this);

		return justificantesfirma;
	}

	public JustificanteFirma removeJustificantesfirma(JustificanteFirma justificantesfirma) {
		getJustificantesfirmas().remove(justificantesfirma);
		justificantesfirma.setUsuario(null);

		return justificantesfirma;
	}

}