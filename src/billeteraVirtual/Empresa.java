package billeteraVirtual;

import java.util.HashSet;
import java.util.Set;

public class Empresa {

	private String cuit;
	private String nombreFantasia;
	private String telefono;
	private String email;
	private String nombreContacto;
	private Set<String> autorizados;;
	
	public Empresa(String cuit, String nombreFantasia, String telefono, String email, String nombreContacto) {
		super();
		this.cuit = cuit;
		this.nombreFantasia = nombreFantasia;
		this.telefono = telefono;
		this.email = email;
		this.nombreContacto = nombreContacto;
		this.autorizados = new HashSet<>();
	}
	
	public String getCuit()
	{
		return cuit;
	}
	
	public Set<String> getAutorizados() {
	    return autorizados;
	}


	@Override
	public String toString() {
		return "\n-Nombre: " + nombreFantasia + "\n-Telefono: " + telefono + "\n-Email: "
				+ email + "\n-NombreContacto: " + nombreContacto;
	}
	
	

}
