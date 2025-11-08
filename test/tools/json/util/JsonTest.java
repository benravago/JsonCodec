package tools.json.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JsonTest {

  @Test
  void test() {
    var expected = resource("schema.json");
    var dict = Json.loads(expected);
    var cs = Json.dumps(dict, 2);
    var actual = cs.toString().replace(" \n", "\n");
    assertEquals(expected, actual);
  }

  String resource(String name) {
    try { return new String(getClass().getResourceAsStream(name).readAllBytes()).strip(); }
    catch (Exception e) { return name+": "+e.toString(); }
  }
}