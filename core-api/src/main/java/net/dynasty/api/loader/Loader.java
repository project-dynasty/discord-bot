package net.dynasty.api.loader;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public abstract class Loader {

    private final List<LoadInfo<?>> infoLists = new ArrayList<>();

    public abstract <T extends LoadObject> void load(LoadInfo<T> loadInfo);

    public abstract <T extends LoadObject> T save(LoadInfo<T> loadInfo);

    public void onLoadComplete() {
    }

    public void save(String loadName) {
        for (LoadInfo<?> infoList : infoLists)
            if (infoList.getName().equalsIgnoreCase(loadName))
                save(infoList);
    }

    public <T extends LoadObject> void save(Class<T> tClass) {
        for (LoadInfo<?> infoList : infoLists)
            if (infoList.getClass().toString().equals(tClass.toString()))
                save(infoList);
    }

    public <T extends LoadObject> T update(Class<T> tClass, T data) {
        if (getInfo(tClass) != null)
            getInfo(tClass).setObject(data);
        return data;
    }

    public <T extends LoadObject> void remove(LoadInfo<T> loadInfo) {
        getInfoLists().remove(loadInfo);
    }

    @Deprecated
    public <T> T getData(Class<T> tClass) {
        for (LoadInfo<?> infoList : infoLists)
            if (infoList.getType().toString().equals(tClass.toString()))
                return (T) infoList.getObject();
        return null;
    }

    public <T extends LoadObject> Optional<T> getDataOptional(Class<T> tClass) {
        Optional<LoadInfo<?>> info = infoLists.stream().filter(loadInfo -> loadInfo.getType().getName().equals(tClass.getName())).findFirst();
        if(info.isEmpty()) return Optional.empty();
        return (Optional<T>) Optional.ofNullable(info.get().getObject());
    }

    public <T extends LoadObject> LoadInfo<T> getInfo(Class<T> tClass) {
        for (LoadInfo<?> infoList : infoLists)
            if (infoList.getType().equals(tClass))
                return (LoadInfo<T>) infoList;
        return null;
    }

    public <T extends LoadObject> void addInfo(LoadInfo<T> loadInfo) {
        infoLists.add(loadInfo);
    }

    @Getter
    @Setter
    public static class LoadInfo<T extends LoadObject> {
        private final String name;
        private final Class<T> type;
        private final T defaultObject;
        private T object;

        public LoadInfo(String name, Class<T> type, T defaultObject) {
            this.name = name;
            this.type = type;
            this.defaultObject = defaultObject;
            this.object = defaultObject;
        }

        @Override
        public String toString() {
            return "LoadInfo{" +
                    "name='" + name + '\'' +
                    ", type=" + type +
                    ", defaultObject=" + defaultObject +
                    ", object=" + object +
                    '}';
        }
    }

}
