package billeteraVirtual;

public class Transferencia extends Movimiento {

	private Cuenta cvuOrigen;
	private Cuenta cvuDestino;
	
	
	public Transferencia(Cuenta cvuOrigen, Cuenta cvuDestino, double monto) {

		super(monto);

		this.cvuOrigen = cvuOrigen;
		this.cvuDestino = cvuDestino;
	}
	
	
	@Override
    public String toString() {

        return "\n-TRANSFERENCIA:\n" +
               " | Fecha: " + fecha + "\n" +
               " | Origen: [" + cvuOrigen.getDniTitular() + "]" + "[" + cvuOrigen.getCvu() + "]" + "\n" +
               " | Destino: [" + cvuDestino.getDniTitular() + "]" + "[" + cvuDestino.getCvu() + "]" + "\n" +
               " | Monto: $" + monto + "\n" +
               " | Estado: " + estado + "\n";
    }
}