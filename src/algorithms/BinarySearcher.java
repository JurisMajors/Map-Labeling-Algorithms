package algorithms;

import interfaces.AbstractAlgorithmInterface;
import interfaces.AbstractCollectionInterface;
import interfaces.models.RectangleInterface;

public abstract class BinarySearcher<T extends RectangleInterface> implements AbstractAlgorithmInterface<T> {
    /**
     *
     * uses binary search to find the optimal height for the rectangles
     *
     * @param nodes {@link AbstractAlgorithmInterface}
     */
    @Override
    public void solve(AbstractCollectionInterface<T> nodes) {

        // TODO: needs to be grabbed from input
        float alpha = 1.4f;

        // TODO: this value can be optimized base on the number of points
        int high = (int)(10000 * Math.sqrt(alpha));
        int low = 0;


        float ratio = 1.4f;


        // make sure that our upper bound is correct. This may not be required if we have a good estimation
        while (isSolvable(nodes, high)) {
            low = high;
            // TODO: value can be optimized (see earlier TODO)
            high *= 2;
        }


        while (low < high - 1) {
            int mid = (high + low) / 2;
            if (isSolvable(nodes, mid)) {
                low = mid;
            } else {
                high = mid;
            }
        }

        float height = low;


        // TODO: this needs to be fixed
        System.out.println(low + " " + high);

        float newHeight = (float) Math.floor(high*ratio) / ratio;
        if (newHeight > height && isSolvable(nodes, newHeight)) {
            height = newHeight;
        }

        newHeight = (float) (Math.floor(high*ratio) - 0.5) / ratio;
        if (newHeight > height && isSolvable(nodes, newHeight)) {
            height = newHeight;
        }

        getSolution(nodes, height);
    }

    /**
     * returns if labels can be placed for a given height
     *
     * @modifies none
     * @param nodes {@link AbstractAlgorithmInterface}
     * @param height required height for rectangles
     */
    abstract boolean isSolvable(AbstractCollectionInterface<T> nodes, float height);

    /**
     * Place all labels with the given height (this method is not robust to reduce computation time)
     *
     * @modifies nodes
     * @param nodes {@link AbstractAlgorithmInterface}
     * @param height required height for rectangles
     * @pre isSolvable(nodes, height)
     */
    abstract void getSolution(AbstractCollectionInterface<T> nodes, float height);

}
