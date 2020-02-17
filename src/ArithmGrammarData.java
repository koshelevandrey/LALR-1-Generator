import java.util.HashMap;

class ArithmGrammarData {
	public static HashMap<String, HashMap<String, String>> actionTable =new HashMap<>(12);
	public static HashMap<String, HashMap<String, String>> gotoTable = new HashMap<>(12);

	public static Rule rules[] = {
	  new Rule ("T", new String[] {"F"}),
	  new Rule ("T", new String[] {"T", "*", "F"}),
	  new Rule ("E", new String[] {"T"}),
	  new Rule ("E", new String[] {"E", "+", "T"}),
	  new Rule ("F", new String[] {"n"}),
	  new Rule ("F", new String[] {"(", "E", ")"}),
	  new Rule ("~S~", new String[] {"E"}),
	};

	static{
		HashMap<String, String> map;

		map = new HashMap<>(2);
		actionTable.put("0", map);
		map.put("(", "s4_11");
		map.put("n", "s5_12");

		map = new HashMap<>(5);
		gotoTable.put("0", map);
		map.put("T", "1_8");
		map.put("E", "2");
		map.put("F", "3_10");
		map.put("(", "4_11");
		map.put("n", "5_12");

		map = new HashMap<>(4);
		actionTable.put("1_8", map);
		map.put("$", "r2");
		map.put(")", "r2");
		map.put("*", "s6_15");
		map.put("+", "r2");

		map = new HashMap<>(1);
		gotoTable.put("1_8", map);
		map.put("*", "6_15");

		map = new HashMap<>(2);
		actionTable.put("2", map);
		map.put("$", "acc");
		map.put("+", "s7_17");

		map = new HashMap<>(1);
		gotoTable.put("2", map);
		map.put("+", "7_17");

		map = new HashMap<>(4);
		actionTable.put("3_10", map);
		map.put("$", "r0");
		map.put(")", "r0");
		map.put("*", "r0");
		map.put("+", "r0");

		map = new HashMap<>(0);
		gotoTable.put("3_10", map);

		map = new HashMap<>(2);
		actionTable.put("4_11", map);
		map.put("(", "s4_11");
		map.put("n", "s5_12");

		map = new HashMap<>(5);
		gotoTable.put("4_11", map);
		map.put("T", "1_8");
		map.put("E", "9_18");
		map.put("F", "3_10");
		map.put("(", "4_11");
		map.put("n", "5_12");

		map = new HashMap<>(4);
		actionTable.put("5_12", map);
		map.put("$", "r4");
		map.put(")", "r4");
		map.put("*", "r4");
		map.put("+", "r4");

		map = new HashMap<>(0);
		gotoTable.put("5_12", map);

		map = new HashMap<>(2);
		actionTable.put("6_15", map);
		map.put("(", "s4_11");
		map.put("n", "s5_12");

		map = new HashMap<>(3);
		gotoTable.put("6_15", map);
		map.put("F", "13_19");
		map.put("(", "4_11");
		map.put("n", "5_12");

		map = new HashMap<>(2);
		actionTable.put("7_17", map);
		map.put("(", "s4_11");
		map.put("n", "s5_12");

		map = new HashMap<>(4);
		gotoTable.put("7_17", map);
		map.put("T", "14_20");
		map.put("F", "3_10");
		map.put("(", "4_11");
		map.put("n", "5_12");

		map = new HashMap<>(2);
		actionTable.put("9_18", map);
		map.put(")", "s16_21");
		map.put("+", "s7_17");

		map = new HashMap<>(2);
		gotoTable.put("9_18", map);
		map.put(")", "16_21");
		map.put("+", "7_17");

		map = new HashMap<>(4);
		actionTable.put("13_19", map);
		map.put("$", "r1");
		map.put(")", "r1");
		map.put("*", "r1");
		map.put("+", "r1");

		map = new HashMap<>(0);
		gotoTable.put("13_19", map);

		map = new HashMap<>(4);
		actionTable.put("14_20", map);
		map.put("$", "r3");
		map.put(")", "r3");
		map.put("*", "s6_15");
		map.put("+", "r3");

		map = new HashMap<>(1);
		gotoTable.put("14_20", map);
		map.put("*", "6_15");

		map = new HashMap<>(4);
		actionTable.put("16_21", map);
		map.put("$", "r5");
		map.put(")", "r5");
		map.put("*", "r5");
		map.put("+", "r5");

		map = new HashMap<>(0);
		gotoTable.put("16_21", map);
	}
}