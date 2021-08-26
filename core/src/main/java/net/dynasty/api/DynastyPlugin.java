package net.dynasty.api;

import lombok.Getter;
import lombok.Setter;
import net.dynasty.api.module.DynastyModule;
import net.dynasty.api.module.DynastyProject;

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
