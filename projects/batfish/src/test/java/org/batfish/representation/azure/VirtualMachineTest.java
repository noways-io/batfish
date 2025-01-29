package org.batfish.representation.azure;

import com.fasterxml.jackson.databind.JsonNode;
import org.batfish.common.util.BatfishObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.batfish.common.util.Resources.readResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class VirtualMachineTest {

    @Test
    public void testDeserialization() throws IOException {
        String text = readResource("org/batfish/representation/azure/VirtualMachineTest.json", UTF_8);
        JsonNode node = BatfishObjectMapper.mapper().readTree(text);

        VirtualMachine vm = BatfishObjectMapper.mapper().convertValue(node, VirtualMachine.class);

        assertEquals("VM1", vm.getName());
        assertEquals("resourceGroups/test/providers/Microsoft.Compute/virtualMachines/VM1", vm.getId());
        assertEquals("Microsoft.Compute/virtualMachines", vm.getType());

        VirtualMachine.VirtualMachineProperties properties = vm.getProperties();
        assertNotNull(properties);

        for(NetworkInterfaceId networkInterfaceId : properties.getNetworkProfile().getNetworkInterfaces()){
            assertNotNull(networkInterfaceId);
            assertEquals(
                    "resourceGroups/test/providers/Microsoft.Network/networkInterfaces/vm1346",
                    networkInterfaceId.getId());
        }
    }
}
