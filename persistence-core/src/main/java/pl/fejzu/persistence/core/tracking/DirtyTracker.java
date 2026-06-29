package pl.fejzu.persistence.core.tracking;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class DirtyTracker<ID> {

    private final Set<ID> dirtyIds = ConcurrentHashMap.newKeySet();

    public void markDirty(ID id) {
        dirtyIds.add(id);
    }

    public void markClean(ID id) {
        dirtyIds.remove(id);
    }

    public boolean isDirty(ID id) {
        return dirtyIds.contains(id);
    }

    public Set<ID> getDirtyIds() {
        return Collections.unmodifiableSet(dirtyIds);
    }

    public int dirtyCount() {
        return dirtyIds.size();
    }

    public void clear() {
        dirtyIds.clear();
    }
}
