package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MonederoTest {

  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void puedoRealizarUnDepositoDeDinero() {
    Double montoPrevio = cuenta.getSaldo();
    Integer montoADepositar = 1500;
    assertDoesNotThrow(()-> cuenta.poner(montoADepositar));
  }

  @Test
  void realizarUnDepositoDe500PesosumentaMiSaldoEn500Pesos() {
    Double montoPrevio = cuenta.getSaldo();
    Integer montoADepositar = 500;
    cuenta.poner(montoADepositar);
    assertEquals(cuenta.getSaldo(),montoPrevio + montoADepositar);
  }

  @Test
  void ponerMontoNegativoLanzaMontoNegativoException() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  void hacerTresDepositosNoLanzaNingunaExcepcion() {
    assertDoesNotThrow(()-> {
      cuenta.poner(1500);
      cuenta.poner(456);
      cuenta.poner(1900);
    });
  }

  @Test
  void hacerMasDeTresDepositosLanzaMaximaCantidadDepositosException() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.poner(1500);
          cuenta.poner(456);
          cuenta.poner(1900);
          cuenta.poner(245);
    });
  }

  @Test
  void extraerMasQueElSaldoLanzaSaldoMenorException() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(90);
          cuenta.sacar(100);
    });
  }

  @Test
  public void extraerMasQueElLimiteDiarioLanzaMaximoExtraccionDiarioException() {
    //TODO Una cosa es el monto maximo de extraccion, otra es extraer mas que ese monto maximo de una sola extraccion
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.sacar(1001);
    });
  }

  @Test
  public void extraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));
  }

}