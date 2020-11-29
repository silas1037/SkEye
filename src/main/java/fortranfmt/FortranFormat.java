package fortranfmt;

import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.StringTokenizer;

public class FortranFormat {
    private static final HashMap<String, EditDescriptor> DESCRIPTOR_HASH = new HashMap<>(EditDescriptor.values().length);
    private final Options options = new Options();
    private final ArrayList<Unit> units;

    static {
        EditDescriptor[] values = EditDescriptor.values();
        for (EditDescriptor editDescriptor : values) {
            DESCRIPTOR_HASH.put(editDescriptor.getTag(), editDescriptor);
        }
    }

    /* access modifiers changed from: private */
    public enum EditDescriptor {
        CHARACTER("A", false) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) {
                String str;
                String str2 = null;
                boolean z = false;
                if (obj != null) {
                    str = obj instanceof String ? (String) obj : obj.toString();
                } else {
                    str = null;
                }
                if (obj != null) {
                    str2 = (unit.getLength() <= 0 || str.length() <= unit.getLength()) ? str : str.substring(0, unit.getLength());
                }
                int length = (str == null || unit.getLength() != 0) ? unit.getLength() : str.length();
                if (!options.isLeftAlignCharacters()) {
                    z = true;
                }
                return format(str2, length, z);
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                return str.trim();
            }
        },
        INTEGER("I", false) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) {
                String num = obj == null ? null : Integer.toString(((Integer) obj).intValue());
                if (num != null && unit.getDecimalLength() > 0) {
                    boolean z = num.charAt(0) == '-';
                    if (z) {
                        num = num.substring(1);
                    }
                    int decimalLength = unit.getDecimalLength() - num.length();
                    StringBuilder sb = new StringBuilder();
                    if (z) {
                        sb.append('-');
                    }
                    for (int i = 0; i < decimalLength; i++) {
                        sb.append('0');
                    }
                    sb.append(num);
                    num = sb.toString();
                }
                return format(num, unit.getLength(), true);
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                if (str.length() != 0) {
                    return Integer.valueOf(Integer.parseInt(str));
                }
                if (options.isReturnZeroForBlanks()) {
                    return 0;
                }
                return null;
            }
        },
        LOGICAL("L", false) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) {
                String str;
                String str2 = obj == null ? null : ((Boolean) obj).booleanValue() ? "T" : "F";
                if (str2 != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < unit.getLength() - 1; i++) {
                        sb.append(' ');
                    }
                    sb.append(str2);
                    str = sb.toString();
                } else {
                    str = str2;
                }
                return format(str, unit.getLength(), false);
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                boolean z = false;
                if (str.length() == 0) {
                    return null;
                }
                if (str.charAt(0) == 'T' || str.charAt(0) == 't') {
                    z = true;
                }
                return Boolean.valueOf(z);
            }
        },
        REAL_DECIMAL("F", false) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) {
                String str = null;
                if (obj != null) {
                    Double valueOf = Double.valueOf(obj instanceof Double ? ((Double) obj).doubleValue() : (double) ((Float) obj).floatValue());
                    boolean z = valueOf.doubleValue() < 0.0d;
                    if (z) {
                        valueOf = Double.valueOf(valueOf.doubleValue() * -1.0d);
                    }
                    StringBuilder sb = new StringBuilder();
                    int length = Integer.toString(valueOf.intValue()).length();
                    for (int i = 0; i < length; i++) {
                        sb.append('0');
                    }
                    sb.append('.');
                    for (int i2 = 0; i2 < unit.getDecimalLength(); i2++) {
                        sb.append('0');
                    }
                    str = (z ? '-' : "") + new DecimalFormat(sb.toString()).format(valueOf);
                }
                return format(str, unit.getLength(), true);
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                Double valueOf;
                double d = 1.0d;
                if (str.indexOf(69) != -1) {
                    String substring = str.substring(str.indexOf("E") + 1);
                    if (substring.startsWith("+")) {
                        substring = substring.substring(1);
                    }
                    str = str.substring(0, str.indexOf("E"));
                    if (str.length() == 0) {
                        valueOf = null;
                    } else {
                        double parseDouble = Double.parseDouble(str);
                        if (str.indexOf(46) == -1) {
                            d = Math.pow(10.0d, (double) unit.getDecimalLength());
                        }
                        valueOf = Double.valueOf((parseDouble / d) * Math.pow(10.0d, (double) Integer.parseInt(substring)));
                    }
                } else if (str.length() == 0) {
                    valueOf = null;
                } else {
                    double parseDouble2 = Double.parseDouble(str);
                    if (str.indexOf(46) == -1) {
                        d = Math.pow(10.0d, (double) unit.getDecimalLength());
                    }
                    valueOf = Double.valueOf(parseDouble2 / d);
                }
                if (valueOf == null && options.isReturnZeroForBlanks()) {
                    valueOf = Double.valueOf(0.0d);
                }
                if (valueOf == null) {
                    return null;
                }
                return Double.valueOf((!options.isReturnFloats() || str.length() == 0) ? valueOf.doubleValue() : (double) new Float(valueOf.doubleValue()).floatValue());
            }
        },
        REAL_DECIMAL_REDUNDANT("G", false) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) throws IOException {
                return REAL_DECIMAL.format(unit, obj, options);
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                return REAL_DECIMAL.parse(unit, str, options);
            }
        },
        REAL_DOUBLE("D", false) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) throws IOException {
                throw new IOException("Ouput for the D edit descriptor is not supported.");
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                throw new IOException("Input for the D edit descriptor is not supported.");
            }
        },
        REAL_ENGINEERING("EN", false) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) {
                String str = null;
                if (obj != null) {
                    Double valueOf = Double.valueOf(obj instanceof Double ? ((Double) obj).doubleValue() : (double) ((Float) obj).floatValue());
                    int i = 0;
                    boolean z = valueOf.doubleValue() < 0.0d;
                    if (z) {
                        valueOf = Double.valueOf(valueOf.doubleValue() * -1.0d);
                    }
                    while (valueOf.doubleValue() > 10.0d) {
                        valueOf = Double.valueOf(valueOf.doubleValue() / 10.0d);
                        i++;
                    }
                    while (valueOf.doubleValue() < 1.0d) {
                        valueOf = Double.valueOf(valueOf.doubleValue() * 10.0d);
                        i--;
                    }
                    while (i % 3 != 0) {
                        valueOf = Double.valueOf(valueOf.doubleValue() * 10.0d);
                        i--;
                    }
                    boolean z2 = i < 0;
                    if (z2) {
                        i *= -1;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("0.");
                    for (int i2 = 0; i2 < unit.getDecimalLength(); i2++) {
                        sb.append('0');
                    }
                    String str2 = (z ? "-" : "") + new DecimalFormat(sb.toString()).format(valueOf);
                    StringBuilder sb2 = new StringBuilder();
                    for (int i3 = 0; i3 < unit.getExponentLength(); i3++) {
                        sb2.append('0');
                    }
                    str = str2 + "E" + (z2 ? "-" : "+") + new DecimalFormat(sb2.toString()).format((long) i);
                }
                return format(str, unit.getLength(), true);
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                return REAL_DECIMAL.parse(unit, str, options);
            }
        },
        REAL_EXPONENT("E", false) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) {
                String str = null;
                if (obj != null) {
                    Double valueOf = Double.valueOf(obj instanceof Double ? ((Double) obj).doubleValue() : (double) ((Float) obj).floatValue());
                    int i = 0;
                    boolean z = valueOf.doubleValue() < 0.0d;
                    if (z) {
                        valueOf = Double.valueOf(valueOf.doubleValue() * -1.0d);
                    }
                    while (valueOf.doubleValue() > 1.0d) {
                        valueOf = Double.valueOf(valueOf.doubleValue() / 10.0d);
                        i++;
                    }
                    while (valueOf.doubleValue() < 0.1d) {
                        valueOf = Double.valueOf(valueOf.doubleValue() * 10.0d);
                        i--;
                    }
                    boolean z2 = i < 0;
                    if (z2) {
                        i *= -1;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("0.");
                    for (int i2 = 0; i2 < unit.getDecimalLength(); i2++) {
                        sb.append('0');
                    }
                    String str2 = (z ? '-' : "") + new DecimalFormat(sb.toString()).format(valueOf);
                    StringBuilder sb2 = new StringBuilder();
                    for (int i3 = 0; i3 < unit.getExponentLength(); i3++) {
                        sb2.append('0');
                    }
                    str = str2 + 'E' + (z2 ? '-' : '+') + new DecimalFormat(sb2.toString()).format((long) i);
                }
                return format(str, unit.getLength(), true);
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                return REAL_DECIMAL.parse(unit, str, options);
            }
        },
        REAL_SCIENTIFIC("ES", false) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) {
                String str = null;
                if (obj != null) {
                    Double valueOf = Double.valueOf(obj instanceof Double ? ((Double) obj).doubleValue() : (double) ((Float) obj).floatValue());
                    int i = 0;
                    boolean z = valueOf.doubleValue() < 0.0d;
                    if (z) {
                        valueOf = Double.valueOf(valueOf.doubleValue() * -1.0d);
                    }
                    while (valueOf.doubleValue() > 10.0d) {
                        valueOf = Double.valueOf(valueOf.doubleValue() / 10.0d);
                        i++;
                    }
                    while (valueOf.doubleValue() < 1.0d) {
                        valueOf = Double.valueOf(valueOf.doubleValue() * 10.0d);
                        i--;
                    }
                    boolean z2 = i < 0;
                    if (z2) {
                        i *= -1;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("0.");
                    for (int i2 = 0; i2 < unit.getDecimalLength(); i2++) {
                        sb.append('0');
                    }
                    String str2 = (z ? "-" : "") + new DecimalFormat(sb.toString()).format(valueOf);
                    StringBuilder sb2 = new StringBuilder();
                    for (int i3 = 0; i3 < unit.getExponentLength(); i3++) {
                        sb2.append('0');
                    }
                    str = str2 + "E" + (z2 ? "-" : "+") + new DecimalFormat(sb2.toString()).format((long) i);
                }
                return format(str, unit.getLength(), true);
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                return REAL_DECIMAL.parse(unit, str, options);
            }
        },
        BLANK_CONTROL_REMOVE("BN", true) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) throws IOException {
                throw new IOException("Ouput for the BN edit descriptor is not supported.");
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                return null;
            }
        },
        BLANK_CONTROL_ZEROS("BZ", true) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) throws IOException {
                throw new IOException("Ouput for the BZ edit descriptor is not supported.");
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                return null;
            }
        },
        FORMAT_SCANNING_CONTROL(":", true) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) {
                return "";
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                return null;
            }
        },
        POITIONING_HORIZONTAL("X", true) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < unit.getLength(); i++) {
                    sb.append(options.getPositioningChar());
                }
                return sb.toString();
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                return null;
            }
        },
        POSITIONING_TAB("T", true) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) {
                return null;
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                throw new IOException("Input for the T edit descriptor is not supported.");
            }
        },
        POSITIONING_TAB_LEFT("TL", true) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) {
                return null;
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                throw new IOException("Input for the TL edit descriptor is not supported.");
            }
        },
        POSITIONING_TAB_RIGHT("TR", true) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) {
                return null;
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                throw new IOException("Input for the TR edit descriptor is not supported.");
            }
        },
        POSITIONING_VERTICAL("/", true) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) {
                return "\n";
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                return null;
            }
        },
        SIGN_CONTROL_COMPILER("S", true) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) throws IOException {
                throw new IOException("Ouput for the S edit descriptor is not supported.");
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                return null;
            }
        },
        SIGN_CONTROL_POSITIVE_ALWAYS("SP", true) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) throws IOException {
                throw new IOException("Ouput for the SP edit descriptor is not supported.");
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                return null;
            }
        },
        SIGN_CONTROL_POSITIVE_NEVER("SS", true) {
            @Override // fortranfmt.FortranFormat.EditDescriptor
            public String format(Unit unit, Object obj, Options options) throws IOException {
                throw new IOException("Ouput for the SS edit descriptor is not supported.");
            }

            @Override // fortranfmt.FortranFormat.EditDescriptor
            public Object parse(Unit unit, String str, Options options) throws IOException {
                return null;
            }
        };
        
        private final boolean nonRepeatable;
        private final String tag;

        public abstract String format(Unit unit, Object obj, Options options) throws IOException;

        public abstract Object parse(Unit unit, String str, Options options) throws IOException;

        private EditDescriptor(String str, boolean z) {
            this.tag = str;
            this.nonRepeatable = z;
        }

        public String getTag() {
            return this.tag;
        }

        /* access modifiers changed from: protected */
        public String format(String str, int i, boolean z) {
            int i2 = 0;
            StringBuilder sb = new StringBuilder();
            if (str == null) {
                while (i2 < i) {
                    sb.append(' ');
                    i2++;
                }
            } else if (i == -1) {
                sb.append(str);
            } else if (str.length() > i) {
                while (i2 < i) {
                    sb.append('*');
                    i2++;
                }
            } else {
                int length = i - str.length();
                if (z) {
                    for (int i3 = 0; i3 < length; i3++) {
                        sb.append(' ');
                    }
                }
                sb.append(str);
                if (!z) {
                    while (i2 < length) {
                        sb.append(' ');
                        i2++;
                    }
                }
            }
            return sb.toString();
        }

        public boolean isNonRepeatable() {
            return this.nonRepeatable;
        }
    }

    protected static class SpecificationStringInterpreter {
        private final String input;
        private final String multipliedOut;
        private final String original;
        private final String withCommas;
        private final String withoutParenthesis;

        public SpecificationStringInterpreter(String str) throws ParseException {
            if (str == null) {
                throw new NullPointerException("The format specification string may not be null.");
            }
            this.original = str;
            int indexOf = str.indexOf(40);
            if (indexOf == -1) {
                throw new ParseException("Fortran format specification strings must begin with an open parenthesis '(' and end with a close parenthesis ')'. Blank spaces are tolerated before an open parenthesis and any characters are tolerated after a close parenthesis. No characters outside of the root parenthesis affect the format specification.", 0);
            }
            int findClosingParenthesis = findClosingParenthesis(str, indexOf);
            if (str.substring(0, indexOf).replaceAll(" ", "").length() != 0) {
                throw new ParseException("Only spaces may precede the root parenthesis.", 0);
            }
            this.input = str.substring(indexOf + 1, findClosingParenthesis).replaceAll(" ", "");
            this.withCommas = checkCommas(this.input);
            this.multipliedOut = multiplyOut(this.withCommas);
            this.withoutParenthesis = removeParenthesis(this.multipliedOut);
        }

        /* access modifiers changed from: protected */
        public final String checkCommas(String str) {
            StringBuilder sb = new StringBuilder();
            boolean z = false;
            boolean z2 = true;
            boolean z3 = false;
            for (int i = 0; i < str.length(); i++) {
                char charAt = str.charAt(i);
                if (charAt == '(' || charAt == ')' || charAt == ',') {
                    sb.append(charAt);
                } else if (charAt == EditDescriptor.POITIONING_HORIZONTAL.getTag().charAt(0)) {
                    sb.append(charAt);
                    if (!(i == str.length() - 1 || str.charAt(i + 1) == ')' || str.charAt(i + 1) == ',')) {
                        sb.append(',');
                        z2 = true;
                    }
                } else if (charAt == '.' || Character.isDigit(charAt)) {
                    sb.append(charAt);
                    if (i == 0 || str.charAt(i - 1) != ',') {
                        z2 = false;
                    } else {
                        z = false;
                        z2 = false;
                    }
                } else {
                    if (z && !z2 && i != 0 && sb.charAt(sb.length() - 1) != ',' && (charAt != EditDescriptor.REAL_EXPONENT.getTag().charAt(0) || !z3)) {
                        sb.append(',');
                        z3 = false;
                    }
                    if (charAt == EditDescriptor.REAL_EXPONENT.getTag().charAt(0)) {
                        z3 = true;
                    }
                    sb.append(charAt);
                    if (charAt == '/') {
                        sb.append(',');
                    }
                    z = true;
                    z2 = true;
                }
            }
            return sb.toString();
        }

        /* access modifiers changed from: protected */
        public final String multiplyOut(String str) throws ParseException {
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            StringBuilder sb3 = new StringBuilder();
            int i = 0;
            int i2 = 1;
            while (i < str.length()) {
                char charAt = str.charAt(i);
                if (charAt == '(') {
                    if (sb3.length() > 0) {
                        i2 = Integer.parseInt(sb3.toString());
                    }
                    if (sb2.length() > 0) {
                        for (int i3 = 0; i3 < i2; i3++) {
                            sb.append(sb2.toString());
                        }
                        sb2.delete(0, sb2.length());
                        sb3.delete(0, sb3.length());
                    }
                    int findClosingParenthesis = findClosingParenthesis(str, i);
                    String multiplyOut = multiplyOut(str.substring(i + 1, findClosingParenthesis));
                    for (int i4 = 0; i4 < i2; i4++) {
                        sb.append('(');
                        sb.append(multiplyOut);
                        sb.append(')');
                    }
                    sb2.delete(0, sb2.length());
                    sb3.delete(0, sb3.length());
                    i = findClosingParenthesis;
                    i2 = 1;
                } else if (charAt == ',') {
                    for (int i5 = 0; i5 < i2; i5++) {
                        sb.append(sb2.toString());
                        sb.append(',');
                    }
                    sb2.delete(0, sb2.length());
                    i2 = 1;
                } else if (!Character.isDigit(charAt) || sb2.length() != 0) {
                    if (charAt == EditDescriptor.POITIONING_HORIZONTAL.getTag().charAt(0)) {
                        sb.append((CharSequence) sb3);
                        sb3.delete(0, sb3.length());
                        sb3.append('1');
                    }
                    if (sb3.length() > 0) {
                        i2 = Integer.parseInt(sb3.toString());
                        sb3.delete(0, sb3.length());
                    }
                    sb2.append(charAt);
                } else {
                    sb3.append(charAt);
                }
                i++;
            }
            if (sb2.length() > 0) {
                for (int i6 = 0; i6 < i2; i6++) {
                    sb.append(sb2.toString());
                    if (i6 != i2 - 1) {
                        sb.append(',');
                    }
                }
            }
            return sb.toString();
        }

        /* access modifiers changed from: protected */
        public final String removeParenthesis(String str) throws ParseException {
            StringBuilder sb = new StringBuilder();
            boolean z = false;
            for (int i = 0; i < str.length(); i++) {
                char charAt = str.charAt(i);
                if (charAt == '(' || charAt == ')') {
                    z = true;
                } else {
                    if (!(!z || sb.length() == 0 || sb.charAt(sb.length() - 1) == ',')) {
                        sb.append(',');
                    }
                    if (!(charAt == ',' && sb.charAt(sb.length() - 1) == ',')) {
                        sb.append(charAt);
                    }
                    z = false;
                }
            }
            return sb.toString();
        }

        private final int findClosingParenthesis(String str, int i) throws ParseException {
            Stack stack = new Stack();
            for (int i2 = i + 1; i2 < str.length(); i2++) {
                switch (str.charAt(i2)) {
                    case '(':
                        stack.push(Integer.valueOf(i2));
                        break;
                    case ')':
                        if (!stack.isEmpty()) {
                            stack.pop();
                            break;
                        } else {
                            return i2;
                        }
                }
            }
            throw new ParseException("Missing a close parenthesis.", i);
        }

        public final ArrayList<Unit> getUnits() throws ParseException {
            int i;
            StringBuilder sb;
            StringTokenizer stringTokenizer = new StringTokenizer(getCompletedInterpretation(), ",");
            ArrayList<Unit> arrayList = new ArrayList<>(stringTokenizer.countTokens());
            while (stringTokenizer.hasMoreTokens()) {
                String nextToken = stringTokenizer.nextToken();
                boolean z = false;
                boolean z2 = false;
                boolean z3 = false;
                StringBuilder sb2 = new StringBuilder();
                StringBuilder sb3 = new StringBuilder();
                StringBuilder sb4 = new StringBuilder();
                StringBuilder sb5 = new StringBuilder();
                StringBuilder sb6 = new StringBuilder();
                for (int i2 = 0; i2 < nextToken.length(); i2++) {
                    if (nextToken.charAt(i2) == '.') {
                        z2 = true;
                    } else if (z && nextToken.charAt(i2) == 'E') {
                        z3 = true;
                    } else if (Character.isLetter(nextToken.charAt(i2)) || nextToken.charAt(i2) == '/') {
                        sb3.append(nextToken.charAt(i2));
                        z = true;
                    } else if (z3) {
                        sb5.append(nextToken.charAt(i2));
                    } else if (z2) {
                        sb4.append(nextToken.charAt(i2));
                    } else if (z) {
                        sb6.append(nextToken.charAt(i2));
                    } else {
                        sb2.append(nextToken.charAt(i2));
                    }
                }
                int parseInt = sb2.length() == 0 ? 1 : Integer.parseInt(sb2.toString());
                if (sb3.toString().equals(EditDescriptor.POITIONING_HORIZONTAL.getTag())) {
                    i = 1;
                    sb = sb2;
                } else {
                    i = parseInt;
                    sb = sb6;
                }
                if (sb3.toString().equals(EditDescriptor.REAL_EXPONENT.getTag()) && sb5.length() == 0) {
                    sb5.append('2');
                }
                int i3 = 0;
                while (true) {
                    if (i3 < i) {
                        if (!FortranFormat.DESCRIPTOR_HASH.containsKey(sb3.toString())) {
                            throw new ParseException("Unsupported Edit Descriptor: " + sb3.toString(), this.original.indexOf(sb3.toString()));
                        }
                        Unit unit = new Unit((EditDescriptor) FortranFormat.DESCRIPTOR_HASH.get(sb3.toString()), sb.length() == 0 ? 0 : Integer.parseInt(sb.toString()));
                        if (sb4.length() != 0) {
                            unit.decimalLength = Integer.parseInt(sb4.toString());
                        }
                        if (sb5.length() != 0) {
                            unit.exponentLength = Integer.parseInt(sb5.toString());
                        }
                        arrayList.add(unit);
                        i3++;
                    }
                }
            }
            return arrayList;
        }

        public String getCompletedInterpretation() {
            return this.withoutParenthesis;
        }
    }

    /* access modifiers changed from: private */
    public static class Unit {
        private int decimalLength;
        private int exponentLength;
        private final int length;
        private final EditDescriptor type;

        public Unit(EditDescriptor editDescriptor, int i) {
            this.type = editDescriptor;
            this.length = i;
        }

        public String toString() {
            return this.type.getTag() + this.length + (this.decimalLength > 0 ? "." + this.decimalLength : "") + (this.exponentLength > 0 ? "E" + this.exponentLength : "") + " ";
        }

        public EditDescriptor getType() {
            return this.type;
        }

        public int getLength() {
            return this.length;
        }

        public int getDecimalLength() {
            return this.decimalLength;
        }

        public int getExponentLength() {
            return this.exponentLength;
        }
    }

    public static class Options {
        private boolean addReturn = false;
        private boolean leftAlignCharacters = false;
        private char positioningChar = ' ';
        private boolean returnFloats = false;
        private boolean returnZeroForBlanks = false;

        public char getPositioningChar() {
            return this.positioningChar;
        }

        public void setPositioningChar(char c) {
            this.positioningChar = c;
        }

        public void setAddReturn(boolean z) {
            this.addReturn = z;
        }

        public boolean isAddReturn() {
            return this.addReturn;
        }

        public boolean isReturnFloats() {
            return this.returnFloats;
        }

        public void setReturnFloats(boolean z) {
            this.returnFloats = z;
        }

        public boolean isReturnZeroForBlanks() {
            return this.returnZeroForBlanks;
        }

        public void setReturnZeroForBlanks(boolean z) {
            this.returnZeroForBlanks = z;
        }

        public boolean isLeftAlignCharacters() {
            return this.leftAlignCharacters;
        }

        public void setLeftAlignCharacters(boolean z) {
            this.leftAlignCharacters = z;
        }
    }

    public static ArrayList<Object> read(String str, String str2) throws ParseException, IOException {
        return new FortranFormat(str2).parse(str);
    }

    public static String write(ArrayList<Object> arrayList, String str) throws ParseException, IOException {
        return new FortranFormat(str).format(arrayList);
    }

    public FortranFormat(String str) throws ParseException {
        this.units = new SpecificationStringInterpreter(str).getUnits();
    }

    public ArrayList<Object> parse(String str) throws IOException {
        StringTokenizer stringTokenizer = new StringTokenizer(str, "\n");
        ArrayList<Object> arrayList = new ArrayList<>(this.units.size());
        StringReader stringReader = new StringReader(stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "");
        Iterator<Unit> it = this.units.iterator();
        while (it.hasNext()) {
            Unit next = it.next();
            char[] cArr = new char[next.length];
            stringReader.read(cArr, 0, next.length);
            StringBuilder sb = new StringBuilder(cArr.length);
            for (char c : cArr) {
                if ((next.type == EditDescriptor.CHARACTER || c != ' ') && c != 0) {
                    sb.append(c);
                }
            }
            String sb2 = sb.toString();
            if (next.type == EditDescriptor.FORMAT_SCANNING_CONTROL) {
                break;
            }
            if (next.type == EditDescriptor.POSITIONING_VERTICAL) {
                stringReader = new StringReader(stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : "");
            } else if (!next.type.isNonRepeatable()) {
                arrayList.add(next.type.parse(next, sb2, this.options));
            }
            stringReader = stringReader;
        }
        return arrayList;
    }

    public String format(Object obj) throws IOException {
        ArrayList<Object> arrayList = new ArrayList<>(1);
        arrayList.add(obj);
        return format(arrayList);
    }

    public String format(ArrayList<Object> arrayList) throws IOException {
        int length;
        int i = 0;
        StringBuilder sb = new StringBuilder();
        int i2 = -1;
        StringBuilder sb2 = null;
        for (int i3 = 0; i3 < arrayList.size() + i; i3++) {
            Unit unit = this.units.get(i3);
            Object obj = arrayList.get(i3 - i);
            if (unit.type == EditDescriptor.POSITIONING_TAB || unit.type == EditDescriptor.POSITIONING_TAB_LEFT || unit.type == EditDescriptor.POSITIONING_TAB_RIGHT) {
                if (sb2 == null) {
                    sb2 = sb;
                } else {
                    while ((i2 - 1) + sb.length() > sb2.length()) {
                        sb2.append(' ');
                    }
                    sb2.replace(i2 - 1, (i2 - 1) + sb.length(), sb.toString());
                }
                switch (unit.type) {
                    case POSITIONING_TAB:
                        length = unit.length;
                        break;
                    case POSITIONING_TAB_LEFT:
                        length = i2 - (unit.length - sb.length());
                        break;
                    case POSITIONING_TAB_RIGHT:
                        length = sb.length() + unit.length + i2;
                        break;
                    default:
                        length = i2;
                        break;
                }
                sb = new StringBuilder();
                i2 = length;
            } else {
                sb.append(unit.type.format(unit, obj, this.options));
            }
            if (unit.type.isNonRepeatable()) {
                i++;
            } else {
                i = i;
            }
        }
        if (sb2 != null) {
            while ((i2 - 1) + sb.length() > sb2.length()) {
                sb2.append(' ');
            }
            sb2.replace(i2 - 1, (i2 - 1) + sb.length(), sb.toString());
        } else {
            sb2 = sb;
        }
        if (this.options.isAddReturn()) {
            sb2.append("\n");
        }
        return sb2.toString();
    }

    public Options getOptions() {
        return this.options;
    }
}
