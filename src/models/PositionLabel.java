package models;

public class PositionLabel extends AbstractLabel {
    protected DirectionEnum direction;

    public PositionLabel(double x, double y, double size, DirectionEnum direction) {
        super(x, y, size);
        this.setEdgeLength(size, direction);
    }

    public void setEdgeLength(double edgeLength, DirectionEnum direction) throws IllegalArgumentException {
        if (edgeLength < 0) {
            throw new IllegalArgumentException("PositionLabel.setEdgeLength.pre violated: edgeLength < 0");
        }
        super.setEdgeLength(edgeLength);

        switch (direction) {
            case NE:
                this.setAnchor(new Anchor(this.getPOI().getX(), this.getPOI().getY()));
                break;
            case NW:
                this.setAnchor(new Anchor(this.getPOI().getX() - edgeLength, this.getPOI().getY()));
                break;
            case SE:
                this.setAnchor(new Anchor(this.getPOI().getX(), this.getPOI().getY() - edgeLength));
                break;
            case SW:
                this.setAnchor(new Anchor(this.getPOI().getX() - edgeLength, this.getPOI().getY() - edgeLength));
                break;
            default:
                throw new IllegalArgumentException(direction.toString() + " not implemented!");
        }

        this.setEdgeLength(edgeLength);
        this.direction = direction;
    }
}
