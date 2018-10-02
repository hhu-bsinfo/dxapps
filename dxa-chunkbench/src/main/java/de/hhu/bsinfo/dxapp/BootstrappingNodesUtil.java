package de.hhu.bsinfo.dxapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.util.NodeRole;
import de.hhu.bsinfo.dxutils.NodeID;

class BootstrappingNodesUtil {
    private static final Logger LOGGER = LogManager.getFormatterLogger(BootstrappingNodesUtil.class.getSimpleName());

    private final BootService m_bootService;

    BootstrappingNodesUtil(final BootService p_bootService) {
        m_bootService = p_bootService;
    }

    List<List<Short>> bootstrapNodesListParameters(final String p_activeNodesPerPhaseStr) {
        List<List<Short>> list = parsePhaseNodeIdList(p_activeNodesPerPhaseStr);

        if (!checkNodesOnline(list)) {
            return null;
        }

        return list;
    }

    List<List<Short>> bootstrapNodesCountParameters(final String p_activeNodesPerPhaseStr) {
        List<Integer> nodeCounts = parsePhaseNodeCountList(p_activeNodesPerPhaseStr);
        List<List<Short>> list = generateActiveNodesPerPhaseList(nodeCounts);

        if (list == null) {
            return null;
        }

        if (!checkNodesOnline(list)) {
            return null;
        }

        return list;
    }

    static List<Short> getParticipatingNodes(final List<List<Short>> p_activeNodesPerPhase) {
        List<Short> participatingNodes = new ArrayList<>();

        for (List<Short> nodesInPhase : p_activeNodesPerPhase) {
            for (Short node : nodesInPhase) {
                if (!participatingNodes.contains(node)) {
                    participatingNodes.add(node);
                }
            }
        }

        return participatingNodes;
    }

    private static List<List<Short>> parsePhaseNodeIdList(final String p_str) {
        List<List<Short>> list = new ArrayList<>();

        // for each phase, separated by :
        for (String phase : p_str.split(":")) {
            List<Short> nodes = new ArrayList<>();

            if (!phase.isEmpty()) {
                for (String node : phase.split(",")) {
                    // allow empty entries for empty phases
                    if (!node.isEmpty()) {
                        nodes.add(NodeID.parse(node));
                    }
                }
            }

            list.add(nodes);
        }

        return list;
    }

    private static List<Integer> parsePhaseNodeCountList(final String p_str) {
        List<Integer> list = new ArrayList<>();

        // for each phase, separated by :
        for (String phaseNodeCount : p_str.split(":")) {
            list.add(Integer.parseInt(phaseNodeCount));
        }

        return list;
    }

    private List<List<Short>> generateActiveNodesPerPhaseList(final List<Integer> p_nodeCountList) {
        List<List<Short>> nodesPerPhase = new ArrayList<>();

        List<Short> nodesAvail = m_bootService.getOnlinePeerNodeIDs();

        for (int nodeCount : p_nodeCountList) {
            List<Short> nodes = new ArrayList<>();

            if (nodeCount < 0) {
                LOGGER.error("Invalid total node count specified: %d", nodeCount);
                return null;
            }

            // if no NIDs specified, pick available nodes instead (not counting current node)
            if (nodeCount > nodesAvail.size()) {
                LOGGER.error("Not enough peers available (%d) to run benchmark with %d nodes", nodesAvail.size(),
                        nodeCount);
                return null;
            }

            // node count of 0 is ok to skip benchmark phases
            if (nodeCount > 0) {
                // always add current bootstrapping node
                nodes.add(m_bootService.getNodeID());
                nodeCount--;

                int idx = 0;

                while (nodeCount > 0) {
                    if (nodesAvail.get(idx) != m_bootService.getNodeID()) {
                        nodes.add(nodesAvail.get(idx));
                        nodeCount--;
                    }

                    idx++;
                }
            }

            nodesPerPhase.add(nodes);
        }

        return nodesPerPhase;
    }

    private boolean checkNodesOnline(final List<List<Short>> p_nodesPerPhase) {
        boolean success = true;

        for (List<Short> nodes : p_nodesPerPhase) {
            for (short nid : nodes) {
                if (!m_bootService.isNodeOnline(nid)) {
                    LOGGER.error("Node %s is not online", NodeID.toHexString(nid));
                    success = false;
                }

                if (m_bootService.getNodeRole(nid) != NodeRole.PEER) {
                    LOGGER.error("Node %s is not a peer", NodeID.toHexString(nid));
                    success = false;
                }
            }
        }

        return success;
    }
}
