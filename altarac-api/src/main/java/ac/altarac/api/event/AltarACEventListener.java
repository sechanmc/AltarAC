package ac.altarac.api.event;

@FunctionalInterface
public interface AltarACEventListener<T extends AltarACEvent<?>> {
    void handle(T event) throws Exception;
}