import java.io.*;
import java.util.*;

// Класс генераторов LALR(1)-таблиц
public class LALRGenerator {
    // Список терминалов
    private HashSet<String> terms;
    // Список нетерминалов
    private HashSet<String> nonterms;
    // Список правил
    private HashMap<String, ArrayList<ArrayList<String> > > rules;
    // Аксиома
    private String axiom;
    // Множества FIRST
    private HashMap<String, HashSet<String>> firsts;

    public LALRGenerator(Node treeNode, String fileName) {
        // a) Из дерева разбора находим терминалы, нетерминалы, правила, аксиому, множества FIRST
        // Параллельно с этим выполняется семантический анализ

        // 1) Находим терминалы, нетерминалы и аксиому, а также проверяем, что у всех нетерминал есть хотя бы одно
        // правило, по которому они раскрываются.
        terms = new HashSet<>();
        nonterms = new HashSet<>();
        axiom = null;
        HashSet<String> declaredNonterms = new HashSet<>();
        findTermsNontermsAxiom(treeNode, declaredNonterms);
        // Проверяем, что все найденные нетерминалы обладают правилом
        for (String nontermStr : nonterms) {
            if (!declaredNonterms.contains(nontermStr)) {
                throw new Error("Found nonterm without rules: " + nontermStr);
            }
        }

        /*
        System.out.println("Terms:");
        System.out.println(terms.toString());
        System.out.println("Nonterms:");
        System.out.println(nonterms.toString());
        System.out.println("Axiom:");
        System.out.println(axiom);
        */

        // 2) Находим правила для каждого нетерминала (хэшмеп из нетерминала в список списков его возможных раскрытий)
        // Для каждого нетерминала создаём хэшмеп
        rules = new HashMap<>();
        for (String nontermStr : nonterms) {
            rules.put(nontermStr, new ArrayList<>());
        }

        // Заполняем хэшмеп правил
        findRules(treeNode);

        /*
        System.out.println();
        System.out.println("Rules:");
        System.out.println(rules.toString());
        */

        // 3) Множества FIRST
        firsts = new HashMap<>();

        // Создаём для каждого нетерминала хэшсет first
        for (String nontermStr : nonterms) {
            firsts.put(nontermStr, new HashSet<>());
        }

        // Заполняем множества first
        fillFirstSets();

        /*
        System.out.println();
        System.out.println("FIRST:");
        System.out.println(firsts.toString());
        */

        // Проверяем, что аксиома была найдена
        if (axiom == null) {
            System.out.println("[ERROR] couldn't find axiom");
            return;
        }

        // б) Строим LALR(1)-состояния

        // 1) Создаём расширенную грамматику путём добавления нового нетерминала S' и правила S' -> S
        nonterms.add("~S~");
        rules.put("~S~", new ArrayList<>());
        ArrayList<String> ruleForNewAxiom = new ArrayList<>();
        ruleForNewAxiom.add(axiom);
        rules.get("~S~").add(ruleForNewAxiom);

        // Добавляем терминал $ в расширенную грамматику
        HashSet<String> newTerms = new HashSet<>(terms);
        newTerms.add("$");

        // 2) Создаём начальный пункт [~S~->*S$, $]
        Point startPoint = new Point("~S~", ruleForNewAxiom, 0, "$");
        // Список пунктов для начального состояния
        ArrayList<Point> startStatePoints = new ArrayList<>();
        startStatePoints.add(startPoint);
        // Список состояний до объединения
        ArrayList<State> statesBeforeUnion = new ArrayList<>();
        int currentStateNumber = 0;
        // Создаём начальное состояние
        State startState = new State(startStatePoints);
        startState.setName(String.valueOf(currentStateNumber++));
        statesBeforeUnion.add(startState);

        // Используем функцию замыкания для начального состояния
        startState.closure(nonterms, terms, rules, firsts);

        // Создаём другие состояния
        // Было ли добавлено новое состояние за итерацию
        boolean statesUpdated = true;
        while (statesUpdated) {
            statesUpdated = false;
            ArrayList<State> statesCopy = new ArrayList<>(statesBeforeUnion);
            for (State curState : statesCopy) {
                // Все символы грамматики (терминалы и нетерминалы)
                HashSet<String> grammarSymbols = new HashSet<>();
                grammarSymbols.addAll(nonterms);
                grammarSymbols.addAll(terms);

                for (String symbol : grammarSymbols) {
                    State gotoState = GOTO(terms, nonterms, rules, firsts, curState, symbol);

                    // Проверяем, получили ли мы новое состояние
                    boolean gotNewState = true;
                    if (gotoState.getPoints().size() != 0) {
                        for (State someState : statesBeforeUnion) {
                            if (someState.isEqual(gotoState)) {
                                gotNewState = false;
                                break;
                            }
                        }
                    } else {
                        gotNewState = false;
                    }
                    if (gotNewState) {
                        // Добавляем новое состояние
                        gotoState.setName(String.valueOf(currentStateNumber++));
                        statesBeforeUnion.add(gotoState);
                        statesUpdated = true;
                        curState.getGotoMap().put(symbol, gotoState.getName());
                    } else {
                        // Если это не новое состояние, то находим, с каким оно совпадает, и указываем для gotoMap
                        // Также проверяем, что совпадает только с одним
                        int amountOfEqualStates = 0;
                        State equalState = null;
                        for (State someState : statesBeforeUnion) {
                            if (someState.isEqual(gotoState)) {
                                amountOfEqualStates++;
                                equalState = someState;
                            }
                        }
                        if (amountOfEqualStates > 1) {
                            throw new Error("found more than one equal state");
                        }
                        if (amountOfEqualStates == 1) {
                            curState.getGotoMap().put(symbol, equalState.getName());
                        }
                    }
                }
            }
        }

        // 3) Формируем состояния

        /*
        System.out.println("\nStates before union:");
        for (State s : statesBeforeUnion) {
            s.printState();
        }
        */

        // Объединяем состояния с одинаковыми ядрами для получения LALR(1)-состояния
        ArrayList<State> unitedKernelStates = new ArrayList<>();
        for (State curState : statesBeforeUnion) {
            // Список номеров состояний с одинаковым ядром
            ArrayList<String> numbersOfStates = new ArrayList<>();
            numbersOfStates.add(curState.getName());
            // Пункты ядра
            ArrayList<Point> curKernel = new ArrayList<>(curState.getKernelPoints());

            // Смотрим ядра всех других состояний
            for (State otherState : statesBeforeUnion) {
                if (curState != otherState) {
                    if (curState.kernelIsEqual(otherState)) {
                        for (Point otherPoint : otherState.getKernelPoints()) {
                            boolean gotNewPoint = true;
                            for (Point curPoint : curState.getKernelPoints()) {
                                if (curPoint.isEqual(otherPoint)) {
                                    gotNewPoint = false;
                                    break;
                                }
                            }
                            if (gotNewPoint) {
                                curKernel.add(otherPoint);
                            }
                        }
                        numbersOfStates.add(otherState.getName());
                    }
                }
            }

            // Создаём состояние из объединения состояний
            State unionState = new State(curKernel);
            // Проверяем, получили ли мы новое состояние
            boolean gotNewState = true;
            for (State someState : unitedKernelStates) {
                if (someState.isEqual(unionState)) {
                    gotNewState = false;
                    break;
                }
            }
            if (gotNewState) {
                unionState.setUnionOfStatesNames(numbersOfStates);
                unitedKernelStates.add(unionState);
                String newStateName = numbersOfStates.get(0);
                for (int i = 1; i < numbersOfStates.size(); i++) {
                    newStateName += "_" + numbersOfStates.get(i);
                }
                unionState.setName(newStateName);
            }
        }

        /*
        System.out.println("\nUnited states:");
        for (State s : unitedKernelStates) {
            s.printState();
        }
        */

        // Упорядоченный список правил грамматики в виде пар левая часть, правая часть (
        // (нумерация нужна для заполнения ACTION и GOTO)
        ArrayList<Rule> simpleRules = new ArrayList<>();
        for (Map.Entry<String, ArrayList<ArrayList<String>>> entry : rules.entrySet()) {
            String nontermStr = entry.getKey();
            ArrayList<ArrayList<String>> rulesForNonterm = entry.getValue();
            for (ArrayList<String> curRule : rulesForNonterm) {
                simpleRules.add(new Rule(nontermStr, curRule));
            }
        }

        // Заполняем gotoMap для объединённых состояний
        for (State curState : statesBeforeUnion) {
            HashSet<String> grammarSymbols = new HashSet<>();
            grammarSymbols.addAll(nonterms);
            grammarSymbols.addAll(terms);

            for (String symbol : grammarSymbols) {
                if (curState.getGotoMap().containsKey(symbol)) {
                    String gotoStateName = curState.getGotoMap().get(symbol);
                    // Новое имя состояния, откуда совершается GOTO
                    State newUnionState = null;
                    for (State lalrState : unitedKernelStates) {
                        for (String stateName : lalrState.getUnionOfStatesNames()) {
                            if (curState.getName().equals(stateName)) {
                                newUnionState = lalrState;
                                break;
                            }
                        }
                    }

                    // Новое имя состояния, куда совершается GOTO
                    State newGotoState = null;
                    for (State lalrState : unitedKernelStates) {
                        for (String stateName : lalrState.getUnionOfStatesNames()) {
                            if (gotoStateName.equals(stateName)) {
                                newGotoState = lalrState;
                                break;
                            }
                        }
                    }

                    if (newUnionState != null && newGotoState != null) {
                        newUnionState.getGotoMap().put(symbol, newGotoState.getName());
                    }
                }
            }
        }

        // Замыкаем все объединённые состояния
        for (State curState : unitedKernelStates) {
            curState.closure(nonterms, terms, rules, firsts);
        }

        // Заполняем таблицы ACTION и GOTO
        for (State curState : unitedKernelStates) {
            for (Point curPoint : curState.getPoints()) {
                ArrayList<String> listForAcc = new ArrayList<>();
                listForAcc.add(axiom);

                if (curPoint.getPointPosition() < curPoint.getRightPart().size()) {
                    // Первый случая для epsilon
                    if (curPoint.getRightPart().size() == 1 && curPoint.getRightPart().get(0).equals("")) {

                        int i = 0;
                        int numberOfRule = -1;
                        // Ищем номер правила, по которому нужно сворачиваться epsilon
                        for (Rule curRule : simpleRules) {
                            if (curRule.getLeftPart().equals(curPoint.getLeftPart())) {
                                boolean rightIsEqual = true;
                                if (curRule.getRightPart().size() != curPoint.getRightPart().size()) {
                                    rightIsEqual = false;
                                }
                                for (int j = 0; j < curRule.getRightPart().size(); j++) {
                                    if (!curRule.getRightPart().get(j).equals(curPoint.getRightPart().get(j))) {
                                        rightIsEqual = false;
                                        break;
                                    }
                                }
                                if (rightIsEqual) {
                                    numberOfRule = i;
                                    break;
                                }
                            }

                            i++;
                        }
                        if (numberOfRule != -1) {
                            curState.getActionMap().put(curPoint.getLookaheadSymbol(), "r" + String.valueOf(numberOfRule));
                        } else {
                            throw new Error("numberOfRule is -1");
                        }

                    } else {
                        String nextSymbol = curPoint.getRightPart().get(curPoint.getPointPosition());
                        if (newTerms.contains(nextSymbol) && curState.getGotoMap().containsKey(nextSymbol)) {
                            curState.getActionMap().put(nextSymbol, "s" + curState.getGotoMap().get(nextSymbol));
                        }
                    }
                } else if ((curPoint.getPointPosition() == curPoint.getRightPart().size() &&
                            !curPoint.getLeftPart().equals("~S~")) || (
                                    curPoint.getRightPart().size() == 1 &&
                                            curPoint.getRightPart().get(0).equals("")
                        )) {

                    int i = 0;
                    int numberOfRule = -1;
                    // Ищем номер правила, по которому нужно сворачиваться
                    for (Rule curRule : simpleRules) {
                        if (curRule.getLeftPart().equals(curPoint.getLeftPart())) {
                            boolean rightIsEqual = true;
                            if (curRule.getRightPart().size() != curPoint.getRightPart().size()) {
                                rightIsEqual = false;
                            }
                            for (int j = 0; j < curRule.getRightPart().size(); j++) {
                                if (!curRule.getRightPart().get(j).equals(curPoint.getRightPart().get(j))) {
                                    rightIsEqual = false;
                                    break;
                                }
                            }
                            if (rightIsEqual) {
                                numberOfRule = i;
                                break;
                            }
                        }

                        i++;
                    }
                    if (numberOfRule != -1) {
                        curState.getActionMap().put(curPoint.getLookaheadSymbol(), "r" + String.valueOf(numberOfRule));
                    } else {
                        throw new Error("numberOfRule is -1");
                    }
                } else if (curPoint.isEqual(new Point("~S~", listForAcc, 1, "$"))) {
                    curState.getActionMap().put("$", "acc");
                } else {
                    throw new Error("Can't create ACTION table");
                }
            }
        }

        /*
        System.out.println("FINAL STATES:");
        for (State st : unitedKernelStates) {
            st.printState();
        }
        */

        printRulesAndTablesToFile(fileName, simpleRules, unitedKernelStates);
    }

