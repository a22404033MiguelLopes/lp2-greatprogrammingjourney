package pt.ulusofona.lp2.greatprogrammingjourney;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GameManager {

    private int worldSize = 0;
    private boolean initialized = false;
    private int turnCount = 0;
    private Integer winnerId = null;


    private final List<Integer> playerOrder = new ArrayList<>();
    private final Map<Integer, String> nameById = new HashMap<>();
    private final Map<Integer, String> colorById = new HashMap<>();
    private final Map<Integer, Integer> posById = new HashMap<>();
    private final Map<Integer, List<String>> langsById = new HashMap<>();
    private final Map<Integer, String> stateById = new HashMap<>();

    private int currentIdx = 0;

    public GameManager() {}


    public boolean createInitialBoard(String[][] playerInfo, int worldSize) {
        this.turnCount = 0;
        this.winnerId = null;
        this.initialized = false;
        this.currentIdx = 0;

        if (playerInfo == null || playerInfo.length < 2 || playerInfo.length > 4) {
            return false;
        }
        if (worldSize < 4) {
            return false;
        }

        this.worldSize = worldSize;
        playerOrder.clear();
        nameById.clear();
        colorById.clear();
        posById.clear();
        langsById.clear();
        stateById.clear();
        java.util.Set<Integer> usedIds = new java.util.HashSet<>();
        java.util.Set<String> usedColors = new java.util.HashSet<>();
        java.util.Set<String> allowed = new java.util.HashSet<>(
                java.util.Arrays.asList("blue","green","brown","purple")
        );

        for (String[] row : playerInfo) {
            if (row == null || row.length < 3) {
                return false;
            }

            int id;
            try {
                id = Integer.parseInt(row[0]);
            } catch (Exception e) {
                return false;
            }
            if (id <= 0) {
                return false;
            }
            if (!usedIds.add(id)) {
                return false;
            }

            String name = (row[1] == null) ? "" : row[1].trim();
            if (name.isEmpty()) {
                return false;
            }

            List<String> langs = new ArrayList<>();
            String colorRaw;

            if (row.length >= 4) {
                String langsRaw = (row[2] == null) ? "" : row[2].trim();
                if (!langsRaw.isEmpty()) {
                    String[] parts = langsRaw.split(";");
                    for (String p : parts) {
                        String s = p.trim();
                        if (!s.isEmpty()) {
                            langs.add(s);
                        }
                    }
                }
                colorRaw = (row[3] == null) ? "" : row[3].trim();
            } else {
                colorRaw = (row[2] == null) ? "" : row[2].trim();
                langs.add("Java");
            }

            String color = colorRaw.toLowerCase(Locale.ROOT);
            if (!allowed.contains(color)) {
                return false;
            }
            if (!usedColors.add(color)) {
                return false;
            }

            playerOrder.add(id);
            nameById.put(id, name);
            colorById.put(id, color);
            posById.put(id, 1);
            langsById.put(id, langs.isEmpty() ? new ArrayList<>(List.of("Java")) : langs);
            stateById.put(id, "Em Jogo");
        }

        Collections.sort(playerOrder);
        currentIdx = 0;
        initialized = true;
        return true;
    }


    public String getImagePng(int position) {
        if (worldSize <= 0) {
            return null;
        }
        if (position < 1 || position > worldSize) {
            return null;
        }
        if (position == worldSize) {
            return "glory.png";
        }
        return null;
    }

    public String[] getProgrammerInfo(int id) {
        if (!initialized) {
            return new String[]{ String.valueOf(id), "", "1", "Blue", "Em Jogo" };
        }
        if (!nameById.containsKey(id)) {
            return null;
        }

        String name = nameById.get(id);
        int pos = posById.getOrDefault(id, 1);
        String colorCap = capitalizeFirst(colorById.getOrDefault(id, "blue"));

        java.util.List<String> langs = langsById.getOrDefault(id, new java.util.ArrayList<>());
        java.util.List<String> sorted = new java.util.ArrayList<>(langs);
        sorted.sort(String.CASE_INSENSITIVE_ORDER);
        String langsJoined = String.join(";", sorted);

        String state = stateById.getOrDefault(id, "Em Jogo");

        return new String[]{
                String.valueOf(id),
                name,
                String.valueOf(pos),
                colorCap,
                state,
                langsJoined
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
        sorted.sort(String.CASE_INSENSITIVE_ORDER);
        String langsJoined = String.join("; ", sorted);

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
        if (playerOrder.isEmpty()) {
            return -1;
        }
        return playerOrder.get(currentIdx);
    }

    private void advanceTurn() {
        if (!playerOrder.isEmpty()) {
            currentIdx = (currentIdx + 1) % playerOrder.size();
        }
    }

    public boolean moveCurrentPlayer(int nrSpaces) {
        if (nrSpaces < 1 || nrSpaces > 6) return false;
        if (playerOrder.isEmpty() || worldSize <= 0) return false;

        int currentId = playerOrder.get(currentIdx);
        int posAtual = posById.getOrDefault(currentId, 1);
        int destino = posAtual + nrSpaces;

        if (destino > worldSize) {
            int excesso = destino - worldSize;
            destino = worldSize - excesso;
            if (destino < 1) destino = 1;
        }

        posById.put(currentId, destino);

        boolean ganhouAgora = (destino == worldSize && winnerId == null);
        if (ganhouAgora) {
            winnerId = currentId;
        }

        turnCount++;

        if (!ganhouAgora) {
            advanceTurn();
        }

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

        restantes.sort((a, b) -> {
            int pa = posById.getOrDefault(a, 0);
            int pb = posById.getOrDefault(b, 0);
            if (pa != pb) return Integer.compare(pb, pa);
            return Integer.compare(a, b);
        });

        for (Integer id : restantes) {
            int pos = posById.getOrDefault(id, 0);
            String nome = nameById.getOrDefault(id, String.valueOf(id));
            out.add(nome + " " + pos);
        }

        return out;
    }



    public JPanel getAuthorsPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setPreferredSize(new Dimension(360, 240));
        root.setBackground(hex("#0B1220"));
        root.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, hex("#111827")), BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel title = new JLabel("THE GREAT PROGRAMMING JOURNEY", SwingConstants.CENTER);
        title.setForeground(hex("#FBBF24"));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        root.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(authorLine("Número: 22404033  |  Nome: Miguel Lopes"));
        content.add(Box.createVerticalStrut(6));
        content.add(authorLine("Turma: LP2-2D1"));
        content.add(authorLine("Ano letivo: 2025/26"));

        root.add(content, BorderLayout.CENTER);

        JLabel footer = new JLabel("Universidade Lusófona", SwingConstants.CENTER);
        footer.setForeground(hex("#CBD5E1"));
        footer.setFont(footer.getFont().deriveFont(Font.PLAIN, 12f));
        footer.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        root.add(footer, BorderLayout.SOUTH);

        return root;
    }

    public HashMap<String, String> customizeBoard() {
        HashMap<String, String> m = new HashMap<>();

        m.put("gridBackgroundColor",   "#0B1220");
        m.put("toolbarBackgroundColor","#111827");
        m.put("slotBackgroundColor",   "#1F2937");
        m.put("slotNumberColor",       "#FBBF24");
        m.put("slotNumberFontSize",    "14");
        m.put("cellSpacing",           "3");
        m.put("logoImage", "logo.png");

        return m;
    }

    private String capitalizeFirst(String s) {
        return (s == null || s.isEmpty()) ? "" : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private JLabel authorLine(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(hex("#E2E8F0"));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 13f));
        return lbl;
    }

    private static Color hex(String rgb) {
        return Color.decode(rgb);
    }
}
