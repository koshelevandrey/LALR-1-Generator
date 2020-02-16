/*
(axiom S) = (S1).
(S1) = (P) (S2).
(S2) = (P) (S2) | .
(P) = (L) (P1).
(P1) = (R) (R1) dot | dot.
(L) = nonterm equals | axiomnonterm equals.
(R) = nonterm | term | pipe.
(R1) = (R) (R1) | .
 */

import java.util.ArrayList;

// Парсер для языка описания грамматик методом рекурсивного спуска
public class RecursiveParser {
    private ArrayList<Token> tokens;
    private int curTokenIndex;

    public RecursiveParser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        curTokenIndex = 0;
    }

    // Возвращает дерево разбора
    public Node getParseTree() {
        return S();
    }

    // (axiom S) = (S1).
    public Node S() {
        Node treeRoot = new Node(null, new ArrayList<>(), "S", null);

        S1(treeRoot);
        return treeRoot;
    }

    // (S1) = (P) (S2).
    public void S1(Node parentNode) {
        Node curNode = new Node(parentNode, new ArrayList<>(), "S1", null);
        parentNode.children.add(curNode);

        P(curNode);
        S2(curNode);
    }

    // (P) = (L) (P1).
    public void P(Node parentNode) {
        Node curNode = new Node(parentNode, new ArrayList<>(), "P", null);
        parentNode.children.add(curNode);

        L(curNode);
        P1(curNode);
    }

    // (L) = nonterm equals | axiomnonterm equals.
    public void L(Node parentNode) {
        Node curNode = new Node(parentNode, new ArrayList<>(), "L", null);
        parentNode.children.add(curNode);

        Token curToken = tokens.get(curTokenIndex);
        curTokenIndex++;
        if (curToken.getTag().equals("nonterm") || curToken.getTag().equals("axiomnonterm")) {
            curNode.addChild(new Node(curNode, null, null, curToken));
            curToken = tokens.get(curTokenIndex);
            curTokenIndex++;
            if (curToken.getTag().equals("equals")) {
                curNode.addChild(new Node(curNode, null, null, curToken));
            } else {
                throw new Error("couldn't find equals token in L");
            }
        } else {
            throw new Error("couldn't find nonterm or axiomnonterm in L");
        }
    }

    // (P1) = (R) (R1) dot | dot.
    // (R) = nonterm | term | pipe.
    // Здесь добавляем epsilon для второго варианта P1
    public void P1(Node parentNode) {
        Node curNode = new Node(parentNode, new ArrayList<>(), "P1", null);
        parentNode.children.add(curNode);

        Token curToken = tokens.get(curTokenIndex);
        if (curToken.getTag().equals("dot")) {
            curNode.addChild(new Node(curNode, null, null,
                    new Token("epsilon", null, null, "epsilon")));
            curNode.addChild(new Node(curNode, null, null, curToken));

            curTokenIndex++;
        } else {
            R(curNode);
            R1(curNode);
            curToken = tokens.get(curTokenIndex);
            curTokenIndex++;
            if (curToken.getTag().equals("dot")) {
                curNode.addChild(new Node(curNode, null, null, curToken));
            } else {
                throw new Error("couldn't find dot in P1");
            }
        }
    }

    // (R) = nonterm | term | pipe.
    public void R(Node parentNode) {
        Node curNode = new Node(parentNode, new ArrayList<>(), "R", null);
        parentNode.children.add(curNode);

        Token curToken = tokens.get(curTokenIndex);
        curTokenIndex++;
        if (curToken.getTag().equals("nonterm") || curToken.getTag().equals("term") || curToken.getTag().equals("pipe")) {
            curNode.addChild(new Node(curNode, null, null, curToken));
        } else {
            throw new Error("couldn't find nonterm, term or pipe in R");
        }
    }

    // (R1) = (R) (R1) | .
    // (R) = nonterm | term | pipe.
    // Для второго варианта добавляем epsilon
    public void R1(Node parentNode) {
        Node curNode = new Node(parentNode, new ArrayList<>(), "R1", null);
        parentNode.children.add(curNode);

        Token curToken = tokens.get(curTokenIndex);
        if (curToken.getTag().equals("nonterm") || curToken.getTag().equals("term") || curToken.getTag().equals("pipe")) {
            R(curNode);
            R1(curNode);
        } else {
            curNode.addChild(new Node(curNode, null, null,
                    new Token("epsilon", null, null, "epsilon")));
        }
    }

    // (S2) = (P) (S2) | .
    // (P) = (L) (P1).
    // (L) = nonterm equals | axiomnonterm equals.
    // Во втором случае S2 добавляем epsilon
    public void S2(Node parentNode) {
        Node curNode = new Node(parentNode, new ArrayList<>(), "S2", null);
        parentNode.children.add(curNode);

        if (curTokenIndex >= tokens.size()) {
            curNode.addChild(new Node(curNode, null, null,
                    new Token("epsilon", null, null, "epsilon")));
        } else {
            Token curToken = tokens.get(curTokenIndex);
            if (curToken.getTag().equals("nonterm") || curToken.getTag().equals("axiomnonterm")) {
                P(curNode);
                S2(curNode);
            } else {
                curNode.addChild(new Node(curNode, null, null,
                        new Token("epsilon", null, null, "epsilon")));
            }
        }
    }
}