    // Ищет терминалы, нетерминалы, аксиому по дереву разбора
    // Также заполняет список нетерминалов, для которых есть хотя бы одно правило, их раскрывающее
    public void findTermsNontermsAxiom(Node node, HashSet<String> declaredNonterms) {
        if (node.getMarker() != null) {
            String nodeMarker = node.getMarker();
            if (nodeMarker.equals("S")) {
                // (axiom S) = (P) (S) | .
                if (node.children != null && node.children.size() >= 2) {
                    // Заходим в P
                    findTermsNontermsAxiom(node.children.get(0), declaredNonterms);
                    // Заходим в S
                    findTermsNontermsAxiom(node.children.get(1), declaredNonterms);
                }
            } else if (nodeMarker.equals("P")) {
                // (P) = (L) (R) dot.
                if (node.children != null && node.children.size() >= 3) {
                    // Заходим в L
                    findTermsNontermsAxiom(node.children.get(0), declaredNonterms);
                    // Заходим в R
                    findTermsNontermsAxiom(node.children.get(1), declaredNonterms);
                }
            } else if (nodeMarker.equals("L")) {
                // (L) = nonterm equals | axiomnonterm equals.
                // Здесь также отмечаем нетерминалы, у которых есть правило
                if (node.children != null && node.children.size() >= 2) {
                    if (node.children.get(0).getToken() != null) {
                        if (node.children.get(0).getToken().getTag().equals("nonterm")) {
                            // Нашли нетерминал
                            nonterms.add(node.children.get(0).getToken().getAttr());
                            declaredNonterms.add(node.children.get(0).getToken().getAttr());
                        } else if (node.children.get(0).getToken().getTag().equals("axiomnonterm")) {
                            // Нашли аксиому
                            String foundAxiom = node.children.get(0).getToken().getAttr();
                            nonterms.add(foundAxiom);
                            declaredNonterms.add(foundAxiom);
                            if (axiom != null && !axiom.equals(foundAxiom)) {
                                // Другая аксиома уже была обнаружена => выдаём ошибку
                                throw new Error("More than one axiom are declared");
                            } else {
                                axiom = foundAxiom;
                            }
                        }
                    }
                }
            } else if (nodeMarker.equals("R")) {
                // (R) = (R1) (R) | .
                if (node.children != null && node.children.size() >= 2) {
                    // Заходим в R1
                    findTermsNontermsAxiom(node.children.get(0), declaredNonterms);
                    // Заходим в R
                    findTermsNontermsAxiom(node.children.get(1), declaredNonterms);
                }
            } else if (nodeMarker.equals("R1")) {
                // (R1) = nonterm | term | pipe.
                if (node.children != null && node.children.size() >= 1) {
                    if (node.children.get(0).getToken() != null) {
                        Token foundToken = node.children.get(0).getToken();
                        if (foundToken.getTag().equals("nonterm")) {
                            // Нашли нетерминал
                            nonterms.add(foundToken.getAttr());
                        } else if (foundToken.getTag().equals("term")) {
                            // Нашли терминал
                            terms.add(foundToken.getAttr());
                        }
                    }
                }
            }
        }
    }

