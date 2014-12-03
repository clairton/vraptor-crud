package br.eti.clairton.vraptor.crud;

public class Param {
    public static final String PAGE = "page";
    
    public static final String PER_PAGE = "per_page";
    
    public static final String ORDER_BY = "order_by";
    
    /**
     * Campo nome do campo, se for um caminho, separa-se por ponto
     */
    private static final String FIELD = "f[]";
    
    /**
     * Valor
     */
    private static final String VALUE = "v";
    
    /**
     * Operação(<, <=, >=, ^=, $=, *=, <>)
     */
    private static final String OPERATION = "o";
    
    public static String value(final String name) {
        return String.format("%s[%s][]", Param.VALUE, name);
    }
    
    public static String operation(final String name) {
        return String.format("%s[%s][]", Param.OPERATION, name);
    }
    
    public static String field() {
        return FIELD;
    }
}
