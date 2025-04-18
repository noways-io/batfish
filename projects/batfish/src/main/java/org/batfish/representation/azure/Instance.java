package org.batfish.representation.azure;

import java.io.Serializable;
import org.batfish.datamodel.Configuration;

public abstract class Instance extends Resource implements Serializable {

  public Instance(String name, String id, String type) {
    super(name, id, type);
  }

  public abstract Configuration toConfigurationNode(
      Region rgp, ConvertedConfiguration convertedConfiguration);
}
