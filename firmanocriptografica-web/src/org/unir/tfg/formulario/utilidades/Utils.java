package org.unir.tfg.formulario.utilidades;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public class Utils {
	
	public void addMessage(Severity severityInfo, final String resumen, final String mensaje) {
		FacesContext contexto = FacesContext.getCurrentInstance();
		contexto.addMessage(null, new FacesMessage(severityInfo, resumen, mensaje));
		contexto.getExternalContext().getFlash().setKeepMessages(true);
	}

}
