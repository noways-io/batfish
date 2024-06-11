package org.batfish.representation.fortios;

import javax.annotation.Nonnull;


public class VIP {

    private final @Nonnull String _name;

    public VIP(String name) {
        _name = name;
    }

    public @Nonnull String getName() {
        return _name;
    }

}
