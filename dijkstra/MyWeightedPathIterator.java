package dijkstra;

import java.util.Iterator;

import org.neo4j.graphalgo.CostEvaluator;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.Path;
import org.neo4j.helpers.collection.PrefetchingIterator;

public class MyWeightedPathIterator extends PrefetchingIterator<WeightedPath> {

    private final Iterator<Path> paths;
    private final CostEvaluator<Double> costEvaluator;
    private Double foundWeight;
    private final boolean stopAfterLowestWeight;

    public MyWeightedPathIterator( Iterator<Path> paths, CostEvaluator<Double> costEvaluator,
            boolean stopAfterLowestWeight )
    {
        this.paths = paths;
        this.costEvaluator = costEvaluator;
        this.stopAfterLowestWeight = stopAfterLowestWeight;
    }

    @Override
    protected WeightedPath fetchNextOrNull()
    {
        if ( !paths.hasNext() )
        {
            return null;
        }
        WeightedPath path = new MyWeightedPathImpl( costEvaluator, paths.next() );

        //  if ( stopAfterLowestWeight && foundWeight != null && path.weight() > foundWeight )
        if ( stopAfterLowestWeight && foundWeight != null && path.weight() < foundWeight )
        {
            return null;
        }
        foundWeight = path.weight();
        return path;
    }
}
