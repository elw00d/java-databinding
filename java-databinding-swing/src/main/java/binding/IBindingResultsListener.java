package binding;

/**
 * @author igor.kostromin
 *         17.07.13 15:59
 */
public interface IBindingResultsListener {
    void onBinding(String sourceProperty, BindingResult result);
}
