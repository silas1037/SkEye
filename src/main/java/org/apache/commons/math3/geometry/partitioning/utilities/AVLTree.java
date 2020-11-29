package org.apache.commons.math3.geometry.partitioning.utilities;

import java.lang.Comparable;

@Deprecated
public class AVLTree<T extends Comparable<T>> {
    private AVLTree<T>.Node top = null;

    /* access modifiers changed from: private */
    public enum Skew {
        LEFT_HIGH,
        RIGHT_HIGH,
        BALANCED
    }

    public void insert(T element) {
        if (element == null) {
            return;
        }
        if (this.top == null) {
            this.top = new Node(element, null);
        } else {
            this.top.insert(element);
        }
    }

    public boolean delete(T element) {
        if (element == null) {
            return false;
        }
        for (AVLTree<T>.Node node = getNotSmaller(element); node != null; node = node.getNext()) {
            if (((Node) node).element == element) {
                node.delete();
                return true;
            } else if (((Node) node).element.compareTo(element) > 0) {
                return false;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return this.top == null;
    }

    public int size() {
        if (this.top == null) {
            return 0;
        }
        return this.top.size();
    }

    public AVLTree<T>.Node getSmallest() {
        if (this.top == null) {
            return null;
        }
        return this.top.getSmallest();
    }

    public AVLTree<T>.Node getLargest() {
        if (this.top == null) {
            return null;
        }
        return this.top.getLargest();
    }

    public AVLTree<T>.Node getNotSmaller(T reference) {
        AVLTree<T>.Node candidate = null;
        AVLTree<T>.Node node = this.top;
        while (node != null) {
            if (((Node) node).element.compareTo(reference) >= 0) {
                candidate = node;
                if (((Node) node).left == null) {
                    return candidate;
                }
                node = ((Node) node).left;
            } else if (((Node) node).right == null) {
                return candidate;
            } else {
                node = ((Node) node).right;
            }
        }
        return null;
    }

    public AVLTree<T>.Node getNotLarger(T reference) {
        AVLTree<T>.Node candidate = null;
        AVLTree<T>.Node node = this.top;
        while (node != null) {
            if (((Node) node).element.compareTo(reference) <= 0) {
                candidate = node;
                if (((Node) node).right == null) {
                    return candidate;
                }
                node = ((Node) node).right;
            } else if (((Node) node).left == null) {
                return candidate;
            } else {
                node = ((Node) node).left;
            }
        }
        return null;
    }

    public class Node {
        private T element;
        private AVLTree<T>.Node left = null;
        private AVLTree<T>.Node parent;
        private AVLTree<T>.Node right = null;
        private Skew skew;

        Node(T element2, AVLTree<T>.Node parent2) {
            this.element = element2;
            this.parent = parent2;
            this.skew = Skew.BALANCED;
        }

        /* JADX WARN: Type inference failed for: r0v0, types: [T, T extends java.lang.Comparable<T>] */
        public T getElement() {
            return this.element;
        }

        /* access modifiers changed from: package-private */
        public int size() {
            int i = 0;
            int size = (this.left == null ? 0 : this.left.size()) + 1;
            if (this.right != null) {
                i = this.right.size();
            }
            return size + i;
        }

        /* access modifiers changed from: package-private */
        public AVLTree<T>.Node getSmallest() {
            Node node = this;
            while (node.left != null) {
                node = node.left;
            }
            return node;
        }

        /* access modifiers changed from: package-private */
        public AVLTree<T>.Node getLargest() {
            Node node = this;
            while (node.right != null) {
                node = node.right;
            }
            return node;
        }

        public AVLTree<T>.Node getPrevious() {
            AVLTree<T>.Node node;
            if (!(this.left == null || (node = this.left.getLargest()) == null)) {
                return node;
            }
            for (Node node2 = this; node2.parent != null; node2 = node2.parent) {
                if (node2 != node2.parent.left) {
                    return node2.parent;
                }
            }
            return null;
        }

        public AVLTree<T>.Node getNext() {
            AVLTree<T>.Node node;
            if (!(this.right == null || (node = this.right.getSmallest()) == null)) {
                return node;
            }
            for (Node node2 = this; node2.parent != null; node2 = node2.parent) {
                if (node2 != node2.parent.right) {
                    return node2.parent;
                }
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        public boolean insert(T newElement) {
            if (newElement.compareTo(this.element) < 0) {
                if (this.left == null) {
                    this.left = new Node(newElement, this);
                    return rebalanceLeftGrown();
                } else if (this.left.insert(newElement)) {
                    return rebalanceLeftGrown();
                } else {
                    return false;
                }
            } else if (this.right == null) {
                this.right = new Node(newElement, this);
                return rebalanceRightGrown();
            } else if (this.right.insert(newElement)) {
                return rebalanceRightGrown();
            } else {
                return false;
            }
        }

        public void delete() {
            AVLTree<T>.Node node;
            boolean leftShrunk;
            AVLTree<T>.Node child;
            if (this.parent == null && this.left == null && this.right == null) {
                this.element = null;
                AVLTree.this.top = null;
                return;
            }
            if (this.left == null && this.right == null) {
                node = this;
                this.element = null;
                if (node == node.parent.left) {
                    leftShrunk = true;
                } else {
                    leftShrunk = false;
                }
                child = null;
            } else {
                node = this.left != null ? this.left.getLargest() : this.right.getSmallest();
                this.element = node.element;
                if (node == node.parent.left) {
                    leftShrunk = true;
                } else {
                    leftShrunk = false;
                }
                child = node.left != null ? node.left : node.right;
            }
            AVLTree<T>.Node node2 = node.parent;
            if (leftShrunk) {
                node2.left = child;
            } else {
                node2.right = child;
            }
            if (child != null) {
                child.parent = node2;
            }
            while (true) {
                if (leftShrunk) {
                    if (!node2.rebalanceLeftShrunk()) {
                        return;
                    }
                } else if (!node2.rebalanceRightShrunk()) {
                    return;
                }
                if (node2.parent != null) {
                    if (node2 == node2.parent.left) {
                        leftShrunk = true;
                    } else {
                        leftShrunk = false;
                    }
                    node2 = node2.parent;
                } else {
                    return;
                }
            }
        }

        private boolean rebalanceLeftGrown() {
            switch (this.skew) {
                case LEFT_HIGH:
                    if (this.left.skew == Skew.LEFT_HIGH) {
                        rotateCW();
                        this.skew = Skew.BALANCED;
                        this.right.skew = Skew.BALANCED;
                        return false;
                    }
                    Skew s = this.left.right.skew;
                    this.left.rotateCCW();
                    rotateCW();
                    switch (s) {
                        case LEFT_HIGH:
                            this.left.skew = Skew.BALANCED;
                            this.right.skew = Skew.RIGHT_HIGH;
                            break;
                        case RIGHT_HIGH:
                            this.left.skew = Skew.LEFT_HIGH;
                            this.right.skew = Skew.BALANCED;
                            break;
                        default:
                            this.left.skew = Skew.BALANCED;
                            this.right.skew = Skew.BALANCED;
                            break;
                    }
                    this.skew = Skew.BALANCED;
                    return false;
                case RIGHT_HIGH:
                    this.skew = Skew.BALANCED;
                    return false;
                default:
                    this.skew = Skew.LEFT_HIGH;
                    return true;
            }
        }

        private boolean rebalanceRightGrown() {
            switch (this.skew) {
                case LEFT_HIGH:
                    this.skew = Skew.BALANCED;
                    return false;
                case RIGHT_HIGH:
                    if (this.right.skew == Skew.RIGHT_HIGH) {
                        rotateCCW();
                        this.skew = Skew.BALANCED;
                        this.left.skew = Skew.BALANCED;
                        return false;
                    }
                    Skew s = this.right.left.skew;
                    this.right.rotateCW();
                    rotateCCW();
                    switch (s) {
                        case LEFT_HIGH:
                            this.left.skew = Skew.BALANCED;
                            this.right.skew = Skew.RIGHT_HIGH;
                            break;
                        case RIGHT_HIGH:
                            this.left.skew = Skew.LEFT_HIGH;
                            this.right.skew = Skew.BALANCED;
                            break;
                        default:
                            this.left.skew = Skew.BALANCED;
                            this.right.skew = Skew.BALANCED;
                            break;
                    }
                    this.skew = Skew.BALANCED;
                    return false;
                default:
                    this.skew = Skew.RIGHT_HIGH;
                    return true;
            }
        }

        private boolean rebalanceLeftShrunk() {
            switch (this.skew) {
                case LEFT_HIGH:
                    this.skew = Skew.BALANCED;
                    return true;
                case RIGHT_HIGH:
                    if (this.right.skew == Skew.RIGHT_HIGH) {
                        rotateCCW();
                        this.skew = Skew.BALANCED;
                        this.left.skew = Skew.BALANCED;
                        return true;
                    } else if (this.right.skew == Skew.BALANCED) {
                        rotateCCW();
                        this.skew = Skew.LEFT_HIGH;
                        this.left.skew = Skew.RIGHT_HIGH;
                        return false;
                    } else {
                        Skew s = this.right.left.skew;
                        this.right.rotateCW();
                        rotateCCW();
                        switch (s) {
                            case LEFT_HIGH:
                                this.left.skew = Skew.BALANCED;
                                this.right.skew = Skew.RIGHT_HIGH;
                                break;
                            case RIGHT_HIGH:
                                this.left.skew = Skew.LEFT_HIGH;
                                this.right.skew = Skew.BALANCED;
                                break;
                            default:
                                this.left.skew = Skew.BALANCED;
                                this.right.skew = Skew.BALANCED;
                                break;
                        }
                        this.skew = Skew.BALANCED;
                        return true;
                    }
                default:
                    this.skew = Skew.RIGHT_HIGH;
                    return false;
            }
        }

        private boolean rebalanceRightShrunk() {
            switch (this.skew) {
                case LEFT_HIGH:
                    if (this.left.skew == Skew.LEFT_HIGH) {
                        rotateCW();
                        this.skew = Skew.BALANCED;
                        this.right.skew = Skew.BALANCED;
                        return true;
                    } else if (this.left.skew == Skew.BALANCED) {
                        rotateCW();
                        this.skew = Skew.RIGHT_HIGH;
                        this.right.skew = Skew.LEFT_HIGH;
                        return false;
                    } else {
                        Skew s = this.left.right.skew;
                        this.left.rotateCCW();
                        rotateCW();
                        switch (s) {
                            case LEFT_HIGH:
                                this.left.skew = Skew.BALANCED;
                                this.right.skew = Skew.RIGHT_HIGH;
                                break;
                            case RIGHT_HIGH:
                                this.left.skew = Skew.LEFT_HIGH;
                                this.right.skew = Skew.BALANCED;
                                break;
                            default:
                                this.left.skew = Skew.BALANCED;
                                this.right.skew = Skew.BALANCED;
                                break;
                        }
                        this.skew = Skew.BALANCED;
                        return true;
                    }
                case RIGHT_HIGH:
                    this.skew = Skew.BALANCED;
                    return true;
                default:
                    this.skew = Skew.LEFT_HIGH;
                    return false;
            }
        }

        private void rotateCW() {
            T t = this.element;
            this.element = this.left.element;
            this.left.element = t;
            AVLTree<T>.Node tmpNode = this.left;
            this.left = tmpNode.left;
            tmpNode.left = tmpNode.right;
            tmpNode.right = this.right;
            this.right = tmpNode;
            if (this.left != null) {
                this.left.parent = this;
            }
            if (this.right.right != null) {
                this.right.right.parent = this.right;
            }
        }

        private void rotateCCW() {
            T t = this.element;
            this.element = this.right.element;
            this.right.element = t;
            AVLTree<T>.Node tmpNode = this.right;
            this.right = tmpNode.right;
            tmpNode.right = tmpNode.left;
            tmpNode.left = this.left;
            this.left = tmpNode;
            if (this.right != null) {
                this.right.parent = this;
            }
            if (this.left.left != null) {
                this.left.left.parent = this.left;
            }
        }
    }
}
