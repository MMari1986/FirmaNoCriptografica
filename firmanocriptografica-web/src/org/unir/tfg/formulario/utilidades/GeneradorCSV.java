package org.unir.tfg.formulario.utilidades;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class GeneradorCSV {
	
	private String codigoSeguroVerificacion;
	
	public GeneradorCSV(byte[] contenido) throws NoSuchAlgorithmException {
		super();
		MessageDigest md = MessageDigest.getInstance("SHA-1"); 
		this.setCodigoSeguroVerificacion(byteArray2Hex(md.digest(contenido)));
	}
	
	private String byteArray2Hex(final byte[] hash) {
	    Formatter formatter = new Formatter();
	    for (byte b : hash) {
	        formatter.format("%02x", b);
	    }
	    
	    String resultado = formatter.toString();
	    formatter.close();
	    return resultado;
	}

	public String getCodigoSeguroVerificacion() {
		return codigoSeguroVerificacion;
	}

	public void setCodigoSeguroVerificacion(String codigoSeguroVerificacion) {
		this.codigoSeguroVerificacion = codigoSeguroVerificacion;
	}

}
