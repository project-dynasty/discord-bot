package net.dynasty.api;

import lombok.Getter;
import lombok.Setter;
import net.dynasty.api.plugin.DynastyModule;
import net.dynasty.api.plugin.DynastyProject;

import java.io.Serializable;

@Getter
@Setter
public abstract class DynastyPlugin implements Serializable, DynastyProject {

    private DynastyModule module;

    public void init() {
    }

    public abstract void enable();

    public abstract void disable();

}