    // Заполняет хэшмеп правил
    public void findRules(Node node) {
        if (node.getMarker() != null) {
            String nodeMarker = node.getMarker();
            if (nodeMarker.equals("S")) {
                // (axiom S) = (P) (S) | .
                if (node.children != null && node.children.size() >= 2) {
                    // Заходим в P
                    findRules(node.children.get(0));
                    // Заходим в S
                    findRules(node.children.get(1));
                }
            } else if (nodeMarker.equals("P")) {
                // (P) = (L) (R) dot.
                // Здесь отмечаем правила
                if (node.children != null && node.children.size() >= 3) {
                    // (L) = nonterm equals | axiomnonterm equals.
                    Node lNode = node.children.get(0);
                    if (lNode.children != null && lNode.children.size() >= 2) {
                        if (lNode.children.get(0).getToken().getTag().equals("nonterm") ||
                                lNode.children.get(0).getToken().getTag().equals("axiomnonterm")) {
                            // Нашли нетерминал
                            String nontermStr = lNode.children.get(0).getToken().getAttr();

                            // Ищем, во что нетерминал раскрывается по правилу
                            ArrayList<String> ruleRightPart = new ArrayList<>();
                            Node rNode = node.children.get(1);

                            // (R) = (R1) (R) | .
                            if (rNode.children != null && rNode.children.size() >= 2) {
                                Node r1Node = rNode.children.get(0);
                                // (R1) = nonterm | term | pipe.
                                // Раскрываем R1
                                if (r1Node.children != null && r1Node.children.size() >= 1 &&
                                        r1Node.children.get(0).getToken() != null) {
                                    Token r1NodeToken = r1Node.children.get(0).getToken();
                                    if (r1NodeToken.getTag().equals("nonterm") || r1NodeToken.getTag().equals("term")) {
                                        // Добавляем нетерминал или терминал в правило
                                        ruleRightPart.add(r1NodeToken.getAttr());
                                    } else if (r1NodeToken.getTag().equals("pipe")) {
                                        // Добавляем, если необходимо, пустое правило и создаём новое
                                        if (ruleRightPart.size() == 0) {
                                            ruleRightPart.add("");
                                        }

                                        rules.get(nontermStr).add(ruleRightPart);
                                        ruleRightPart = new ArrayList<>();
                                    }
                                }

                                // Раскрываем R
                                // (R) = (R1) (R) | .
                                Node nextRNode = rNode.children.get(1);
                                while (true) {
                                    if (nextRNode.children != null && nextRNode.children.size() >= 2) {
                                        r1Node = nextRNode.children.get(0);

                                        if (r1Node.children != null && r1Node.children.size() >= 1 &&
                                                r1Node.children.get(0).getToken() != null) {
                                            Token r1NodeToken = r1Node.children.get(0).getToken();
                                            if (r1NodeToken.getTag().equals("nonterm") || r1NodeToken.getTag().equals("term")) {
                                                // Добавляем нетерминал или терминал в правило
                                                ruleRightPart.add(r1NodeToken.getAttr());
                                            } else if (r1NodeToken.getTag().equals("pipe")) {
                                                // Добавляем, если необходимо, пустое правило и создаём новое
                                                if (ruleRightPart.size() == 0) {
                                                    ruleRightPart.add("");
                                                }

                                                rules.get(nontermStr).add(ruleRightPart);
                                                ruleRightPart = new ArrayList<>();
                                            }
                                        }

                                        nextRNode = nextRNode.children.get(1);
                                    } else if (nextRNode.children != null) {
                                        if (ruleRightPart.size() == 0) {
                                            ruleRightPart.add("");
                                        }
                                        break;
                                    }
                                }

                                rules.get(nontermStr).add(ruleRightPart);
                            } else if (rNode.children != null && rNode.children.size() >= 1) {
                                // Добавляем раскрытие в epsilon
                                ruleRightPart.add("");
                                rules.get(nontermStr).add(ruleRightPart);
                            }
                        }
                    }
                }
            }
        }
    }

