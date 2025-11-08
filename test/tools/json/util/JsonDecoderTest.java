package tools.json.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JsonDecoderTest {

  @BeforeAll
  void setup() {
    rdr = new JsonDecoder();
  }

  JsonDecoder rdr;

  @ParameterizedTest
  @ValueSource(strings = {
    "example-1",
    "example-2",
    "example-3",
    "example-4",
    "example-5",
    "example-6",
    "example-7",
    "example-8"
  })
  void test(String name) {
    var input = resource(name+".json");
    var actual = rdr.decode(input::toCharArray);
    assertEquals(trim(input), text(actual));
  }

  String text(Object o) {
    return o.toString().strip();
  }

  String trim(String s) {
    return s
      .replaceAll("\\s+"," ")
      .replace("[ ","[").replace(" ]","]")
      .replace("{ ","{").replace(" }","}")
      .replace("\": ","=")
      .replace("\"","");
  }

  String resource(String name) {
    try { return new String(getClass().getResourceAsStream(name).readAllBytes()).strip(); }
    catch (Exception e) { return name+": "+e.toString(); }
  }
}