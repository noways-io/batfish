package org.batfish.representation.azure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.batfish.datamodel.Ip;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicIpAddress extends Resource {

    private final PublicIpAddressProperties _properties;

    public PublicIpAddress(
            @JsonProperty(AzureEntities.JSON_KEY_NAME) String name,
            @JsonProperty(AzureEntities.JSON_KEY_ID) String id,
            @JsonProperty(AzureEntities.JSON_KEY_TYPE) String type,
            @JsonProperty(AzureEntities.JSON_KEY_PROPERTIES) PublicIpAddressProperties properties) {
        super(name, id, type);
        _properties = properties;
    }

    public PublicIpAddressProperties getProperties() {
        return _properties;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PublicIpAddressProperties{
        private final Ip _ipAddress;

        public PublicIpAddressProperties(
                @JsonProperty(AzureEntities.JSON_KEY_PUBLIC_IP) Ip ipAddress) {
            _ipAddress = ipAddress;
        }

        public Ip getIpAddress() {
            return _ipAddress;
        }
    }
}
