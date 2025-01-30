package org.batfish.representation.azure;

import com.fasterxml.jackson.databind.JsonNode;
import org.batfish.common.util.BatfishObjectMapper;
import org.batfish.datamodel.Ip;
import org.junit.Test;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.batfish.common.util.Resources.readResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PublicIpTest {

    @Test
    public void testDeserialization() throws IOException {
        String text = readResource("org/batfish/representation/azure/PublicIpAddressTest.json", UTF_8);
        JsonNode node = BatfishObjectMapper.mapper().readTree(text);

        PublicIpAddress publicIpAddress = BatfishObjectMapper.mapper().convertValue(node, PublicIpAddress.class);
        assertNotNull(publicIpAddress);

        assertEquals("test-ip", publicIpAddress.getName());
        assertEquals("resourceGroups/test/providers/Microsoft.Network/publicIPAddresses/test-ip",
                publicIpAddress.getId());
        assertEquals("Microsoft.Network/publicIPAddresses", publicIpAddress.getType());

        PublicIpAddress.PublicIpAddressProperties properties = publicIpAddress.getProperties();
        assertNotNull(properties);

        assertEquals(Ip.parse("40.40.40.40"), properties.getIpAddress());
    }
}
