import java.util.ArrayList;
import java.util.HashMap;

public class LRParser {
    private ArrayList<Rule> rules;
    private HashMap<String, HashMap<String, HashMap<String, String>>> table;

    public LRParser(ArrayList<Rule> rules,
                    HashMap<String, HashMap<String, HashMap<String, String>>> table) {
        this.rules = rules;
        this.table = table;
    }

    // Парсит и возвращает дерево разбора
    public Node Parse(ArrayList<Token> tokens) {
        // Стек состояний
        ArrayList<String> statesStack = new ArrayList<>();
        statesStack.add("0");

        // Добавляем токен $ в конец списка токенов
        tokens.add(new Token("$", null, null, "$"));
        Token curToken = tokens.get(0);
        int tokenIndex = 1;

        // Используется для составления дерева разбора
        ArrayList<Node> nodes = new ArrayList<>();

        while (true) {

            /*
            System.out.println("STATE OF PARSE:");
            System.out.println("CUR TOKEN:" + curToken.getTag());
            System.out.println("STACK:");
            for (String str : statesStack) {
                System.out.print(str + " ");
            }
            System.out.println();

            System.out.println("TREE LIST:");
            for (Node nod : nodes) {
                System.out.print(nod.toString() + " ");
            }
            System.out.println();
            System.out.println();
            System.out.println();
            */
            /*
            if (nodes.size() != 0) {
                System.out.println("TREE:");
                System.out.println(MainApp.treeToString(nodes.get(0)));
            }
            System.out.println();
            */

            String curStateName = statesStack.get(statesStack.size()-1);
            String actionStr = table.get(curStateName).get("ACTION").get(curToken.getTag());
            if (actionStr == null) {
                throw new Error("syntax error");
            }
            if (actionStr.substring(0, 1).equals("s")) {
                statesStack.add(actionStr.substring(1));
                nodes.add(new Node(null, new ArrayList<>(), null, curToken));
                curToken = tokens.get(tokenIndex++);
            }
            if (actionStr.substring(0, 1).equals("r")) {
                int numberOfRule = Integer.valueOf(actionStr.substring(1));
                Rule ruleToUse = rules.get(numberOfRule);

                /*
                System.out.println("RULE USED:");
                System.out.print("["+ruleToUse.getLeftPart() + " -> ");
                for (String rulePart : ruleToUse.getRightPart()) {
                    System.out.print(rulePart + " ");
                }
                System.out.println("]");
                System.out.println();
                System.out.println();
                */

                // Снимаем столько состояний, сколько символов в правой части правила
                for (int i = 0; i < ruleToUse.getRightPart().size(); i++) {
                    if (!ruleToUse.getRightPart().get(i).equals("")) {
                        // Если не epsilon
                        statesStack.remove(statesStack.size()-1);
                    }
                }
                curStateName = statesStack.get(statesStack.size()-1);

                /*
                System.out.println("STACK CHANGED:");
                for (String str : statesStack) {
                    System.out.print(str + " ");
                }
                System.out.println();
                System.out.println();
                System.out.println();
                */

                String stateToAdd = table.get(curStateName).get("GOTO").get(ruleToUse.getLeftPart());
                statesStack.add(stateToAdd);

                // Добавляем epsilon в список вершин, если встретили пустое правило
                if (ruleToUse.getRightPart().size() == 1 && ruleToUse.getRightPart().get(0).equals("")) {
                    nodes.add(new Node(null, new ArrayList<>(),
                            null, new Token("epsilon", null, null, "epsilon")));
                }

                // Меняем дерево разбора
                ArrayList<Node> childrenNodes = new ArrayList<>();
                for (int i = nodes.size() - ruleToUse.getRightPart().size(); i < nodes.size(); i++) {
                    childrenNodes.add(nodes.get(i));
                }

                // Убираем последние ноды (терминалы, нетерминалы), соответствующие применённому правилу
                ArrayList<Node> newNodes = new ArrayList<>();
                for (int i = 0; i < nodes.size() - ruleToUse.getRightPart().size(); i++) {
                    newNodes.add(nodes.get(i));
                }
                nodes = newNodes;

                Node ruleNode = new Node(null, new ArrayList<>(), ruleToUse.getLeftPart(), null);
                for (Node childNode : childrenNodes) {
                    ruleNode.children.add(childNode);
                    childNode.setParent(ruleNode);
                }

                nodes.add(ruleNode);
            }
            if (actionStr.equals("acc")) {
                return nodes.get(0);
            }
        }
    }
}
