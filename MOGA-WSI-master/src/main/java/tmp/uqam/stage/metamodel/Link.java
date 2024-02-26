package tmp.uqam.stage.metamodel;

import tmp.uqam.stage.slicing.ClassVertex;

import java.util.Objects;

/**
 * A simple link between two classvertex
 */
public class Link {

    private ClassVertex from;
    private ClassVertex to;

    public Link(ClassVertex from, ClassVertex to) {
        this.from = from;
        this.to = to;
    }

    public ClassVertex getFrom() {
        return from;
    }

    public ClassVertex getTo() {
        return to;
    }

    /**
     * Two links are equal if they have the same source and same target
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(from, link.from) &&
                Objects.equals(to, link.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return "{" + from.getName() +
                "-" + to.getName() +
                '}';
    }
}
