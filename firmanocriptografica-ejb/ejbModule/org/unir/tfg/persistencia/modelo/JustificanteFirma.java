package org.unir.tfg.persistencia.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the JUSTIFICANTESFIRMA database table.
 * 
 */
@Entity
@Table(name="JUSTIFICANTESFIRMA")
@NamedQuery(name="JustificanteFirma.findAll", query="SELECT j FROM JustificanteFirma j")
public class JustificanteFirma implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="JUSTIFICANTESFIRMA_ID_GENERATOR", sequenceName="TFG_SECUENCIA", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="JUSTIFICANTESFIRMA_ID_GENERATOR")
	@Column(unique=true, nullable=false, precision=19)
	private long id;

	@Column(nullable=false, length=50)
	private String codigoFirma;

	@Lob
	@Column(nullable=false)
	private byte[] contenidoJustificante;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date fechaFirma;

	@Column(nullable=false, length=255)
	private String ipOrigenFirma;

	@Column(nullable=false, length=255)
	private String navegador;

	//bi-directional many-to-one association to Documento
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="FKDOCUMENTO")
	private Documento documento;

	//bi-directional many-to-one association to Usuario
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="FKUSUARIO")
	private Usuario usuario;

	public JustificanteFirma() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCodigoFirma() {
		return this.codigoFirma;
	}

	public void setCodigoFirma(String codigoFirma) {
		this.codigoFirma = codigoFirma;
	}

	public byte[] getContenidoJustificante() {
		return this.contenidoJustificante;
	}

	public void setContenidoJustificante(byte[] contenidoJustificante) {
		this.contenidoJustificante = contenidoJustificante;
	}

	public Date getFechaFirma() {
		return this.fechaFirma;
	}

	public void setFechaFirma(Date fechaFirma) {
		this.fechaFirma = fechaFirma;
	}

	public String getIpOrigenFirma() {
		return this.ipOrigenFirma;
	}

	public void setIpOrigenFirma(String ipOrigenFirma) {
		this.ipOrigenFirma = ipOrigenFirma;
	}

	public String getNavegador() {
		return this.navegador;
	}

	public void setNavegador(String navegador) {
		this.navegador = navegador;
	}

	public Documento getDocumento() {
		return this.documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}

	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}