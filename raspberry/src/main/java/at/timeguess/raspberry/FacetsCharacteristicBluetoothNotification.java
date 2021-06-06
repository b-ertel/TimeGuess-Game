package at.timeguess.raspberry;

import tinyb.BluetoothNotification;

/**
 * A BluetoothNotification for the facets characteristic.
 */
public class FacetsCharacteristicBluetoothNotification implements BluetoothNotification<byte[]> {

    private String macAddress;
    private MessageSender messageSender;

    public FacetsCharacteristicBluetoothNotification(String macAddress, MessageSender messageSender) {
        this.macAddress = macAddress;
        this.messageSender = messageSender;
    }

    public void run(byte[] facetRaw) {
        FacetsMessage facetsMessage = new FacetsMessage();
        facetsMessage.setIdentifier(macAddress);
        facetsMessage.setFacet(facetRaw[0]);
        messageSender.sendFacetsMessage(facetsMessage);
    }

}
