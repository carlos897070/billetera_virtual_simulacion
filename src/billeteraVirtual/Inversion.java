package billeteraVirtual;

import java.time.LocalDate;


public abstract class Inversion extends Movimiento {

    protected static int contadorId = 1000;

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

    public void cancelar() {
        activa = false;
    }
    
    public Cuenta getCuenta()
    {
    	return cuentaOrigen;
    }

    public abstract double calcularRetorno();
    public abstract boolean esPrecancelable();
}