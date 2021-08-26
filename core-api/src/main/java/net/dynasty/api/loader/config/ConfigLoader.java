package net.dynasty.api.loader.config;

import lombok.Getter;
import net.dynasty.api.Dynasty;
import net.dynasty.api.loader.LoadObject;
import net.dynasty.api.loader.Loader;

@Getter
public abstract class ConfigLoader extends Loader {

    private AbstractJsonConfig config;

    public ConfigLoader(AbstractJsonConfig config) {
        if (config != null)
            this.config = config;
        Dynasty.LOADERS.add(this);
    }

    @Override
    public <T extends LoadObject> void load(LoadInfo<T> loadInfo) {
        T info = config.get(loadInfo.getName(), loadInfo.getType());
        if (info == null)
            info = save(loadInfo);
        loadInfo.setObject(info);
        addInfo(loadInfo);
        onLoadComplete();
    }

    @Override
    public <T extends LoadObject> T save(LoadInfo<T> loadInfo) {
        T t = loadInfo.getObject();
        config.set(loadInfo.getName(), t);
        config.saveConfig();
        return t;
    }

}
