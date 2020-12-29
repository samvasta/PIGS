package com.samvasta.imageGenerator.common.interfaces;

import com.samvasta.imageGenerator.common.models.IniSchemaOption;

import java.util.ArrayList;
import java.util.List;

/**
 * <para>
 * Convenience class for lightweight generators which have no settings.
 * </para>
 * <br/>
 * <br/>
 * <para>
 * Also configures a list of snapshot listeners and provides a convenience
 * {@code takeSnapshot()} method.
 * </para>
 * <br/>
 * <br/>
 * <para>
 * Default settings:
 * <ul>
 *     <li>
 *          getIniSettings          => (empty list)
 *     </li>
 *     <li>
 *          isOnByDefault           => true
 *     </li>
 *     <li>
 *          isMultiThreadEnabled    => true
 *     </li>
 * </ul>
 *
 * </para>
 */
public abstract class SimpleGenerator implements IGenerator {
    protected List<ISnapshotListener> snapshotListeners;

    public SimpleGenerator() {
        snapshotListeners = new ArrayList<>();
    }

    @Override
    public boolean isOnByDefault() {
        return true;
    }

    @Override
    public List<IniSchemaOption<?>> getIniSettings() {
        return new ArrayList<>();
    }

    @Override
    public boolean isMultiThreadEnabled() {
        return true;
    }

    @Override
    public void addSnapshotListener(ISnapshotListener listener) {
        snapshotListeners.add(listener);
    }

    @Override
    public void removeSnapshotListener(ISnapshotListener listener) {
        snapshotListeners.remove(listener);
    }

    public void takeSnapshot(){
        for(ISnapshotListener listener : snapshotListeners){
            listener.takeSnapshot();
        }
    }
}
