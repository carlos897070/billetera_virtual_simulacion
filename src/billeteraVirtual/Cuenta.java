package billeteraVirtual;

import java.util.*;

public abstract class Cuenta {
	
	protected String cvu;
	protected String alias;
	protected double saldo;
	protected double saldoInvertido;
	protected String dniTitular;
	protected List<Movimiento> historial;
	protected List<Inversion> inversiones;
	
	public Cuenta(String alias, String dniTitular) {
		super();
		this.cvu = Utilitarios.generarSiguienteCvu();
		this.alias = alias;
		this.saldo = 0;
		this.saldoInvertido = 0;
		this.dniTitular = dniTitular;
		this.historial = new ArrayList<>();
		this.inversiones = new ArrayList<>();
	}
	
	
	
	public String getCvu()
	{
		return cvu;
	}
	
	public String getAlias()
	{
		return alias;
	}
	
	public double getSaldo()
	{
		return saldo;
	}
	
	public String getDniTitular()
	{
		return dniTitular;
	}
	
	public List<Movimiento> getHistorial()
	{
		return historial;
	}
	
	public double getSaldoInvertido()
	{
		return saldoInvertido;
	}
	
	public List<Inversion> getInversiones()
	{
		return inversiones;
	}
	
	public void agregarInversion(Inversion i)
	{
		inversiones.add(i);
	}
	
	public void depositar(double dinero)
	{
		if(dinero <= 0) throw new IllegalArgumentException("Monto inválido");
		
		saldo += dinero;
		
	}
	
	public boolean retirarDinero(double dinero)
	{
		if(dinero > saldo || dinero <= 0) throw new IllegalArgumentException("Monto inválido");
		
		saldo -= dinero;
		return true;
	}
	
	public void depositarEnSaldoDeInversiones(double dinero)
	{
		this.saldoInvertido += dinero;
	}
	
	public void retirarDineroDeInversion(double dinero)
	{
		this.saldoInvertido -= dinero;
	}
	
	public void agregarMovimiento(Movimiento m)
	{
	    historial.add(m);
	}
	
	public int volumenDeCuenta()
	{
		return historial.size();
	}

	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + "]: [" + alias + "]" + "[" + cvu + "]";
	}
	
	

}