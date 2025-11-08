package tools.json.util;

import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;

public class JsonEncoder {

  public void encode(Object in, Appendable out) {
    encode(in, out, _->{});
  }

  public void encode(Object in, Appendable out, IntConsumer dent) {
    try { d = dent; o=out; value(in); }
    catch (Exception e) { uncheck(e); }
  }

  Appendable o;
  IntConsumer d; // -> accept( -1 = decrease+print, 0 = print, 1 = increase+print )

  void value(Object v) throws Exception {
    switch (v) {
      case Map<?,?> m -> object(m);
      case List<?> l -> array(l);
      case String s -> string(s);
      case Number n -> o.append(n.toString());
      case Boolean b -> o.append(b.toString());
      case null -> o.append("null");
      default -> other(v,false);
    }
  }

  void object(Map<?,?> m) throws Exception {
    var n = m.size();
    o.append('{'); d.accept(1);
    for (var e:m.entrySet()) {
      key(e.getKey());
      o.append(": ");
      value(e.getValue());
      if (--n > 0) {
        o.append(", "); d.accept(0);
      }
    }
    d.accept(-1); o.append('}');
  }
  void array(List<?> a) throws Exception {
    var n = a.size();
    o.append('['); d.accept(1);
    for (var e:a) {
      value(e);
      if (--n > 0) {
        o.append(", "); d.accept(0);
      }
    }
    d.accept(-1); o.append(']');
  }

  void string(String s) throws Exception {
    o.append('"');
    for (var c:s.toCharArray()) switch(c) {
      case '\\' -> o.append("\\\\");
      case '\t' -> o.append("\\t");
      case '\b' -> o.append("\\b");
      case '\n' -> o.append("\\n");
      case '\r' -> o.append("\\r");
      case '\f' -> o.append("\\f");
      case '\"' -> o.append("\\\"");
      default -> o.append(c);
    }
    o.append('"');
  }

  void key(Object k) throws Exception {
    if (k instanceof String s) string(s); else other(k,true);
  }

  void other(Object v, boolean q) throws Exception {
    if (q) o.append('"');
    o.append(v.getClass().getCanonicalName()).append('@').append(Integer.toHexString(v.hashCode()));
    if (q) o.append('"');
  }

  @SuppressWarnings("unchecked")
  static <T extends Throwable> void uncheck(Throwable t) throws T { throw (T)t; }
}