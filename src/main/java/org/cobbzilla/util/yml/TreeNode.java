package org.cobbzilla.util.yml;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by olivier.revial on 24/08/2017.
 */
public class TreeNode<T> {
    private TreeNode<T> parent;
    private String key;
    private T value;
    private List<TreeNode<T>> children = new ArrayList<>();

    // default constructor
    public TreeNode(String key, T value)
    {
        setParent(null);
        this.key = key;
        this.value = value;
    }

    // constructor overloading to set the parent
    public TreeNode(TreeNode<T> parent)
    {
        this.setParent(parent);
        //this.addChild(parent);
    }

    // constructor overloading to set the parent of the list
    public TreeNode(TreeNode<T> parent, TreeNode<T> child)
    {
        this(parent);
        this.children.add(child);
    }

    /**
     * This method doesn't return anything and takes a parameter of
     * the object type you are trying to store in the node
     **/
    public void addChild(TreeNode<T> child)
    {
        child.setParent(this);
        this.children.add(child); // add this child to the list
    }

    public void removeChild(TreeNode<T> child)
    {
        this.children.remove(child); // remove this child from the list
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean hasChildren()
    {
        return this.children.size()>0;
    }

    @SuppressWarnings("unchecked")
    public TreeNode<T>[] children()
    {
        return (TreeNode<T>[]) children.toArray(new TreeNode[children.size()]);
    }

    public TreeNode<T> getChild(String key) {
        for(TreeNode<T> child : children()) {
            if(child.getKey().equals(key)) {
                return child;
            }
        }
        return null;
    }
}
