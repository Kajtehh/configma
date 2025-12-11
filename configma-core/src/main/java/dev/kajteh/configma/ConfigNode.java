package dev.kajteh.configma;

import java.util.List;

// TODO: 12/11/2025 implement it
public record ConfigNode(String key, Object value, List<ConfigNode> children) {

    public boolean isLeaf() {
        return this.children == null || this.children.isEmpty();
    }
}