    // Заполняет множества FIRST
    // (алгоритм взят из лекций по компиляторам)
    public void fillFirstSets() {
        // Было ли множество first обновлено за итерацию
        boolean firstWasUpdated = true;

        while (firstWasUpdated) {
            firstWasUpdated = false;
            // Перебираем все правила
            for (Map.Entry<String, ArrayList<ArrayList<String>>> entry : rules.entrySet()) {
                String nontermStr = entry.getKey();
                ArrayList<ArrayList<String>> rulesForNonterm = entry.getValue();

                for (ArrayList<String> rule : rulesForNonterm) {
                    HashSet<String> newFirstSet = firstSetsHelper(rule);
                    int amountOfNewSymbols = 0;
                    for (String symbol : newFirstSet) {
                        // Смотрим, сколько добавилось новых
                        if (!firsts.get(nontermStr).contains(symbol)) {
                            amountOfNewSymbols++;
                        }

                        // Также добавляем все найденные
                        firsts.get(nontermStr).add(symbol);
                    }

                    // Если хотя бы что-то было добавлено, продолжаем цикл
                    if (amountOfNewSymbols > 0) {
                        firstWasUpdated = true;
                    }
                }
            }
        }
    }

    // Вспомогательная функция по нахождения множеств first
    // (функция F из лекций по компиляторам)
    public HashSet<String> firstSetsHelper(ArrayList<String> rule) {
        HashSet<String> firstSet = new HashSet<>();
        if (rule.size() == 1 && rule.get(0).equals("")) {
            firstSet.add("");
            return firstSet;
        } else if (terms.contains(rule.get(0))) {
            firstSet.add(rule.get(0));
            return firstSet;
        } else if (nonterms.contains(rule.get(0))) {
            if (!firsts.get(rule.get(0)).contains("")) {
                for (String symbol : firsts.get(rule.get(0))) {
                    firstSet.add(symbol);
                }

                return firstSet;
            }
        } else {
            // Если раскрывается в epsilon
            HashSet<String> firstForFirstInRule = new HashSet<>(firsts.get(rule.get(0)));
            for (String symbolInRule : firstForFirstInRule) {
                if (symbolInRule.equals("")) {
                    firstForFirstInRule.remove(symbolInRule);
                }
            }
            if (rule.size() > 1) {
                ArrayList<String> ruleWithoutFirstSymbol = new ArrayList<>(rule);
                ruleWithoutFirstSymbol.remove(0);
                HashSet<String> firstForRuleWithoutFirstSymbol = firstSetsHelper(ruleWithoutFirstSymbol);
                for (String firstSymb : firstForRuleWithoutFirstSymbol) {
                    firstForFirstInRule.add(firstSymb);
                }

                return firstForFirstInRule;
            } else {
                return firstForFirstInRule;
            }
        }

        return firstSet;
    }

