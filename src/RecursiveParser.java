/*
(axiom S) = (P) (S) | .
(P) = (L) (R) dot.
(L) = nonterm equals | axiomnonterm equals.
(R) = (R1) (R) | .
(R1) = nonterm | term | pipe.
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
        Node treeRoot = new Node(null, new ArrayList<>(), "S", null);

        if (tokens.size() == 0) {
            treeRoot.addChild(new Node(treeRoot, null, null,
                    new Token("epsilon", null, null, "epsilon")));
            return treeRoot;
        }

        Token curToken = tokens.get(curTokenIndex);
        if (curToken.getTag().equals("nonterm") || curToken.getTag().equals("axiomnonterm")) {
            P(treeRoot);
            S(treeRoot);
        } else {
            treeRoot.addChild(new Node(treeRoot, null, null,
                    new Token("epsilon", null, null, "epsilon")));
        }

        return treeRoot;
    }

    // (axiom S) = (P) (S) | .
    public void S(Node parentNode) {
        Node curNode = new Node(parentNode, new ArrayList<>(), "S", null);
        parentNode.children.add(curNode);

        if (tokens.size() == curTokenIndex) {
            curNode.addChild(new Node(curNode, null, null,
                    new Token("epsilon", null, null, "epsilon")));
        } else {
            Token curToken = tokens.get(curTokenIndex);
            if (curToken.getTag().equals("nonterm") || curToken.getTag().equals("axiomnonterm")) {
                P(curNode);
                S(curNode);
            } else {
                curNode.addChild(new Node(curNode, null, null,
                        new Token("epsilon", null, null, "epsilon")));
            }
        }
    }

    // (P) = (L) (R) dot.
    public void P(Node parentNode) {
        Node curNode = new Node(parentNode, new ArrayList<>(), "P", null);
        parentNode.children.add(curNode);

        L(curNode);
        R(curNode);
        Token curToken = tokens.get(curTokenIndex);
        curTokenIndex++;
        if (curToken.getTag().equals("dot")) {
            curNode.addChild(new Node(curNode, null, null, curToken));
        } else {
            throw new Error("couldn't find dot in P");
        }

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

    // (R) = (R1) (R) | .
    // (R1) = nonterm | term | pipe.
    public void R(Node parentNode) {
        Node curNode = new Node(parentNode, new ArrayList<>(), "R", null);
        parentNode.children.add(curNode);

        Token curToken = tokens.get(curTokenIndex);
        if (curToken.getTag().equals("nonterm") || curToken.getTag().equals("term") || curToken.getTag().equals("pipe")) {
            R1(curNode);
            R(curNode);
        } else {
            curNode.addChild(new Node(curNode, null, null,
                    new Token("epsilon", null, null, "epsilon")));
        }
    }

    // (R1) = nonterm | term | pipe.
    public void R1(Node parentNode) {
        Node curNode = new Node(parentNode, new ArrayList<>(), "R1", null);
        parentNode.children.add(curNode);

        Token curToken = tokens.get(curTokenIndex);
        curTokenIndex++;
        if (curToken.getTag().equals("nonterm") || curToken.getTag().equals("term") || curToken.getTag().equals("pipe")) {
            curNode.addChild(new Node(curNode, null, null, curToken));
        } else {
            throw new Error("couldn't find nonterm, term or pipe in R");
        }
    }
}
