package pt.ulusofona.lp2.greatprogrammingjourney;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestGameManager {

    private String[][] playersOK() {
        return new String[][]{
                {"1","Alice","blue"},
                {"2","Bob","green"}
        };
    }

    @Test
    void aceitaJogadoresValidos() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(playersOK(), 6));
        assertEquals(1, gm.getCurrentPlayerID());
    }

    @Test
    void rejeitaNumJogadoresForaIntervalo() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(new String[][]{{"1","A","blue"}}, 6)); // 1 jogador
    }

    @Test
    void rejeitaIdsRepetidosOuNegativos() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(
                new String[][]{{"1","A","blue"},{"1","B","green"}}, 6)); // id repetido
        assertFalse(gm.createInitialBoard(
                new String[][]{{"-3","A","blue"},{"2","B","green"}}, 6)); // id negativo
    }

    @Test
    void rejeitaNomeVazio() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(
                new String[][]{{"1","   ","blue"},{"2","Bob","green"}}, 6));
    }

    @Test
    void rejeitaCorInvalidaOuRepetida() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(
                new String[][]{{"1","A","pink"},{"2","B","green"}}, 6)); // cor inválida
        assertFalse(gm.createInitialBoard(
                new String[][]{{"1","A","blue"},{"2","B","blue"}}, 6)); // cor repetida
    }

    @Test
    void rejeitaWorldSizePequeno() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(playersOK(), 3));
    }
}
