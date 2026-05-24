package billeteraVirtual;

public class InversionLiquidez extends Inversion {
	
	private static final double TASA = 0.08;
	private double cotizacionInicial;

    public InversionLiquidez(Cuenta cuenta, double monto, int plazoDias)
    {
        super(cuenta, monto, plazoDias);
        this.cotizacionInicial = Utilitarios.consultarCotizacion("FLE");
    }
    
    
    @Override
	public double calcularRetorno()
	{
		double cotizacionFinal = Utilitarios.consultarCotizacion("FLE");
		double variacion = cotizacionFinal/cotizacionInicial;
		
		double incremento = monto*variacion;
		
		double incrementoMasTasa = incremento + (incremento*TASA);
		
		return incrementoMasTasa;
	}
    
    @Override
    public boolean esPrecancelable()
    {
    	return false;
    }
    
    @Override
    public String toString() {

        return "\n-INVERSION:\n" +
               " | Origen: " + "[" + cuentaOrigen.getDniTitular() + "]" + "[" + cuentaOrigen.getCvu() + "]\n" +
               " | Tipo: [Fondo De Liquidez Empresarial]" + "\n" +
               " | Monto: " + "[" +monto+ "]" + "\n" +
               " | Plazo: " + "["+plazoDias+" dias]\n";
    }
}
