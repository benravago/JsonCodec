package tools.json.util;

import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Json {

  static Object loads(CharSequence cs) {
    var t = cs instanceof String s ? s : cs.toString();
    return new JsonDecoder().decode(t::toCharArray);
  }

  static CharSequence dumps(Object o, int...t) {
    var b = new StringBuilder();
    new JsonEncoder().encode(o, b, Indent.ed(b,t));
    return b;
  }

  static Object load(Reader r) {
    return new JsonDecoder().decode(() -> readChars(r));
  }

  static void dump(Object o, Writer w, int...t) {
    new JsonEncoder().encode(o, w, Indent.ed(w,t));
  }

  static JsonEncoder encoder() { return null; }
  static JsonDecoder decoder() { return null; }

  @SuppressWarnings("unchecked")
  static Map<String,?> map(Object x) { return x instanceof Map m ? m : null; }
  static List<?>       list(Object x) { return x instanceof List l ? l : null; }
  static String        str(Object x) { return x instanceof String s? s : null; }
  static Number        num(Object x) { return x instanceof Number n ? n : null; }
  static Boolean       bool(Object x) { return x instanceof Boolean b ? b : null; }

  static int num(Object x, int y) { return x instanceof Number n ? n.intValue() : y; }
  static long num(Object x, long y) { return x instanceof Number n ? n.longValue() : y; }
  static float num(Object x, float y) { return x instanceof Number n ? n.floatValue() : y; }
  static double num(Object x, double y) { return x instanceof Number n ? n.doubleValue() : y; }

  static boolean is(Object x) { // like javascript 'truthy'
    return switch(x) {
      case null -> false;
      case Boolean b ->  b;
      case Number n -> n.doubleValue() != 0.0;
      case String s -> !s.isEmpty();
      case Collection<?> c -> !c.isEmpty();
      case Map<?,?> m -> !m.isEmpty();
      default -> true;
    };
  }

  static char[] readChars(Reader r) {
    try { return r.readAllAsString().toCharArray(); }
    catch (Exception e) { return uncheck(e); }
  }
  static void writeChars(Writer w, char[] cs) {
    try { w.write(cs); }
    catch (Exception e) { uncheck(e); }
  }

  @SuppressWarnings("unchecked")
  static <T extends Throwable, V> V uncheck(Throwable t) throws T { throw (T)t; }
}