package org.unir.tfg.persistencia.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the DOCUMENTOS database table.
 * 
 */
@Entity
@Table(name="DOCUMENTOS")
@NamedQuery(name="Documento.findAll", query="SELECT d FROM Documento d")
public class Documento implements Serializable  {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="DOCUMENTOS_ID_GENERATOR", sequenceName="TFG_SECUENCIA", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DOCUMENTOS_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=19)
	private long id;

	@Column(nullable=false, length=255)
	private String codigoSeguroVerificacion;

	@Lob
	@Column(nullable=false)
	@Basic(fetch = FetchType.LAZY)
	private byte[] contenido;

	@Column(nullable=false, length=127)
	private String nombreDocumento;

	//bi-directional many-to-one association to Usuario
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="FKUSUARIOPROPIETARIO")
	private Usuario usuario;

	//bi-directional many-to-one association to JustificanteFirma
	@OneToMany(mappedBy="documento", fetch=FetchType.LAZY)
	private List<JustificanteFirma> justificantesfirmas;

	public Documento() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCodigoSeguroVerificacion() {
		return this.codigoSeguroVerificacion;
	}

	public void setCodigoSeguroVerificacion(String codigoSeguroVerificacion) {
		this.codigoSeguroVerificacion = codigoSeguroVerificacion;
	}

	public byte[] getContenido() {
		return this.contenido;
	}

	public void setContenido(byte[] contenido) {
		this.contenido = contenido;
	}

	public String getNombreDocumento() {
		return this.nombreDocumento;
	}

	public void setNombreDocumento(String nombreDocumento) {
		this.nombreDocumento = nombreDocumento;
	}

	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<JustificanteFirma> getJustificantesfirmas() {
		return this.justificantesfirmas;
	}

	public void setJustificantesfirmas(List<JustificanteFirma> justificantesfirmas) {
		this.justificantesfirmas = justificantesfirmas;
	}

	public JustificanteFirma addJustificantesfirma(JustificanteFirma justificantesfirma) {
		getJustificantesfirmas().add(justificantesfirma);
		justificantesfirma.setDocumento(this);

		return justificantesfirma;
	}

	public JustificanteFirma removeJustificantesfirma(JustificanteFirma justificantesfirma) {
		getJustificantesfirmas().remove(justificantesfirma);
		justificantesfirma.setDocumento(null);

		return justificantesfirma;
	}

}