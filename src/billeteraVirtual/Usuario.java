package billeteraVirtual;

import java.util.*;

public class Usuario {
	
	private String dni;
	private String nombre;
	private String telefono;
	private String email;
	// alias, cuenta
	private List<Cuenta> cuentas;
	private List<Movimiento> historial;
	private List<Inversion> inversiones;
	
	public Usuario(String dni, String nombre, String telefono, String email) {
		super();
		this.dni = dni;
		this.nombre = nombre;
		this.telefono = telefono;
		this.email = email;
		this.cuentas = new ArrayList<>();
		this.historial = new ArrayList<>();
		this.inversiones = new ArrayList<>();
	}
	
	public String getDni()
	{
		return dni;
	}

	public List<Cuenta> getCuentas()
	{
		return cuentas;
	}
	
	public List<Movimiento> getHistorial()
	{
		return historial;
	}
	
	public void agregarMovimiento(Movimiento m)
	{
	    historial.add(m);
	}
	
	public void agregarInversion(Inversion i)
	{
		inversiones.add(i);
	}
	
	public List<Inversion> getInversiones()
	{
		return inversiones;
	}
	
	@Override
	public String toString() {
		return "\n-Nombre: " + nombre + "\n-DNI: " + dni + "\n-Telefono: " + telefono + "\n-Email: " + email + "\n-Cuentas: " + cuentas;
	}
	
	

}
