package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class MonederoTest {

  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void puedoRealizarUnDepositoDeDinero() {
    BigDecimal montoADepositar = new BigDecimal(1500);
    assertDoesNotThrow(()-> cuenta.depositarDinero(montoADepositar));
  }

  @Test
  void realizarUnDepositoDe500PesosumentaMiSaldoEn500Pesos() {
    BigDecimal montoPrevio = cuenta.getSaldo();
    BigDecimal montoADepositar = new BigDecimal(500);
    cuenta.depositarDinero(montoADepositar);
    assertEquals(cuenta.getSaldo(),montoPrevio.add(montoADepositar));
  }

  @Test
  void ponerMontoNegativoLanzaMontoNegativoException() {
    assertThrows(MontoNegativoException.class, () -> cuenta.depositarDinero(new BigDecimal(-1500)));
  }

  @Test
  void hacerTresDepositosNoLanzaNingunaExcepcion() {
    assertDoesNotThrow(()-> {
      cuenta.depositarDinero(new BigDecimal(1500));
      cuenta.depositarDinero(new BigDecimal(456));
      cuenta.depositarDinero(new BigDecimal(1900));
    });
  }

  @Test
  void hacerMasDeTresDepositosLanzaMaximaCantidadDepositosException() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
      cuenta.depositarDinero(new BigDecimal(1500));
      cuenta.depositarDinero(new BigDecimal(456));
      cuenta.depositarDinero(new BigDecimal(1900));
      cuenta.depositarDinero(new BigDecimal(245));
    });
  }

  @Test
  void extraerMasQueElSaldoLanzaSaldoMenorException() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(new BigDecimal(90));
          cuenta.extraerDinero(new BigDecimal(100));
    });
  }

  @Test
  public void extraerMasQueElLimiteDiarioLanzaMaximoExtraccionDiarioException() {
    //TODO Una cosa es el monto maximo de extraccion, otra es extraer mas que ese monto maximo de una sola extraccion
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(new BigDecimal(5000));
      cuenta.extraerDinero(new BigDecimal(1001));
    });
  }

  @Test
  public void extraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.extraerDinero(new BigDecimal(-500)));
  }

}