package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private BigDecimal saldo;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
  }
  public Cuenta(Double montoInicial) {
    saldo = new BigDecimal(montoInicial);
  }

  public void agregarMovimiento(Movimiento movimiento) {
    this.movimientos.add(movimiento);
  }

  public void depositarDinero(BigDecimal monto) {
    validarDeposito(monto);
    agregarMovimiento(new Movimiento(LocalDate.now(), monto, true));
  }

  public void extraerDinero(BigDecimal monto) {
    validarMontoExtraccion(monto);
    BigDecimal montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    BigDecimal limite = new BigDecimal(1000).subtract(montoExtraidoHoy);
    if (monto.compareTo(limite) == 1) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000 //TODO El meter el 1000 hardcodeado en una variable
          + " diarios, límite: " + limite);
    }
    agregarMovimiento(new Movimiento(LocalDate.now(), monto, false));
    this.saldo.subtract(monto);
  }

  private void validarMontoExtraccion(BigDecimal monto) throws MontoNegativoException, SaldoMenorException {
    if (monto.intValue() <= 0) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }
    if (getSaldo().subtract(monto).intValue() < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }

  public void agregarMovimiento(LocalDate fecha, BigDecimal monto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, monto, esDeposito);
    movimientos.add(movimiento);
    this.saldo.add(monto);
  }

  public BigDecimal getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
            .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
            .map(movimiento -> movimiento.getMonto())
            .reduce(BigDecimal.ZERO,BigDecimal::add);
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public BigDecimal getSaldo() {
    return saldo;
  }

  public void setSaldo(BigDecimal saldo) {
    this.saldo = saldo;
  }

  private void validarDeposito(BigDecimal monto) throws MontoNegativoException, MaximaCantidadDepositosException {
    if (monto.intValue() <= 0) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }

    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

}
