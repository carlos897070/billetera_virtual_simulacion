package billeteraVirtual;

import java.time.LocalDate;

public abstract class Inversion extends Movimiento {

    protected static int contadorId = 1;

    protected int id;
    protected Cuenta cuentaOrigen;
    protected int plazoDias;
    protected LocalDate fechaVencimiento;
    protected boolean activa;

    public Inversion(Cuenta cuentaOrigen, double monto, int plazoDias) {

        super(monto);

        this.id = contadorId++;
        this.cuentaOrigen = cuentaOrigen;
        this.plazoDias = plazoDias;
        this.fechaVencimiento = fecha.plusDays(plazoDias);
        this.activa = true;
    }

    public int getId() {
        return id;
    }

    public boolean estaActiva() {
        return activa;
    }

    public void desactivar() {
        activa = false;
    }
    
    public Cuenta getCuenta()
    {
    	return cuentaOrigen;
    }
    
    public LocalDate getFechaVencimiento()
    {
    	return fechaVencimiento;
    }

    public abstract double calcularRetorno();
    public abstract boolean esPrecancelable();
    
    @Override
    public String toString() {

        return "\n-INVERSION:\n" +
        	   " | Fecha: " + fecha + "\n" +
               " | Origen: " + "[" + cuentaOrigen.getDniTitular() + "]" + "[" + cuentaOrigen.getCvu() + "]\n" +
               " | Tipo: " + "[" + getClass().getSimpleName() + "]" + "\n" +
               " | Monto: " + "[" +monto+ "]" + "\n" +
               " | Plazo: " + "["+plazoDias+" dias]\n" + 
               " | Estado: " + estado + "\n";
    }
}