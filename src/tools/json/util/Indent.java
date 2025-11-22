package tools.json.util;

import java.util.function.IntConsumer;

public class Indent implements IntConsumer {

  public Indent(Appendable a, int t) {
    tab = t;
    out = a;
  }

  Appendable out;
  int tab;
  int gap = 0;

  @Override
  public void accept(int v) {
    var n = v < 0 ? (gap -= tab)
          : v > 0 ? (gap += tab)
          : /*v=0*/ (gap);
    try {
      out.append('\n');
      while (n-- > 0) out.append(' ');
    }
    catch (Exception e) { uncheck(e); }
  }

  public static IntConsumer ed(Appendable a, int...t) {
    return t.length > 0 ? new Indent(a,t[0]) : _->{};
  }

  @SuppressWarnings("unchecked")
  static <T extends Throwable> void uncheck(Throwable t) throws T { throw (T)t; }
}