package org.unir.tfg.formulario.utilidades;

import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.faces.validator.ValidatorException;

@FacesConverter("NIFNIEConverter")
public class NIFNIEConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		String resultado = value.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
		if (resultado.equals("")) {
			String errVacio;
			if (value.length() > 0) {
				errVacio = this.getTextFromRBundle(context, "registro.novalida");
			} else {
				errVacio = this.getTextFromRBundle(context, "registro.novacio");
			}
			throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, errVacio, errVacio));
		}

		if (!validaNIFNIE(resultado)) {
			throw new ConverterException();
		}

		return resultado;
	}
	

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return value.toString();
	}

	private String getTextFromRBundle(FacesContext ctx, String key) {
		ResourceBundle rb = ctx.getApplication().getResourceBundle(ctx, "msg" );
		return rb.getString(key);
	}

	private boolean validaNIFNIE(String valor) {
		boolean resultado = false;
		
		final char primerCaracter = valor.charAt(0);
		if (primerCaracter == 'X' || primerCaracter == 'Y' || primerCaracter == 'Z' || primerCaracter == 'L' || primerCaracter == 'M' || primerCaracter == 'K') { // NIE					
			valor = valor.substring(1);
			char primerDigito = '0';
			if (primerCaracter == 'Y') {
				primerDigito = '1';
			}
			if (primerCaracter == 'Z') {
				primerDigito = '2';
			}
			resultado = validarNIF(primerDigito + valor);
		} else {
			resultado = validarNIF(valor);
		}


		return resultado;
	}


	private boolean validarNIF(final String nif) throws ValidatorException {
		if (nif.matches("^[0-9]+[A-Z]$")) {
			if (!("" + generarLetraNIF(nif.substring(0, nif.length() - 1))).equals(nif.substring(nif.length() - 1, nif.length()))) { 
				return false;
			}
		} else {
			return false;
		}
		return true;
	}


	private char generarLetraNIF(final String dni) {

		final int aux = (int) (Long.parseLong(dni) % 23);
		switch (aux) {
		case 0:
			return 'T';
		case 1:
			return 'R';
		case 2:
			return 'W';
		case 3:
			return 'A';
		case 4:
			return 'G';
		case 5:
			return 'M';
		case 6:
			return 'Y';
		case 7:
			return 'F';
		case 8:
			return 'P';
		case 9:
			return 'D';
		case 10:
			return 'X';
		case 11:
			return 'B';
		case 12:
			return 'N';
		case 13:
			return 'J';
		case 14:
			return 'Z';
		case 15:
			return 'S';
		case 16:
			return 'Q';
		case 17:
			return 'V';
		case 18:
			return 'H';
		case 19:
			return 'L';
		case 20:
			return 'C';
		case 21:
			return 'K';
		case 22:
			return 'E';
		default:
			return ' '; // Caso imposible
		}
	}

}
