package binding;

/**
 * User: igor.kostromin
 * Date: 13.07.13
 * Time: 17:12
 */
public class BindingExpressionParser {
    public static class BindingProto {
        public String path;
        public BindingMode mode;
        public UpdateSourceTrigger updateSourceTrigger;

        public BindingProto(String path, BindingMode mode, UpdateSourceTrigger updateSourceTrigger) {
            this.path = path;
            this.mode = mode;
            this.updateSourceTrigger = updateSourceTrigger;
        }
    }

    /**
     * Parses expression in syntax {Path=propertyName, Mode=Default, UpdateSourceTrigger=LostFocus}
     * Mode and UpdateSourceTrigger parts are not required, but Path is.
     * @param expr
     * @return
     */
    public static BindingProto parse( String expr) {
        if (null == expr || expr.length() == 0) throw new IllegalArgumentException("expr is null or empty");
        if (!expr.startsWith("{") || !expr.endsWith("}"))
            throw new RuntimeException("Invalid syntax");
        String body = expr.substring(1, expr.length() - 1);
        String[] parts = body.split(",");
        if (parts.length > 3) throw new RuntimeException("Invalid syntax");
        String path = null;
        BindingMode mode = null;
        UpdateSourceTrigger updateSourceTrigger = null;
        for (String part : parts) {
            String[] leftAndRight = part.trim().split("=");
            if (leftAndRight.length != 2) throw new RuntimeException("Invalid syntax");
            String lowercasedKey = leftAndRight[0].toLowerCase();
            if ("path".equals(lowercasedKey)) {
                if (null != path) throw new RuntimeException("Invalid syntax: path redefinition");
                path = leftAndRight[1];
            } else if ("mode".equals(lowercasedKey)) {
                if (null != mode) throw new RuntimeException("Invalid syntax: mode redefinition");
                String s = leftAndRight[1].toLowerCase();
                if ("onetime".equals(s)) mode = BindingMode.OneTime;
                else if ("oneway".equals(s)) mode = BindingMode.OneWay;
                else if ("onewaytosource".equals(s)) mode = BindingMode.OneWayToSource;
                else if ("twoway".equals(s)) mode = BindingMode.TwoWay;
                else if ("default".equals(s)) mode = BindingMode.Default;
                else throw new RuntimeException("Invalid syntax: unknown mode");
            } else if ("updatesourcetrigger".equals(lowercasedKey)) {
                if (null != updateSourceTrigger)
                    throw new RuntimeException("Invalid syntax: update source trigger redefinition");
                String s = leftAndRight[1].toLowerCase();
                if ("lostfocus".equals(s)) updateSourceTrigger = UpdateSourceTrigger.LostFocus;
                else if ("propertychanged".equals(s)) updateSourceTrigger = UpdateSourceTrigger.PropertyChanged;
                else if ("explicit".equals(s)) updateSourceTrigger = UpdateSourceTrigger.Explicit;
                else if ("default".equals(s)) updateSourceTrigger = UpdateSourceTrigger.Default;
                else throw new RuntimeException("Invalid syntax: unknown update source trigger mode");
            }
        }
        if (null == path) throw new RuntimeException("Invalid syntax: path should be defined");
        if (null == mode) mode = BindingMode.Default;
        if (null == updateSourceTrigger ) updateSourceTrigger = UpdateSourceTrigger.Default;
        return new BindingProto(path, mode, updateSourceTrigger);
    }
}
