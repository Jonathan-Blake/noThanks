package no.thanks.runners.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchSpaceTest {

    @Test
    void getAllProducesRightSize_SingleVar() {
        SearchSpace search = new SearchSpace.SearchSpaceBuilder().variable("A").from(5).increment(1).to(10).complete();
        search.getAllValues().forEach(System.out::println);
        assertEquals(6, search.getAllValues().size());
        assertEquals(5.0, search.getAllValues().stream().mapToDouble(variable -> variable.get("A")).min().getAsDouble());
        assertEquals(10.0, search.getAllValues().stream().mapToDouble(variable -> variable.get("A")).max().getAsDouble());
    }

    @Test
    void getAllProducesRightSize_MultiVar() {
        SearchSpace search = new SearchSpace.SearchSpaceBuilder().variable("A").from(5).increment(1).to(10)
                .nextVariable("B").from(0).increment(1).to(2)
                .nextVariable("C").from(1).increment(1).to(2)
                .complete();
        search.getAllValues().forEach(System.out::println);
        assertEquals(6 * 3 * 2, search.getAllValues().size());

    }
}