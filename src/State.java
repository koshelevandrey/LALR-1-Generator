import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

// Класс состояний автомата
public class State {
    // Номер состояния
    private String name;
    // Набор пунктов, составляющих ядро состояния
    private ArrayList<Point> kernelPoints;
    // Набор пунктов состояния
    private ArrayList<Point> points;
    // Таблицы ACTION и GOTO для состояния
    private HashMap<String, String> actionMap;
    private HashMap<String, String> gotoMap;
    // Объедининем каких множеств является
    private ArrayList<String> unionOfStatesNames;

    public State(ArrayList<Point> points) {
        name = null;
        this.kernelPoints = new ArrayList<>(points);
        this.points = new ArrayList<>(points);
        actionMap = new HashMap<>();
        gotoMap = new HashMap<>();
        unionOfStatesNames = new ArrayList<>();
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public ArrayList<Point> getKernelPoints() {
        return kernelPoints;
    }

    public HashMap<String, String> getActionMap() {
        return actionMap;
    }

    public HashMap<String, String> getGotoMap() {
        return gotoMap;
    }

    public void setUnionOfStatesNames(ArrayList<String> unionOfStatesNames) {
        this.unionOfStatesNames = unionOfStatesNames;
    }

    public ArrayList<String> getUnionOfStatesNames() {
        return unionOfStatesNames;
    }

    // Применяет функцию замыкания ко всем пунктам состояния
    public void closure(HashSet<String> nonterms, HashSet<String> terms,
                        HashMap<String, ArrayList<ArrayList<String>>> rules,
                        HashMap<String, HashSet<String>> firsts) {
        // Были ли добавлены в состояние новые пункты
        boolean updated = true;
        while (updated) {
            // Найдём все пункты, в которых точка стоит перед нетерминалом
            ArrayList<Point> pointsToUseClosure = new ArrayList<>();

            for (Point curPoint : points) {
                if (curPoint.getPointPosition() < curPoint.getRightPart().size() &&
                    nonterms.contains(curPoint.getRightPart().get(curPoint.getPointPosition()))) {
                    // Нашли пункт, где точка находится перед нетерминалом
                    pointsToUseClosure.add(curPoint);
                }
            }

            int curPointsAmount = points.size();
            if (curPointsAmount == 0) {
                // Нет пунктов с точкой перед нетерминалом => не для чего применять замыкание
                updated = false;
            }

            for (Point curPoint : pointsToUseClosure) {
                // Выполняем замыкание для пункта
                // Сам нетерминал после точки
                String nontermAfterPoint = curPoint.getRightPart().get(curPoint.getPointPosition());
                // Правила для этого нетерминала
                ArrayList<ArrayList<String>> nontermAfterPointRules = rules.get(nontermAfterPoint);

                // Рассматриваем каждое правило нетерминала
                for (ArrayList<String> rule : nontermAfterPointRules) {
                    // Следующий после данного нетерминала символ в пункте
                    String symbolAfter = null;
                    if (curPoint.getPointPosition() < curPoint.getRightPart().size() - 1) {
                        symbolAfter = curPoint.getRightPart().get(curPoint.getPointPosition()+1);
                    }

                    // FIRST
                    HashSet<String> symbolAfterFirstSet = new HashSet<>();
                    if (symbolAfter == null) {
                        if (curPoint.getLookaheadSymbol().equals("")) {
                            symbolAfterFirstSet.add("$");
                        } else {
                            symbolAfterFirstSet.add(curPoint.getLookaheadSymbol());
                        }
                    } else if (terms.contains(symbolAfter)) {
                        symbolAfterFirstSet.add(symbolAfter);
                    } else if (nonterms.contains(symbolAfter)) {
                        if (firsts.get(symbolAfter).contains("")) {
                            for (String symbol : firsts.get(symbolAfter)) {
                                if (!symbol.equals("")) {
                                    symbolAfterFirstSet.add(symbol);
                                }
                            }
                            symbolAfterFirstSet.remove("");
                            symbolAfterFirstSet.add(curPoint.getLookaheadSymbol());
                        } else {
                            for (String symbol : firsts.get(symbolAfter)) {
                                symbolAfterFirstSet.add(symbol);
                            }
                        }
                    } else {
                        throw new Error("couldn't find first in closure for: " + symbolAfter);
                    }

                    for (String symbol : symbolAfterFirstSet) {
                        // Добавляем новый пункт, если такого же ещё нет в состоянии
                        boolean alreadyHasSuchPoint = false;
                        Point newPoint = new Point(nontermAfterPoint, rule, 0, symbol);

                        // Сравниваем со всеми пунктами
                        for (Point someStatePoint : points) {
                            if (newPoint.isEqual(someStatePoint)) {
                                alreadyHasSuchPoint = true;
                                break;
                            }
                        }

                        if (!alreadyHasSuchPoint) {
                            points.add(newPoint);
                        }
                    }
                }
            }

            // Смотрим, изменилось ли число пунктов в состоянии
            if (points.size() == curPointsAmount) {
                updated = false;
            }
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void printState() {
        System.out.println("\nState " + name + ":");
        for (Point p : this.getPoints()) {
            System.out.println(p);
        }
        System.out.println("ACTION:");
        System.out.println(this.getActionMap().toString());
        System.out.println("GOTO:");
        System.out.println(this.getGotoMap().toString());
    }

    // Сравнение состояний
    public boolean isEqual(State otherState) {
        if (this.points.size() != otherState.points.size()) {
            return false;
        }

        for (Point curStatePoint : this.points) {
            boolean hasSuchPoint = false;
            for (Point otherStatePoint : otherState.points) {
                if (curStatePoint.isEqual(otherStatePoint)) {
                    hasSuchPoint = true;
                    break;
                }
            }
            if (!hasSuchPoint) {
                return false;
            }
        }

        for (Point otherStatePoint : otherState.points) {
            boolean hasSuchPoint = false;
            for (Point curStatePoint : this.points) {
                if (otherStatePoint.isEqual(curStatePoint)) {
                    hasSuchPoint = true;
                    break;
                }
            }
            if (!hasSuchPoint) {
                return false;
            }
        }

        return true;
    }

    // Сравнение ядер состояний (без символа предпросмотра)
    public boolean kernelIsEqual(State otherState) {
        for (Point curStatePoint : this.points) {
            boolean hasSuchKernel = false;
            for (Point otherStatePoint : otherState.points) {
                if (curStatePoint.kernelIsEqual(otherStatePoint)) {
                    hasSuchKernel = true;
                    break;
                }
            }
            if (!hasSuchKernel) {
                return false;
            }
        }

        for (Point otherStatePoint : otherState.points) {
            boolean hasSuchKernel = false;
            for (Point curStatePoint : this.points) {
                if (otherStatePoint.kernelIsEqual(curStatePoint)) {
                    hasSuchKernel = true;
                    break;
                }
            }
            if (!hasSuchKernel) {
                return false;
            }
        }

        return true;
    }
}
