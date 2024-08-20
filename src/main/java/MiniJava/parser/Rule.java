package MiniJava.parser;

import MiniJava.scanner.token.Token;

import java.util.ArrayList;
import lombok.Getter;

/**
 * Created by mohammad hosein on 6/25/2015.
 */

@Getter
public class Rule {
    private final NonTerminal LHS;
    private final ArrayList<GrammarSymbol> RHS;
    private int semanticAction;

    public Rule(String stringRule) {
        int index = stringRule.indexOf("#");
        if (index != -1) {
            try {
                semanticAction = Integer.parseInt(stringRule.substring(index + 1));
            } catch (NumberFormatException ex) {
                semanticAction = 0;
            }
            stringRule = stringRule.substring(0, index);
        } else {
            semanticAction = 0;
        }
        String[] splited = stringRule.split("->");
        LHS = NonTerminal.valueOf(splited[0]);
        RHS = new ArrayList<>();
        if (splited.length > 1) {
            String[] RHSs = splited[1].split(" ");
            for (String s : RHSs) {
                try {
                    RHS.add(new GrammarSymbol(NonTerminal.valueOf(s)));
                } catch (Exception e) {
                    RHS.add(new GrammarSymbol(new Token(Token.getTyepFormString(s), s)));
                }
            }
        }
    }

}

