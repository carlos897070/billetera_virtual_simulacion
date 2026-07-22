package billeteraVirtual;

import java.time.LocalDate;

public abstract class Movimiento {

	protected double monto;
	protected LocalDate fecha;
	protected EstadoOperacion estado;
	
	public Movimiento(double monto) {

        this.fecha = Utilitarios.hoy();
        this.monto = monto;
        this.estado = EstadoOperacion.PENDIENTE;  }
	
	public double getMonto()
	{
		return monto;
	}
	
	public void modificarMonto(double dinero)
	{
		monto = dinero;
	}
	
	public void aprobar() {
        estado = EstadoOperacion.APROBADA;
    }

    public void rechazar() {
        estado = EstadoOperacion.RECHAZADA;
    }
    
    public void cancelar()
    {
    	estado = EstadoOperacion.CANCELADA;
    }
}