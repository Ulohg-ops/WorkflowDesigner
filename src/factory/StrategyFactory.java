package factory;

import controller.strategy.*;
import controller.CanvasController;
import enums.Mode;

public class StrategyFactory {

    public static CanvasMouseStrategy createStrategy(Mode mode, CanvasController controller) {
        return switch (mode) {
            case SELECT -> new SelectModeStrategy(controller);
            case RECT -> new RectModeStrategy(controller);
            case OVAL -> new OvalModeStrategy(controller);
            case ASSOCIATION -> new AssociationModeStrategy(controller);
            case GENERALIZATION -> new GeneralizationModeStrategy(controller);
            case COMPOSITION -> new CompositionModeStrategy(controller);
            default -> new NullStrategy();
        };
    }
}
