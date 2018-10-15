package de.hhu.bsinfo.dxapp;

import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.util.NodeCapabilities;

import java.util.Optional;

public class NetworkHelper {

    public static short findStorageNode(BootService p_bootService) {
        Optional<Short> targetOptional = p_bootService.getSupportingNodes(NodeCapabilities.STORAGE).stream()
                .filter(id -> id != p_bootService.getNodeID())
                .findFirst();

        while (!targetOptional.isPresent()) {
            targetOptional = p_bootService.getSupportingNodes(NodeCapabilities.STORAGE).stream()
                    .filter(id -> id != p_bootService.getNodeID())
                    .findFirst();
        }

        return targetOptional.get();
    }
}