    // Функция нахождения GOTO(I, X), I - набор пунктов, X - символ грамматики
    public static State GOTO(HashSet<String> terms, HashSet<String> nonterms,
                             HashMap<String, ArrayList<ArrayList<String> > > rules,
                             HashMap<String, HashSet<String>> firsts,
                             State state,
                             String symbol) {
        // Список пунктов для GOTO(I, X)
        ArrayList<Point> gotoPoints = new ArrayList<>();
        for (Point curPoint : state.getPoints()) {
            // Находим правила с точкой перед X

            if (curPoint.getPointPosition() < curPoint.getRightPart().size() &&
                curPoint.getRightPart().get(curPoint.getPointPosition()).equals(symbol)) {
                // Добавляем пункт с точкой на 1 позицию дальше
                gotoPoints.add(new Point(curPoint.getLeftPart(), curPoint.getRightPart(), curPoint.getPointPosition()+1,
                        curPoint.getLookaheadSymbol()));
            }
        }

        // Создаём новое состояние из построенных пунктов и замыкаем его
        State gotoState = new State(gotoPoints);
        gotoState.closure(nonterms, terms, rules, firsts);
        return gotoState;
    }

    // Печатает правила и таблицы в Java-класс
    public void printRulesAndTablesToFile(String fileName, ArrayList<Rule> rulesToSave, ArrayList<State> parserStates) {
        try {
            FileWriter classFile = new FileWriter("src/" + fileName + ".java");
            classFile.write("import java.util.HashMap;\n\n");
            classFile.write("class " + fileName + " {\n");
            classFile.write("\tpublic static HashMap<String, HashMap<String, String>> actionTable =" +
                    "new HashMap<>(" + parserStates.size() + ");\n");
            classFile.write("\tpublic static HashMap<String, HashMap<String, String>> gotoTable = " +
                    "new HashMap<>(" + parserStates.size() + ");\n");
            // Заполняем набор правил
            classFile.write("\n\tpublic static Rule rules[] = {\n");
            for (Rule r : rulesToSave) {
                classFile.write("\t  new Rule (" + "\"" + r.getLeftPart() + "\", new String[] {");
                if (r.getRightPart().size() >= 1) {
                    classFile.write("\"" + r.getRightPart().get(0) + "\"");
                    for (int i = 1; i < r.getRightPart().size(); i++) {
                        classFile.write(", \"" + r.getRightPart().get(i) + "\"");
                    }
                }

                classFile.write("}),\n");
            }
            classFile.write("\t};\n");

            classFile.write("\n\tstatic{\n");

            // Заполняем таблицы
            classFile.write("\t\tHashMap<String, String> map;\n");
            for (State st : parserStates) {
                // Заполняем ACTION
                classFile.write("\n\t\tmap = new HashMap<>(" + st.getActionMap().size() + ");\n");
                classFile.write("\t\tactionTable.put(" + "\"" + st.getName() + "\", map);\n");
                for(Map.Entry<String, String> entry : st.getActionMap().entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    classFile.write("\t\tmap.put(" + "\"" + key + "\", " + "\"" + value + "\");\n");
                }

                // Заполняем GOTO
                classFile.write("\n\t\tmap = new HashMap<>(" + st.getGotoMap().size() + ");\n");
                classFile.write("\t\tgotoTable.put(" + "\"" + st.getName() + "\", map);\n");
                for(Map.Entry<String, String> entry : st.getGotoMap().entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    classFile.write("\t\tmap.put(" + "\"" + key + "\", " + "\"" + value + "\");\n");
                }
            }
            classFile.write("\t}\n");


            classFile.write("}");

            classFile.close();
        } catch (IOException err) {
            System.out.println("[ERROR] while writing to file: " + err);
        }
    }
}
