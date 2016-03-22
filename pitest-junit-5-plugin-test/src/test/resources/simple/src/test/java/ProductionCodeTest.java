import org.junit.gen5.api.Test;

import static org.junit.gen5.api.Assertions.assertTrue;

/**
 *
 *
 * @author Stefan Pennndorf
 */
class ProductionCodeTest {

    @Test
    void executes() {
        final ProductionCode p = new ProductionCode();
        p.calculateOutput();

        assertTrue(p.ran);
    }

}