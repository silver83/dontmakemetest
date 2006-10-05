/*
 *    Copyright (c) 2006 LiXiao.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.thoughtworks.fireworks.ui.tree;

import com.thoughtworks.fireworks.core.tree.ShadowMethodTreeNode;
import com.thoughtworks.fireworks.core.tree.ShadowTreeNode;
import com.thoughtworks.fireworks.core.tree.TestStatusSummaryListener;
import com.thoughtworks.shadow.Shadow;
import org.apache.commons.collections.map.HashedMap;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.util.Collections;
import java.util.Map;

public class TestShadowTreeRenderer implements TreeCellRenderer, TestStatusSummaryListener {

    private Map<ShadowTreeNode, Icon> icons = Collections.synchronizedMap(new HashedMap());

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                  boolean expanded, boolean leaf,
                                                  int row, boolean hasFocus) {
        ShadowTreeNode node = ((ShadowTreeNode) value);

        DefaultTreeCellRenderer mainLabel = new DefaultTreeCellRenderer();
        mainLabel.getTreeCellRendererComponent(tree, node.label(), sel, expanded, leaf, row, hasFocus);
        mainLabel.setIcon(icons.get(node));

        return panel(mainLabel, accessoryLabel(mainLabel, node));
    }

    private JPanel panel(DefaultTreeCellRenderer mainLabel, JLabel accessoryLabel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 1, 1));
        panel.setBackground(mainLabel.getBackground());
        panel.add(mainLabel);
        panel.add(accessoryLabel);
        return panel;
    }

    private JLabel accessoryLabel(DefaultTreeCellRenderer mainLabel, ShadowTreeNode node) {
        JLabel accessoryLabel = new JLabel();
        accessoryLabel.setForeground(Color.GRAY);
        accessoryLabel.setBackground(mainLabel.getBackground());
        accessoryLabel.setText(node.accessory());
        return accessoryLabel;
    }

    public void summaryStatusChanged(Icon status) {
    }

    public void statusChanged(Object key, Icon status) {
        icons.put(toShadowTreeNode(key), status);
    }

    private ShadowTreeNode toShadowTreeNode(Object key) {
        if (key instanceof ShadowTreeNode) {
            return (ShadowTreeNode) key;
        }
        return new ShadowMethodTreeNode((Shadow) key, null);
    }
}