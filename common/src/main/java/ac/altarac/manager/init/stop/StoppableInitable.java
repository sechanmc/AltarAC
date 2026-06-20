package ac.altarac.manager.init.stop;

import ac.altarac.manager.init.Initable;

public interface StoppableInitable extends Initable {
    void stop();
}
