package com.unir.tfg.filtro;

import java.io.IOException;

import javax.faces.application.ResourceHandler;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.MDC;


@WebFilter(servletNames={"Faces Servlet"}) 
public class ExtendingLogFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession  sesion 	 = req.getSession();

		//Filtro para evitar la cache
		if (!req.getRequestURI().startsWith(req.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) { // Skip JSF resources (CSS/JS/Images/etc)
			res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
			res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
			res.setDateHeader("Expires", 0); // Proxies.
		}


		//Filtro almacena IPorigen y navegador
		String ip =  req.getRemoteAddr();	
		String xforwardedfor = req.getHeader("x-forwarded-for");
		if(xforwardedfor != null && !xforwardedfor.isEmpty()) {
			if(xforwardedfor.indexOf(",") != -1) {
				ip = xforwardedfor.substring(0, xforwardedfor.indexOf(",")).trim();
			}else {
				ip = xforwardedfor.trim();
			}
		}
		String navegador = procesarNavegador(req.getHeader("User-Agent"));
		String usuario = (String)sesion.getAttribute("USUARIO");

		MDC.put("ip", ip);
		MDC.put("navegador", navegador); 
		MDC.put("usuario", (usuario != null && !usuario.isEmpty())?usuario:"-"); 	
		
		chain.doFilter(request, response);
	}

	private String procesarNavegador(String navegador){
		try{
			if(navegador == null || navegador.isEmpty()) {
				navegador = "-";
			}else if(navegador.contains("Chrome")){ //Chrome
				String substring=navegador.substring(navegador.indexOf("Chrome")).split(" ")[0];
				navegador=substring.split("/")[0]+" "+substring.split("/")[1];
			}
			else if(navegador.contains("Firefox")){  //Firefox
				String substring=navegador.substring(navegador.indexOf("Firefox")).split(" ")[0];
				navegador=substring.split("/")[0]+" "+substring.split("/")[1];
			}
			else if(navegador.contains("MSIE")){ //Internet Explorer
				String substring=navegador.substring(navegador.indexOf("MSIE")).split(";")[0];
				navegador="Internet Explorer "+substring.split(" ")[1];
			}
			else if(navegador.contains("rv")){ //Internet Explorer 11
				String substring=navegador.substring(navegador.indexOf("rv"),navegador.indexOf(")"));
				navegador="Internet Explorer "+substring.split(":")[1];
			}
			else if(navegador.contains("Safari")){ //Safari
				String substring=navegador.substring(navegador.indexOf("Version"),navegador.indexOf("Safari")-1);
				navegador="Safari "+substring.split("/")[1];
			}
		}catch(Exception e){
			return navegador;
		}

		return navegador;
	}


}