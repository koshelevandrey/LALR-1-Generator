import java.util.HashMap;

class ArithmGrammarData {
	public static HashMap<String, HashMap<String, String>> actionTable =new HashMap<>(16);
	public static HashMap<String, HashMap<String, String>> gotoTable = new HashMap<>(16);

	public static Rule rules[] = {
	  new Rule ("T", new String[] {"F", "T1"}),
	  new Rule ("E", new String[] {"T", "E1"}),
	  new Rule ("F", new String[] {"n"}),
	  new Rule ("F", new String[] {"(", "E", ")"}),
	  new Rule ("~S~", new String[] {"E"}),
	  new Rule ("E1", new String[] {"+", "T", "E1"}),
	  new Rule ("E1", new String[] {""}),
	  new Rule ("T1", new String[] {"*", "F", "T1"}),
	  new Rule ("T1", new String[] {""}),
	};

	static{
		HashMap<String, String> map;

		map = new HashMap<>(2);
		actionTable.put("0", map);
		map.put("(", "s4_13");
		map.put("n", "s5_14");

		map = new HashMap<>(5);
		gotoTable.put("0", map);
		map.put("T", "1_10");
		map.put("E", "2");
		map.put("F", "3_12");
		map.put("(", "4_13");
		map.put("n", "5_14");

		map = new HashMap<>(3);
		actionTable.put("1_10", map);
		map.put("$", "r6");
		map.put(")", "r6");
		map.put("+", "s6_17");

		map = new HashMap<>(2);
		gotoTable.put("1_10", map);
		map.put("+", "6_17");
		map.put("E1", "7_18");

		map = new HashMap<>(1);
		actionTable.put("2", map);
		map.put("$", "acc");

		map = new HashMap<>(0);
		gotoTable.put("2", map);

		map = new HashMap<>(4);
		actionTable.put("3_12", map);
		map.put("$", "r8");
		map.put(")", "r8");
		map.put("*", "s8_20");
		map.put("+", "r8");

		map = new HashMap<>(2);
		gotoTable.put("3_12", map);
		map.put("*", "8_20");
		map.put("T1", "9_21");

		map = new HashMap<>(2);
		actionTable.put("4_13", map);
		map.put("(", "s4_13");
		map.put("n", "s5_14");

		map = new HashMap<>(5);
		gotoTable.put("4_13", map);
		map.put("T", "1_10");
		map.put("E", "11_22");
		map.put("F", "3_12");
		map.put("(", "4_13");
		map.put("n", "5_14");

		map = new HashMap<>(4);
		actionTable.put("5_14", map);
		map.put("$", "r2");
		map.put(")", "r2");
		map.put("*", "r2");
		map.put("+", "r2");

		map = new HashMap<>(0);
		gotoTable.put("5_14", map);

		map = new HashMap<>(2);
		actionTable.put("6_17", map);
		map.put("(", "s4_13");
		map.put("n", "s5_14");

		map = new HashMap<>(4);
		gotoTable.put("6_17", map);
		map.put("T", "15_25");
		map.put("F", "3_12");
		map.put("(", "4_13");
		map.put("n", "5_14");

		map = new HashMap<>(2);
		actionTable.put("7_18", map);
		map.put("$", "r1");
		map.put(")", "r1");

		map = new HashMap<>(0);
		gotoTable.put("7_18", map);

		map = new HashMap<>(2);
		actionTable.put("8_20", map);
		map.put("(", "s4_13");
		map.put("n", "s5_14");

		map = new HashMap<>(3);
		gotoTable.put("8_20", map);
		map.put("F", "16_26");
		map.put("(", "4_13");
		map.put("n", "5_14");

		map = new HashMap<>(3);
		actionTable.put("9_21", map);
		map.put("$", "r0");
		map.put(")", "r0");
		map.put("+", "r0");

		map = new HashMap<>(0);
		gotoTable.put("9_21", map);

		map = new HashMap<>(1);
		actionTable.put("11_22", map);
		map.put(")", "s19_27");

		map = new HashMap<>(1);
		gotoTable.put("11_22", map);
		map.put(")", "19_27");

		map = new HashMap<>(3);
		actionTable.put("15_25", map);
		map.put("$", "r6");
		map.put(")", "r6");
		map.put("+", "s6_17");

		map = new HashMap<>(2);
		gotoTable.put("15_25", map);
		map.put("+", "6_17");
		map.put("E1", "23_28");

		map = new HashMap<>(4);
		actionTable.put("16_26", map);
		map.put("$", "r8");
		map.put(")", "r8");
		map.put("*", "s8_20");
		map.put("+", "r8");

		map = new HashMap<>(2);
		gotoTable.put("16_26", map);
		map.put("*", "8_20");
		map.put("T1", "24_29");

		map = new HashMap<>(4);
		actionTable.put("19_27", map);
		map.put("$", "r3");
		map.put(")", "r3");
		map.put("*", "r3");
		map.put("+", "r3");

		map = new HashMap<>(0);
		gotoTable.put("19_27", map);

		map = new HashMap<>(2);
		actionTable.put("23_28", map);
		map.put("$", "r5");
		map.put(")", "r5");

		map = new HashMap<>(0);
		gotoTable.put("23_28", map);

		map = new HashMap<>(3);
		actionTable.put("24_29", map);
		map.put("$", "r7");
		map.put(")", "r7");
		map.put("+", "r7");

		map = new HashMap<>(0);
		gotoTable.put("24_29", map);
	}
}