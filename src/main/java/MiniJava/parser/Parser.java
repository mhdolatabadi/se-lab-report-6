package MiniJava.parser;

import MiniJava.Log.Log;
import MiniJava.codeGenerator.CodeGenerator;
import MiniJava.errorHandler.ErrorHandler;
import MiniJava.scanner.LexicalAnalyzer;
import MiniJava.scanner.token.Token;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Stack;

public class Parser {
    private final ArrayList<Rule> rules;
    private final Stack<Integer> parsStack;
    private ParseTable parseTable;
    private final CodeGenerator cg;

    public Parser() {
        parsStack = new Stack<>();
        parsStack.push(0);
        try {
            parseTable = new ParseTable(Files.readAllLines(Paths.get("src/main/resources/parseTable")).get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        rules = new ArrayList<>();
        try {
            for (String stringRule : Files.readAllLines(Paths.get("src/main/resources/Rules"))) {
                rules.add(new Rule(stringRule));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        cg = new CodeGenerator();
    }

    public void startParse(java.util.Scanner sc) {
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(sc);
        Token lookAhead = lexicalAnalyzer.getNextToken();
        boolean finish = false;
        Action currentAction;
        while (!finish) {
            try {
                Log.print(/*"lookahead : "+*/ lookAhead.toString() + "\t" + parsStack.peek());
                currentAction = parseTable.getActionTable(parsStack.peek(), lookAhead);
                Log.print(currentAction.toString());

                switch (currentAction.getAction()) {
                    case shift:
                        parsStack.push(currentAction.getNumber());
                        lookAhead = lexicalAnalyzer.getNextToken();
                        break;
                    case reduce:
                        Rule rule = rules.get(currentAction.getNumber());
                        for (int i = 0; i < rule.getRHS().size(); i++) {
                            parsStack.pop();
                        }
                        Log.print(/*"state : " +*/ parsStack.peek() + "\t" + rule.getLHS());
                        parsStack.push(parseTable.getGotoTable(parsStack.peek(), rule.getLHS()));
                        Log.print(/*"new State : " + */parsStack.peek() + "");
                        try {
                            cg.semanticFunction(rule.getSemanticAction(), lookAhead);
                        } catch (Exception e) {
                            Log.print("Code Genetator Error");
                        }
                        break;
                    case accept:
                        finish = true;
                        break;
                }
                Log.print("");
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
        if (!ErrorHandler.hasError) cg.printMemory();
    }
}
