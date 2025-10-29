package pt.ulusofona.lp2.greatprogrammingjourney;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GameManager {

    private int worldSize = 0;
    private int turnCount = 0;
    private Integer winnerId = null;


    private final List<Integer> playerOrder = new ArrayList<>();
    private final Map<Integer, String> nameById = new HashMap<>();
    private final Map<Integer, String> colorById = new HashMap<>();
    private final Map<Integer, Integer> posById = new HashMap<>();
    private final Map<Integer, List<String>> langsById = new HashMap<>();
    private final Map<Integer, String> stateById = new HashMap<>();

    private int currentIdx = 0;

    private static final Set<String> ALLOWED_COLORS =
            new HashSet<>(Arrays.asList("blue", "green", "yellow", "red"));

    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 4;
    private static final int MIN_WORLD_SIZE = 4;

    public GameManager() {}


    public boolean createInitialBoard(String[][] playerInfo, int worldSize) {
        if (playerInfo == null) return false;
        if (playerInfo.length < MIN_PLAYERS || playerInfo.length > MAX_PLAYERS) return false;
        if (worldSize < MIN_WORLD_SIZE) return false;

        Set<Integer> seenIds = new HashSet<>();
        Set<String> usedColors = new HashSet<>();

        for (String[] row : playerInfo) {
            if (row == null || row.length < 3) return false;

            int id;
            try {
                id = Integer.parseInt(row[0]);
            } catch (NumberFormatException e) {
                return false;
            }
            if (id <= 0) return false;
            if (!seenIds.add(id)) return false;

            String name = row[1] == null ? "" : row[1].trim();
            if (name.isEmpty()) return false;

            String color = row[2] == null ? "" : row[2].trim().toLowerCase(Locale.ROOT);
            if (!ALLOWED_COLORS.contains(color)) return false;
            if (!usedColors.add(color)) return false;
        }

        this.worldSize = worldSize;
        playerOrder.clear();
        nameById.clear();
        colorById.clear();
        posById.clear();

        for (String[] row : playerInfo) {
            int id = Integer.parseInt(row[0]);
            String name = row[1].trim();
            String color = row[2].trim().toLowerCase(Locale.ROOT);

            playerOrder.add(id);
            nameById.put(id, name);
            colorById.put(id, color);
            posById.put(id, 0);

            langsById.put(id, new ArrayList<>(Arrays.asList("Java")));
            stateById.put(id, "Em Jogo");
        }
        Collections.sort(playerOrder);
        currentIdx = 0;
        return true;
    }

    public String getImagePng(int position) {
        if (worldSize <= 0) return null;
        if (position < 1 || position > worldSize) return null;
        if (position == worldSize) return "glory.png";
        return null;
    }

    public String[] getProgrammerInfo(int id) {
        if (!nameById.containsKey(id)) {
            return null;
        }

        String name = nameById.get(id);
        int pos = posById.getOrDefault(id, 0);
        List<String> langs = langsById.getOrDefault(id, new ArrayList<>());
        String estado = stateById.getOrDefault(id, "Em Jogo");

        List<String> sorted = new ArrayList<>(langs);
        Collections.sort(sorted, String.CASE_INSENSITIVE_ORDER);

        String langsJoined = String.join(";", sorted);

        return new String[]{
                String.valueOf(id),
                name,
                String.valueOf(pos),
                langsJoined,
                estado
        };
    }


    public String getProgrammerInfoAsStr(int id) {
        if (!nameById.containsKey(id)) {
            return null;
        }

        String name = nameById.get(id);
        int pos = posById.getOrDefault(id, 0);
        List<String> langs = langsById.getOrDefault(id, new ArrayList<>());
        String estado = stateById.getOrDefault(id, "Em Jogo");

        List<String> sorted = new ArrayList<>(langs);
        Collections.sort(sorted, String.CASE_INSENSITIVE_ORDER);
        String langsJoined = String.join(";", sorted);

        return id + " | " + name + " | " + pos + " | " + langsJoined + " | " + estado;
    }


    public String[] getSlotInfo(int position) {
        if (position < 1 || position > worldSize) {
            return null;
        }

        List<Integer> idsNaCasa = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : posById.entrySet()) {
            if (entry.getValue() == position) {
                idsNaCasa.add(entry.getKey());
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < idsNaCasa.size(); i++) {
            sb.append(idsNaCasa.get(i));
            if (i < idsNaCasa.size() - 1) {
                sb.append(",");
            }
        }

        String resultado = sb.toString();
        return new String[]{ resultado };
    }


    public int getCurrentPlayerID() {
        if (playerOrder.isEmpty()) return -1;
        return playerOrder.get(currentIdx);
    }

    private void advanceTurn() {
        if (!playerOrder.isEmpty()) {
            currentIdx = (currentIdx + 1) % playerOrder.size();
        }
    }

    public boolean moveCurrentPlayer(int nrSpaces) {
        if (nrSpaces < 1 || nrSpaces > 6) {
            return false;
        }
        if (playerOrder.isEmpty() || worldSize <= 0) {
            return false;
        }

        int currentId = playerOrder.get(currentIdx);
        int posAtual = posById.getOrDefault(currentId, 0);

        int destino = posAtual + nrSpaces;

        if (destino > worldSize) {
            int excesso = destino - worldSize;
            destino = worldSize - excesso;
            if (destino < 1) {
                destino = 1;
            }
        }

        posById.put(currentId, destino);

        if (destino == worldSize && winnerId == null) {
            winnerId = currentId;
        }

        turnCount++;
        advanceTurn();
        return true;

    }


    public boolean gameIsOver() {
        if (worldSize <= 0 || posById.isEmpty()) {
            return false;
        }

        for (int pos : posById.values()) {
            if (pos == worldSize) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getGameResults() {
        ArrayList<String> out = new ArrayList<>();

        if (!gameIsOver() || winnerId == null) {
            return out;
        }

        // Cabeçalho
        out.add("THE GREAT PROGRAMMING JOURNEY");
        out.add("");
        out.add("NR. DE TURNOS");
        out.add(String.valueOf(turnCount));
        out.add("");
        out.add("VENCEDOR");
        out.add(nameById.getOrDefault(winnerId, String.valueOf(winnerId)));
        out.add("");
        out.add("RESTANTES");

        ArrayList<Integer> restantes = new ArrayList<>();
        for (Integer id : playerOrder) {
            if (!id.equals(winnerId)) restantes.add(id);
        }

        Collections.sort(restantes, new Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                int pa = posById.getOrDefault(a, 0);
                int pb = posById.getOrDefault(b, 0);
                if (pa != pb) return Integer.compare(pb, pa);
                return Integer.compare(a, b);
            }
        });

        for (Integer id : restantes) {
            out.add(nameById.getOrDefault(id, String.valueOf(id)));
        }

        return out;
    }

    public JPanel getAuthorsPanel() {

        JPanel root = new JPanel();
        root.setPreferredSize(new Dimension(300, 300));
        root.setMinimumSize(new Dimension(300, 300));
        root.setMaximumSize(new Dimension(300, 300));
        root.setBackground(new Color(245, 245, 245));
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        root.setLayout(new BorderLayout());

        JLabel title = new JLabel("THE GREAT PROGRAMMING JOURNEY", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 12f));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        root.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // TODO: altera para os teus dados reais
        content.add(makeLine("Número: 20201234  |  Nome: Ana Silva"));
        content.add(makeLine("Número: 20205678  |  Nome: Bruno Costa"));
        content.add(Box.createVerticalStrut(8));
        content.add(makeLine("Turma: LP2-XYZ"));
        content.add(makeLine("Ano letivo: 2025/26"));

        root.add(content, BorderLayout.CENTER);

        JLabel footer = new JLabel("Universidade Lusófona", SwingConstants.CENTER);
        footer.setFont(footer.getFont().deriveFont(Font.PLAIN, 11f));
        footer.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        root.add(footer, BorderLayout.SOUTH);

        return root;
    }

    private JLabel makeLine(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 12f));
        return lbl;
    }

    public HashMap<String, String> customizeBoard() { return new HashMap<>(); }
}
