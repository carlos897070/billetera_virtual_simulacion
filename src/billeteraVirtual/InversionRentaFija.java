package billeteraVirtual;

public class InversionRentaFija extends Inversion {

    private static final double TASA = 0.20;

    public InversionRentaFija(Cuenta cuentaOrigen, double monto, int plazoDias) {

        super(cuentaOrigen, monto, plazoDias);
    }

    @Override
    public double calcularRetorno()
    {
        long dias = java.time.temporal.ChronoUnit.DAYS.between(fecha, Utilitarios.hoy());

        double intereses = monto * (TASA / 365.0) * dias;

        // si fue precancelada
        if(!activa)
        {
            intereses /= 2;
        }

        return monto + intereses;
    }
    
    @Override
    public boolean esPrecancelable()
    {
    	return true;
    }

}