package org.unir.tfg.persistencia.fachadas;

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
	private EntityManager em;
	
	public Usuario crearUsuario(Usuario usuario){
		em.persist(usuario);
		em.flush();
		return usuario;
    }
	
	public Documento crearDocumento(Documento documento){
		em.persist(documento);
		em.flush();
		return documento;
		
    }
	
	public JustificanteFirma crearFirma(JustificanteFirma firma){
		em.persist(firma);
		em.flush();
		return firma;
    }
	
	public void eliminarUsuario(String sid){
		Usuario usuario = em.find(Usuario.class, sid);
		em.remove(usuario);
    }
	
	public void eliminarDocumento(String sid){
		Documento documento = em.find(Documento.class, sid);
		em.remove(documento);
    }
	
	public void eliminarFirma(String sid){
		JustificanteFirma firma = em.find(JustificanteFirma.class, sid);
		em.remove(firma);
    }
	
	
	
	public Usuario actualizaUsuario(Usuario usuario){
		return em.merge(usuario);
    }
	
	public Documento actualizaDocumento(Documento documento){
		return em.merge(documento);
    }
	
	public JustificanteFirma actualizaFirma(JustificanteFirma firma){
		return em.merge(firma);
    }
	
	public Usuario obtenerUsuario(String sid){
		Usuario usuario = em.find(Usuario.class, sid);
		return usuario;
    }
	
	public Documento obtenerDocumento(String sid){
		Documento documento = em.find(Documento.class, sid);
		return documento;
    }
	
	public JustificanteFirma obtenerFirma(String sid){
		JustificanteFirma justificante = em.find(JustificanteFirma.class, sid);
		return justificante;
		
    }	
	
	
	public Usuario buscarUsuarioPorNumeroIdentificacion(String numeroIdentificacion){
		Usuario usuario = new Usuario();
		TypedQuery<Usuario> listaUsuarios = em.createQuery(
				"SELECT u FROM Usuario u WHERE u.numeroIdentificacion=:parametro1", Usuario.class)
				.setParameter("parametro1", numeroIdentificacion);
		
		 if(listaUsuarios != null && !listaUsuarios.getResultList().isEmpty()) {
			 usuario = listaUsuarios.getResultList().get(0);
		 }
		 return usuario;
		
		
    }
	
	public Usuario buscarUsuarioPorNombreUsuario(String nombreUsuario){
		Usuario usuario = new Usuario();
		logger.info("Buscando usuario: " + nombreUsuario);
		TypedQuery<Usuario> listaUsuarios = em.createQuery(
				"SELECT u FROM Usuario u WHERE u.nombreUsuario=:parametro1", Usuario.class)
				.setParameter("parametro1", nombreUsuario);
		
		if(listaUsuarios!= null && !listaUsuarios.getResultList().isEmpty()) {
			usuario = listaUsuarios.getResultList().get(0);
		}
		return usuario;
    }
	
}
