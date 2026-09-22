package net.jewelry.neoforge.compat;

public class CompatFeatures {
    /// The Curios integration (`compat/curios/**`) is excluded from the compile while `enable_curios=false`
    /// (see gradle.properties), so it is reached reflectively instead of by a direct reference. With the gate
    /// on — the normal case — the class is present and this resolves it exactly as a direct call would.
    private static final String CURIOS_COMPAT = "net.jewelry.neoforge.compat.curios.CuriosCompat";

    public static void init() {
        try {
            Class.forName(CURIOS_COMPAT).getMethod("init").invoke(null);
        } catch (ClassNotFoundException e) {
            // built without the Curios integration
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to initialize Curios compat", e);
        }
    }
}
