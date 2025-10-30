package pt.ulusofona.lp2.greatprogrammingjourney;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestGameManager {

    private String[][] playersOK() {
        return new String[][]{
                {"1", "Alice", "blue"},
                {"2", "Bob", "green"}
        };
    }

    @Test
    void aceitaJogadoresValidos() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(playersOK(), 6),
                "Deve aceitar 2 jogadores válidos e worldSize >= 4");
        assertEquals(1, gm.getCurrentPlayerID(),
                "O jogador com menor ID deve começar o jogo");
    }

    @Test
    void rejeitaNumJogadoresForaIntervalo() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(
                        new String[][]{{"1", "A", "blue"}}, 6),
                "Deve rejeitar menos de 2 jogadores");
    }

    @Test
    void rejeitaIdsRepetidosOuNegativos() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(
                        new String[][]{{"1", "A", "blue"}, {"1", "B", "green"}}, 6),
                "IDs repetidos não devem ser aceites");
        assertFalse(gm.createInitialBoard(
                        new String[][]{{"-3", "A", "blue"}, {"2", "B", "green"}}, 6),
                "IDs negativos não devem ser aceites");
    }

    @Test
    void rejeitaNomeVazio() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(
                        new String[][]{{"1", "   ", "blue"}, {"2", "Bob", "green"}}, 6),
                "Nome vazio deve causar falha na criação");
    }

    @Test
    void rejeitaCorInvalidaOuRepetida() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(
                        new String[][]{{"1", "A", "pink"}, {"2", "B", "green"}}, 6),
                "Cor inexistente deve ser rejeitada");
        assertFalse(gm.createInitialBoard(
                        new String[][]{{"1", "A", "blue"}, {"2", "B", "blue"}}, 6),
                "Cores repetidas não são permitidas");
    }

    @Test
    void rejeitaWorldSizePequeno() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(playersOK(), 3),
                "WorldSize inferior a 4 deve ser rejeitado");
    }
}
