package com.thoughtworks.fireworks.core.tree;

import com.thoughtworks.fireworks.core.ConsoleViewAdaptee;
import com.thoughtworks.shadow.ComparableTestShadow;
import com.thoughtworks.shadow.Shadow;
import com.thoughtworks.shadow.ShadowCabinetListener;
import com.thoughtworks.shadow.TestStateListener;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class ShadowTreeModel implements TreeModel, TestListener, ShadowCabinetListener, Log {
    private final List<TreeModelListener> listeners = new ArrayList();

    private final TreeNodesMap treeNodes;
    private final ShadowSummaryTreeNode root;

    private List<Test> tests = new ArrayList();
    private TestFailures testFailures = new TestFailures();

    public ShadowTreeModel(ShadowSummaryTreeNode root) {
        this.root = root;
        this.treeNodes = new TreeNodesMap(root);
    }

    public Object getRoot() {
        return root;
    }

    public Object getChild(Object parent, int index) {
        return treeNodes.getChild((ShadowTreeNode) parent, index);
    }

    public int getChildCount(Object parent) {
        return treeNodes.getChildCount((ShadowTreeNode) parent);
    }

    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException("UnsupportedOperation: path=" + path + "; value=" + newValue);
    }

    public int getIndexOfChild(Object parent, Object child) {
        return treeNodes.getIndexOfChild((ShadowTreeNode) parent, child);
    }

    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    public void addError(Test test, Throwable t) {
        testFailures.addIntoBuffer((Shadow) test, t);
    }

    public void addFailure(Test test, AssertionFailedError t) {
        testFailures.addIntoBuffer((Shadow) test, t);
    }

    public void endTest(Test test) {
    }

    public void startTest(Test test) {
        tests.add(test);
    }

    public void afterAddTest(ComparableTestShadow test) {
        test.addListener(new TestStateListener() {
            public void endTestShadow(ComparableTestShadow shadow, boolean wasSuccessful, int times) {
                ShadowClassTreeNode parent = toShadowClassTreeNode(shadow);
                testFailures.commitBuffer(parent);
                for (Test test : tests) {
                    addNode(toTreeNode(test, parent));
                }
                tests.clear();
            }
        });
        root.childrenIncreased();
        addNode(toShadowClassTreeNode(test));
    }

    public void afterRemoveTest(ComparableTestShadow test) {
        root.childrenDecreased();
        removeNode(toShadowClassTreeNode(test));
    }

    public void output(ShadowTreeNode node, ConsoleViewAdaptee consoleView) {
        testFailures.output(node, consoleView);
    }

    public void startAction() {
        testFailures = new TestFailures();
        tests = new ArrayList();
        List<ShadowTreeNode> nodes = treeNodes.clearTestMethodNode();
        for (ShadowTreeNode node : nodes) {
            removeNode(node);
        }
    }

    public void endAction() {
    }

    private void removeNode(ShadowTreeNode child) {
        TreeModelEvent event = createTreeModelEvent(child);
        treeNodes.remove(child);
        fireTreeNodeRemovedEvent(event);
    }

    private void addNode(ShadowTreeNode child) {
        treeNodes.add(child);
        fireTreeNodeInsertedEvent(createTreeModelEvent(child));
    }

    private void fireTreeNodeRemovedEvent(TreeModelEvent event) {
        for (TreeModelListener listener : listeners) {
            listener.treeNodesRemoved(event);
        }
    }

    private void fireTreeNodeInsertedEvent(TreeModelEvent event) {
        for (TreeModelListener listener : listeners) {
            listener.treeNodesInserted(event);
        }
    }

    private TreeModelEvent createTreeModelEvent(ShadowTreeNode child) {
        int index = getIndexOfChild(child.parent(), child);
        return new TreeModelEvent(this, getParents(child.parent()), new int[]{index}, new Object[]{child});
    }

    private ShadowTreeNode[] getParents(ShadowTreeNode node) {
        List<ShadowTreeNode> list = new ArrayList();
        while (node != null) {
            list.add(0, node);
            node = node.parent();
        }
        return list.toArray(new ShadowTreeNode[list.size()]);
    }

    private ShadowClassTreeNode toShadowClassTreeNode(ComparableTestShadow test) {
        return new ShadowClassTreeNode(test, root);
    }

    private ShadowMethodTreeNode toTreeNode(Test test, ShadowTreeNode parent) {
        return new ShadowMethodTreeNode((Shadow) test, parent);
    }

}
