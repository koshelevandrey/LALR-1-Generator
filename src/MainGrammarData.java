import java.util.HashMap;

class MainGrammarData {
	public static HashMap<String, HashMap<String, String>> actionTable =new HashMap<>(16);
	public static HashMap<String, HashMap<String, String>> gotoTable = new HashMap<>(16);

	public static Rule rules[] = {
	  new Rule ("P", new String[] {"L", "R", "dot"}),
	  new Rule ("R", new String[] {"R1", "R"}),
	  new Rule ("R", new String[] {""}),
	  new Rule ("S", new String[] {"P", "S"}),
	  new Rule ("S", new String[] {""}),
	  new Rule ("~S~", new String[] {"S"}),
	  new Rule ("L", new String[] {"nonterm", "equals"}),
	  new Rule ("L", new String[] {"axiomnonterm", "equals"}),
	  new Rule ("R1", new String[] {"nonterm"}),
	  new Rule ("R1", new String[] {"term"}),
	  new Rule ("R1", new String[] {"pipe"}),
	};

	static{
		HashMap<String, String> map;

		map = new HashMap<>(3);
		actionTable.put("0", map);
		map.put("nonterm", "s2");
		map.put("axiomnonterm", "s4");
		map.put("$", "r4");

		map = new HashMap<>(5);
		gotoTable.put("0", map);
		map.put("P", "1");
		map.put("nonterm", "2");
		map.put("S", "3");
		map.put("axiomnonterm", "4");
		map.put("L", "5");

		map = new HashMap<>(3);
		actionTable.put("1", map);
		map.put("nonterm", "s2");
		map.put("axiomnonterm", "s4");
		map.put("$", "r4");

		map = new HashMap<>(5);
		gotoTable.put("1", map);
		map.put("P", "1");
		map.put("nonterm", "2");
		map.put("S", "6");
		map.put("axiomnonterm", "4");
		map.put("L", "5");

		map = new HashMap<>(1);
		actionTable.put("2", map);
		map.put("equals", "s7");

		map = new HashMap<>(1);
		gotoTable.put("2", map);
		map.put("equals", "7");

		map = new HashMap<>(1);
		actionTable.put("3", map);
		map.put("$", "acc");

		map = new HashMap<>(0);
		gotoTable.put("3", map);

		map = new HashMap<>(1);
		actionTable.put("4", map);
		map.put("equals", "s8");

		map = new HashMap<>(1);
		gotoTable.put("4", map);
		map.put("equals", "8");

		map = new HashMap<>(4);
		actionTable.put("5", map);
		map.put("nonterm", "s10");
		map.put("dot", "r2");
		map.put("term", "s11");
		map.put("pipe", "s12");

		map = new HashMap<>(5);
		gotoTable.put("5", map);
		map.put("R", "9");
		map.put("nonterm", "10");
		map.put("term", "11");
		map.put("pipe", "12");
		map.put("R1", "13");

		map = new HashMap<>(1);
		actionTable.put("6", map);
		map.put("$", "r3");

		map = new HashMap<>(0);
		gotoTable.put("6", map);

		map = new HashMap<>(5);
		actionTable.put("7", map);
		map.put("nonterm", "r6");
		map.put("axiomnonterm", "r6");
		map.put("$", "r6");
		map.put("term", "r6");
		map.put("pipe", "r6");

		map = new HashMap<>(0);
		gotoTable.put("7", map);

		map = new HashMap<>(5);
		actionTable.put("8", map);
		map.put("nonterm", "r7");
		map.put("axiomnonterm", "r7");
		map.put("$", "r7");
		map.put("term", "r7");
		map.put("pipe", "r7");

		map = new HashMap<>(0);
		gotoTable.put("8", map);

		map = new HashMap<>(1);
		actionTable.put("9", map);
		map.put("dot", "s14");

		map = new HashMap<>(1);
		gotoTable.put("9", map);
		map.put("dot", "14");

		map = new HashMap<>(4);
		actionTable.put("10", map);
		map.put("nonterm", "r8");
		map.put("dot", "r8");
		map.put("term", "r8");
		map.put("pipe", "r8");

		map = new HashMap<>(0);
		gotoTable.put("10", map);

		map = new HashMap<>(4);
		actionTable.put("11", map);
		map.put("nonterm", "r9");
		map.put("dot", "r9");
		map.put("term", "r9");
		map.put("pipe", "r9");

		map = new HashMap<>(0);
		gotoTable.put("11", map);

		map = new HashMap<>(4);
		actionTable.put("12", map);
		map.put("nonterm", "r10");
		map.put("dot", "r10");
		map.put("term", "r10");
		map.put("pipe", "r10");

		map = new HashMap<>(0);
		gotoTable.put("12", map);

		map = new HashMap<>(4);
		actionTable.put("13", map);
		map.put("nonterm", "s10");
		map.put("dot", "r2");
		map.put("term", "s11");
		map.put("pipe", "s12");

		map = new HashMap<>(5);
		gotoTable.put("13", map);
		map.put("R", "15");
		map.put("nonterm", "10");
		map.put("term", "11");
		map.put("pipe", "12");
		map.put("R1", "13");

		map = new HashMap<>(3);
		actionTable.put("14", map);
		map.put("nonterm", "r0");
		map.put("axiomnonterm", "r0");
		map.put("$", "r0");

		map = new HashMap<>(0);
		gotoTable.put("14", map);

		map = new HashMap<>(1);
		actionTable.put("15", map);
		map.put("dot", "r1");

		map = new HashMap<>(0);
		gotoTable.put("15", map);
	}
}