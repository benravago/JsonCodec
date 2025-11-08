package tools.json.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;
import java.util.LinkedHashMap;

public class JsonDecoder {

  public Object decode(Supplier<char[]> in) {
    s = in.get();
    p = 0;
    ln = lc = 0;
    return value();
  }

  char[] s;
  int p;
  int ln, lc;

  Object value() {
    return switch(ws()) {
      case '{' -> object();  // object
      case '[' -> array();   // array
      case '"' -> string();  // string
      default  -> number();  // number
      case 't' -> true_();   // "true"
      case 'f' -> false_();  // "false"
      case 'n' -> null_();   // "null"
      case  0  -> fail();    // EOD
    };
  }

  char ws() {
    for (;;) {
      var c = s[p];
      if (c > ' ') return c;
      if (c == '\n') {
        ln++;
        lc = p;
      }
      p++;
    }
  }

  Map<String,?> object() {
    var map = new LinkedHashMap<String,Object>();
    p++; // s[p] -> {
    var c = ws();
    while (c != '}') {
      if (c != '"') fail();
      map.put(name(),value());
      c = comma('}');
    }
    p++; // s[p] -> }
    return map;
  }

  String name() {
    var key = string();
    var c = ws();
    if (c != ':') fail();
    p++; // s[p] -> ':'
    return key;
  }

  List<?> array() {
    var list = new ArrayList<Object>();
    p++; // s[p] -> [
    var c = ws();
    while (c != ']') {
      list.add(value());
      c = comma(']');
    }
    p++; // s[p] -> ]
    return list;
  }

  char comma(char d) {
    var c = ws();
    if (c == ',') {
      p++; // s[p] -> ,
      c = ws();
      if (c == d) fail();
    } // s[p] -> lead char
    return c;
  }

  String string() {
    var q = p; // s[p] -> "
    for (;;) {
      var c = s[++p];
      if (c == '"') break;
      if (c < ' ' || '~' < c) fail();
      if (c == '\\') {
        switch (s[++p]) {
          case '"', '\\', '/', 'b', 'n', 'r', 't' -> {}
          case 'u' -> hex(4);
          default -> fail();
        }
      }
    }
    var b = p++; // s[p] -> "
    return new String(s,(q+1),(b-1)-q);
  }

  void hex(int n) {
    while (n-- > 0) {
      var c = s[++p];
      if (c < '0' || ('9' < c && c < 'A') || ('F' < c && c < 'a') || 'f' < c) fail();
    }
  }

  Number number() {
    var q = p;
    var c = s[p];
    if (c == '-') {
      p++; // s[p] -> -
    }
    c = digits();
    if (c == '.') {
      p++; // s[p] -> .
      c = digits();
    }
    if (c == 'E' || c == 'e') {
      c = s[p++]; // s[p] -> Ee
      if (c == '-' || c == '+') {
        p++; // s[p] -> -+
      }
      c = digits();
    }
    p--; // s[p] -> past last digit
    return new BigDecimal(s,q,p-q);
  }

  char digits() {
    for (;;) {
      var c = s[p++];
      if (c < '0' || '9' < c) return c;
    }
  }

  Boolean true_() {
    return (s[++p]=='r' && s[++p]=='u' && s[++p]=='e' && ++p > 0) ? Boolean.TRUE : fail();
  }
  Boolean false_() {
    return (s[++p]=='a' && s[++p]=='l' && s[++p]=='s' && s[++p]=='e' && ++p > 0) ? Boolean.FALSE : fail();
  }
  Object null_() {
    return (s[++p]=='u' && s[++p]=='l' && s[++p]=='l' && ++p > 0) ? null : fail();
  }

  <T> T fail() { throw new MatchException("'"+s[p]+"' at line:"+(ln+1)+" col:"+(p-lc), null); }
}