package org.batfish.representation.azure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import org.batfish.datamodel.*;
import org.batfish.datamodel.acl.MatchHeaderSpace;
import org.batfish.datamodel.bgp.Ipv4UnicastAddressFamily;
import org.batfish.datamodel.bgp.LocalOriginationTypeTieBreaker;
import org.batfish.datamodel.route.nh.NextHop;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import static org.batfish.datamodel.IpProtocol.*;
import static org.batfish.datamodel.bgp.NextHopIpTieBreaker.HIGHEST_NEXT_HOP_IP;

public class NatGateway extends Resource implements Serializable {

    static final List<IpProtocol> NAT_PROTOCOLS = ImmutableList.of(TCP, UDP, ICMP);
    private final NatGatewayProperties _properties;

    public NatGateway(
            @JsonProperty(AzureEntities.JSON_KEY_ID) String id,
            @JsonProperty(AzureEntities.JSON_KEY_NAME) String name,
            @JsonProperty(AzureEntities.JSON_KEY_TYPE) String type,
            @JsonProperty(AzureEntities.JSON_KEY_PROPERTIES) NatGatewayProperties properties
    )
    {
        super(name, id, type);
        _properties = properties;
    }

    public String getNodeName() {
        return getCleanId();
    }

    public Configuration toConfigurationNode(ConvertedConfiguration convertedConfiguration){

        Configuration cfgNode = Configuration.builder()
                .setHostname(getCleanId())
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

    public NatGatewayProperties getProperties() {
        return _properties;
    }

    public static class NatGatewayProperties implements Serializable {
        private final Set<PublicIpAddressId> _publicIpAddresses;
        private final Set<PublicIpPrefixId>  _publicIpPrefixes;

        @JsonCreator
        public NatGatewayProperties(
                @JsonProperty("publicIpAddresses") Set<PublicIpAddressId> publicIpAddresses,
                @JsonProperty("publicIpPrefixes") Set<PublicIpPrefixId> publicIpPrefixes
        ) {
            _publicIpAddresses = publicIpAddresses;
            _publicIpPrefixes = publicIpPrefixes;
        }

        public Set<PublicIpAddressId> getPublicIpAddresses() {
            return _publicIpAddresses;
        }

        public Set<PublicIpPrefixId> getPublicIpPrefixes() {
            return _publicIpPrefixes;
        }
    }

    public static class PublicIpAddressId implements Serializable {
        private final String _id;

        @JsonCreator
        public PublicIpAddressId(
                @JsonProperty(AzureEntities.JSON_KEY_ID) String id) {
            _id = id;
        }

        public String getId() {
            return _id;
        }
    }

    public static class PublicIpPrefixId implements Serializable {
        private final String _id;

        @JsonCreator
        public PublicIpPrefixId(
                @JsonProperty(AzureEntities.JSON_KEY_ID) String id
        ) {
            _id = id;
        }

        public String getId() {
            return _id;
        }
    }
}
