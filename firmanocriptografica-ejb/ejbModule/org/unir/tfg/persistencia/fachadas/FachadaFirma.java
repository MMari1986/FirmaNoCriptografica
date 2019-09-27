package org.unir.tfg.persistencia.fachadas;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.unir.tfg.persistencia.modelo.Documento;
import org.unir.tfg.persistencia.modelo.JustificanteFirma;
import org.unir.tfg.persistencia.modelo.Usuario;

/**
 * Session Bean implementation class FachadaFirma
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class FachadaFirma {
	private static Logger logger =  Logger.getLogger(FachadaFirma.class);


	@PersistenceContext
	private EntityManager entityManager;
	
	public Usuario crearUsuario(Usuario usuario){
		entityManager.persist(usuario);
		entityManager.flush();
		return usuario;
    }
	
	public Documento crearDocumento(Documento documento){
		entityManager.persist(documento);
		entityManager.flush();
		return documento;
		
    }
	
	public JustificanteFirma crearFirma(JustificanteFirma firma){
		entityManager.persist(firma);
		entityManager.flush();
		return firma;
    }
	
	public void eliminarUsuario(String sid){
		Usuario usuario = entityManager.find(Usuario.class, sid);
		entityManager.remove(usuario);
    }
	
	public void eliminarDocumento(String sid){
		Documento documento = entityManager.find(Documento.class, sid);
		entityManager.remove(documento);
    }
	
	public void eliminarFirma(String sid){
		JustificanteFirma firma = entityManager.find(JustificanteFirma.class, sid);
		entityManager.remove(firma);
    }
	
	
	public Usuario mergeUsuario(Usuario usuario){
		return entityManager.merge(usuario);
    }
	
	public Documento  mergeDocumento(Documento documento){
		return entityManager.merge(documento);
    }
	
	public JustificanteFirma  mergeFirma(JustificanteFirma firma){
		return entityManager.merge(firma);
    }
	
	public Usuario obtenerUsuario(String sid){
		Usuario usuario = entityManager.find(Usuario.class, sid);
		return usuario;
    }
	
	public Documento obtenerDocumento(String sid){
		Documento documento = entityManager.find(Documento.class, sid);
		return documento;
    }
	
	public JustificanteFirma obtenerFirma(String sid){
		JustificanteFirma justificante = entityManager.find(JustificanteFirma.class, sid);
		return justificante;
		
    }	
	
	
	public Usuario buscarUsuarioPorNumeroIdentificacion(String numeroIdentificacion){
		Usuario usuario = new Usuario();
		TypedQuery<Usuario> listaUsuarios = entityManager.createQuery(
				"SELECT u FROM Usuario u WHERE u.numeroIdentificacion=:parametro1", Usuario.class)
				.setParameter("parametro1", numeroIdentificacion);
		 entityManager.flush();
		 if(listaUsuarios != null && !listaUsuarios.getResultList().isEmpty()) {
			 usuario = listaUsuarios.getResultList().get(0);
		 }
		 return usuario;
		
		
    }
	
	public Usuario buscarUsuarioPorNombreUsuario(String nombreUsuario){
		Usuario usuario = new Usuario();
		logger.info("Buscando usuario: " + nombreUsuario);
		TypedQuery<Usuario> listaUsuarios = entityManager.createQuery(
				"SELECT u FROM Usuario u WHERE u.nombreUsuario=:parametro1", Usuario.class)
				.setParameter("parametro1", nombreUsuario);
		entityManager.flush();
		if(listaUsuarios!= null && !listaUsuarios.getResultList().isEmpty()) {
			usuario = listaUsuarios.getResultList().get(0);
		}
		return usuario;
    }
	
	public List<Documento> getDocumentosUsuario(String numeroIdentificacion){
		List<Documento> documentos = null;
		Usuario usuario = null;
		
		TypedQuery<Usuario> listaUsuarios = entityManager.createQuery(
				"SELECT u FROM Usuario u JOIN FETCH u.documentos WHERE u.numeroIdentificacion=:parametro1", Usuario.class)
				.setParameter("parametro1", numeroIdentificacion);
		
		entityManager.flush();
		 if(listaUsuarios != null && !listaUsuarios.getResultList().isEmpty()) {
			 usuario = listaUsuarios.getResultList().get(0);
			 documentos = usuario.getDocumentos();
		 }
		
		return documentos;
	}
	
}
