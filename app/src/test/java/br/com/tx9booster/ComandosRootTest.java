package br.com.tx9booster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ComandosRootTest {

    @Test
    public void comandosDePacoteUsamModelosFixosEInversos() {
        ComandosRoot.Comando desativar = ComandosRoot.desativarPacote("br.exemplo.app");
        assertEquals("pm disable-user --user 0 br.exemplo.app", desativar.linha());
        assertEquals("pm enable --user 0 br.exemplo.app", desativar.inverso());

        ComandosRoot.Comando reativar = ComandosRoot.reativarPacote("br.exemplo.app");
        assertEquals("pm enable --user 0 br.exemplo.app", reativar.linha());
        assertEquals("pm disable-user --user 0 br.exemplo.app", reativar.inverso());
    }

    @Test
    public void rejeitaPacoteComComandoInjetado() {
        try {
            ComandosRoot.desativarPacote("br.exemplo;id");
        } catch (IllegalArgumentException esperado) {
            assertTrue(esperado.getMessage().contains("inválido"));
            return;
        }
        throw new AssertionError("Pacote inválido foi aceito.");
    }

    @Test
    public void sysfsAceitaSomenteChavesDaLista() {
        assertEquals("cat /proc/sys/vm/swappiness",
                ComandosRoot.lerSysfs("swappiness").linha());
        try {
            ComandosRoot.lerSysfs("/data/qualquer-coisa");
        } catch (IllegalArgumentException esperado) {
            return;
        }
        throw new AssertionError("Chave fora da lista foi aceita.");
    }

    @Test
    public void testaRootUsaSomenteId() {
        assertEquals("id", ComandosRoot.testarRoot().linha());
    }
}