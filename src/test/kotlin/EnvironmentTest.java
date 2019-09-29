import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class EnvironmentTest {

    @Test
    void shouldHaveEnvironmentAvailable() {
        String value = System.getenv("TESTVAR");
        assertEquals("TESTVALUE", value);
    }
}
