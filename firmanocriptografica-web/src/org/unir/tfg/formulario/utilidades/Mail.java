package org.unir.tfg.formulario.utilidades;

import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Mail {

	
	private static String puerto = "587";
	
//	private static String url = "smtp.gmail.com";
//	private static String userFrom = "tfg.unir.mmhc@gmail.com";
//	private static String password = "MMARI1986";
	
	private static String url = "smtpinterno.uam.es";
	private static String userFrom = "maria.hernandez@externo.uam.es";
	private static String password = "asd*6654";
	
	private static String descripcionFrom = "Sistema de firma electrónica";	

	public static void main(String[] args) throws Exception {
		new Mail().enviarCorreoElectronico("magda.almeria@gmail.com", "Correo de pruebas", "Cuerpo de pruebas");
	}
	
	public void enviarCorreoElectronicoActivacion(String destinatario, String numeroIdentificacion) {
		String enlace = "http://localhost:8080/firmanocriptografica-web/formularios/autorizausuario.xhtml?numeroIdentificacion=" + numeroIdentificacion;
		
		String asunto = "Activación registro de usuario " + numeroIdentificacion;
		String cuerpo = "Para validar el usuario acceda al siguiente <a href='" + enlace + "'>enlace</a>";
		new Mail().enviarCorreoElectronico(destinatario, asunto, cuerpo);
	}
	

	public void enviarCorreoElectronico(String destinatario, String asunto, String cuerpo) {

		Properties props = System.getProperties();
		props.put("mail.smtp.host", url);
		props.put("mail.smtp.port", puerto);

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		props.put("mail.smtp.user", userFrom);
		props.put("mail.smtp.clave", password);
		
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "False");


		Session session = Session.getDefaultInstance(props);
		MimeMessage message = new MimeMessage(session);

		try {

			try {
				message.setFrom(new InternetAddress(userFrom, descripcionFrom));
			}catch(Exception e) {
				e.printStackTrace();
				message.setFrom(new InternetAddress(userFrom));
			}

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
			message.setSubject(asunto);



			try {
				Multipart mp = darFormatoCuerpo(cuerpo);
				message.setContent(mp);
			}catch(Exception e) {
				e.printStackTrace();
				message.setText(cuerpo);
			}


			Transport transport = session.getTransport("smtp");
			transport.connect(url, userFrom, password);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		}
		catch (MessagingException me) {
			me.printStackTrace();
		}
	}


	private MimeMultipart darFormatoCuerpo(String cuerpo) throws Exception {

		String html_data = "";
		MimeMultipart mp = new MimeMultipart();


		html_data = "<HTML style='font-style: normal; font-size: 14px; font-family: Arial; color: #000000'>";
		html_data += "<BODY>";
		html_data += "<table border='0' width='100%' CELLSPACING='15'>";


		html_data += "<tr><td>" + cuerpo + "</td></tr>";
		html_data += "<tr><td></td></tr>";

		//html_data += "<tr><td>Para validar el usuario acceda al siguiente <a href='" + URL + "'>enlace</a></td></tr>";

		html_data += "<tr><td><b>Aviso: Este correo ha sido enviado por un sistema automático de envíos y no permite respuesta.</b></td></tr>";
		html_data += "</table>";
		html_data += "</BODY></HTML>";

		BodyPart pixPart = new MimeBodyPart();
		pixPart.setContent(html_data, "text/html; charset=ISO-8859-1");

		mp.addBodyPart(pixPart);

		return mp;
	}

}
