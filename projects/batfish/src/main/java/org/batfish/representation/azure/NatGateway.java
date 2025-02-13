package org.batfish.representation.azure;

import org.batfish.datamodel.BgpProcess;
import org.batfish.datamodel.BgpUnnumberedPeerConfig;
import org.batfish.datamodel.Configuration;
import org.batfish.datamodel.ConfigurationFormat;
import org.batfish.datamodel.Interface;
import org.batfish.datamodel.InterfaceType;
import org.batfish.datamodel.LineAction;
import org.batfish.datamodel.LinkLocalAddress;
import org.batfish.datamodel.Prefix;
import org.batfish.datamodel.StaticRoute;
import org.batfish.datamodel.Vrf;
import org.batfish.datamodel.bgp.Ipv4UnicastAddressFamily;
import org.batfish.datamodel.bgp.LocalOriginationTypeTieBreaker;
import org.batfish.datamodel.route.nh.NextHop;

import static org.batfish.datamodel.bgp.NextHopIpTieBreaker.HIGHEST_NEXT_HOP_IP;

public class NatGateway {

    private final String _name;

    public NatGateway(String name) {
        _name = name;
    }

    public Configuration toConfigurationNode(ConvertedConfiguration convertedConfiguration){

        Configuration cfgNode = Configuration.builder()
                .setHostname(_name)
                .setConfigurationFormat(ConfigurationFormat.AZURE)
                .setDefaultCrossZoneAction(LineAction.PERMIT)
                .setDefaultInboundAction(LineAction.PERMIT)
                .setDomainName("azure")
                .build();

        Vrf.builder()
                .setName(Configuration.DEFAULT_VRF_NAME)
                .setOwner(cfgNode)
                .build();

        {   // Internet
            Interface.builder()
                    .setName("backbone")
                    .setVrf(cfgNode.getDefaultVrf())
                    .setOwner(cfgNode)
                    .setType(InterfaceType.PHYSICAL)
                    .setAddress(LinkLocalAddress.of(AzureConfiguration.LINK_LOCAL_IP))
                    .build();

            BgpProcess process = BgpProcess.builder()
                    .setRouterId(AzureConfiguration.LINK_LOCAL_IP)
                    .setVrf(cfgNode.getDefaultVrf())
                    .setEbgpAdminCost(20)
                    .setIbgpAdminCost(200)
                    .setLocalAdminCost(200)
                    // arbitrary values below since does not export from BGP RIB
                    .setLocalOriginationTypeTieBreaker(LocalOriginationTypeTieBreaker.NO_PREFERENCE)
                    .setNetworkNextHopIpTieBreaker(HIGHEST_NEXT_HOP_IP)
                    .setRedistributeNextHopIpTieBreaker(HIGHEST_NEXT_HOP_IP)
                    .build();

            BgpUnnumberedPeerConfig.builder()
                    .setPeerInterface("backbone")
                    .setRemoteAs(8075)
                    .setLocalIp(AzureConfiguration.LINK_LOCAL_IP)
                    .setLocalAs(AzureConfiguration.AZURE_LOCAL_ASN)
                    .setBgpProcess(process)
                    .setIpv4UnicastAddressFamily(
                        Ipv4UnicastAddressFamily.builder().setExportPolicy("test").build())
                    .build();

        }
        {   // Subnet
            Interface.builder()
                    .setName("subnet")
                    .setVrf(cfgNode.getDefaultVrf())
                    .setOwner(cfgNode)
                    .setAddress(LinkLocalAddress.of(AzureConfiguration.LINK_LOCAL_IP))
                    .build();

            StaticRoute st = StaticRoute.builder()
                    .setNetwork(Prefix.parse("88.183.185.217/32"))
                    .setNextHop(NextHop.legacyConverter("subnet", AzureConfiguration.LINK_LOCAL_IP))
                    .setNonForwarding(false)
                    .setAdministrativeCost(0)
                    .setMetric(0)
                    .build();

            cfgNode.getDefaultVrf().getStaticRoutes().add(st);
        }


        return cfgNode;
    }
}
