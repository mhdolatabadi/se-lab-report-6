package MiniJava.semantic.symbol;

import lombok.Getter;

/**
 * Created by mohammad hosein on 6/28/2015.
 */

@Getter
public record Symbol(SymbolType type, int address) {

}
