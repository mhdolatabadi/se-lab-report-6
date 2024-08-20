package MiniJava.parser;

import MiniJava.scanner.token.Token;
import lombok.Getter;

@Getter
public class GrammarSymbol {
    private final boolean isTerminal;
    private NonTerminal nonTerminal;
    private Token terminal;

    public GrammarSymbol(NonTerminal nonTerminal) {
        this.nonTerminal = nonTerminal;
        isTerminal = false;
    }

    public GrammarSymbol(Token terminal) {
        this.terminal = terminal;
        isTerminal = true;
    }

}
