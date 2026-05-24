package billeteraVirtual;

import java.time.LocalDate;

public abstract class Movimiento {

	protected double monto;
	protected LocalDate fecha;
	
	public Movimiento(double monto) {

        this.fecha = Utilitarios.hoy();
        this.monto = monto;
    }
	
	public double getMonto()
	{
		return monto;
	}
	
	public void modificarMonto(double dinero)
	{
		monto = dinero;
	}
}
