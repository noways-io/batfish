package org.batfish.specifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import org.batfish.datamodel.Configuration;
import org.batfish.datamodel.ConfigurationFormat;
import org.batfish.datamodel.Interface;
import org.batfish.datamodel.collections.NodeInterfacePair;
import org.junit.Test;

public class NodeSpecifierInterfaceSpecifierTest {

  @Test
  public void testResolve() {

    Configuration node1 = new Configuration("node1", ConfigurationFormat.CISCO_IOS);
    Configuration node2 = new Configuration("node2", ConfigurationFormat.CISCO_IOS);

    Interface iface1node1 = Interface.builder().setName("iface1").setOwner(node1).build();
    Interface iface2node1 = Interface.builder().setName("iface2").setOwner(node1).build();
    Interface iface1node2 = Interface.builder().setName("iface1").setOwner(node2).build();

    node1.setInterfaces(
        ImmutableSortedMap.of(
            iface1node1.getName(), iface1node1, iface2node1.getName(), iface2node1));
    node2.setInterfaces(ImmutableSortedMap.of(iface1node2.getName(), iface1node2));

    MockSpecifierContext ctxt =
        MockSpecifierContext.builder()
            .setConfigs(ImmutableMap.of(node1.getHostname(), node1, node2.getHostname(), node2))
            .build();

    // common node
    assertThat(
        new NodeSpecifierInterfaceSpecifier(new NameNodeSpecifier("node1"))
            .resolve(ImmutableSet.of("node1", "node2"), ctxt),
        containsInAnyOrder(NodeInterfacePair.of(iface1node1), NodeInterfacePair.of(iface2node1)));

    // no common nodes
    assertThat(
        new NodeSpecifierInterfaceSpecifier(new NameNodeSpecifier("node1"))
            .resolve(ImmutableSet.of("node2"), ctxt),
        empty());
  }
}
