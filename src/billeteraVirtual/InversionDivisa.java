package billeteraVirtual;

public class InversionDivisa extends Inversion {

	private String divisa;
	private double tasa;
	private double cotizacionInicial;
	
	public InversionDivisa(Cuenta cuentaOrigen, double monto, int plazoDias, String divisa, double tasa) {
		super(cuentaOrigen, monto, plazoDias);
		this.divisa = divisa;
		this.tasa = tasa;
		this.cotizacionInicial = Utilitarios.consultarCotizacion(divisa);
	}
	
	public String getDivisa()
	{
		return divisa;
	}
	
	public double getTasa()
	{
		return tasa;
	}
	
	public double cotizacionInicial()
	{
		return cotizacionInicial;
	}
	
	@Override
	public double calcularRetorno() {

	    double cotizacionFinal = Utilitarios.consultarCotizacion(divisa);

	    // cuantos USD compre originalmente
	    double capitalEnDivisa = monto / cotizacionInicial;

	    // dias transcurridos desde la creacion
	    long dias = java.time.temporal.ChronoUnit.DAYS.between(fecha, Utilitarios.hoy());

	    // intereses proporcionales
	    double intereses = capitalEnDivisa * (tasa / 365.0) * dias;

	    // si fue precancelada -> mitad de intereses
	    if(!activa)
	    {
	        intereses /= 2;
	    }

	    // total en divisa
	    double totalDivisa = capitalEnDivisa + intereses;

	    // volver a pesos
	    return totalDivisa * cotizacionFinal;
	}
	
	@Override
	public boolean esPrecancelable()
	{
		return true;
	}
	
	@Override
    public String toString() {

        return "\n-INVERSION:\n" +
        	   " | Fecha: " + fecha + "\n" +
               " | Origen: " + "[" + cuentaOrigen.getDniTitular() + "]" + "[" + cuentaOrigen.getCvu() + "]\n" +
               " | Tipo: [Divisa]" + "\n" +
               " | Monto: " + "[" +monto+ "]" + "\n" +
               " | Plazo: " + "["+plazoDias+" dias]\n";
    }
	
}
