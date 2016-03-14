package dijkstra;

import org.neo4j.graphalgo.CostEvaluator;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.PathExpander;

public abstract class MyGraphAlgoFactory extends GraphAlgoFactory {

	/**
	 * @function 重写GraphAlgoFactory的dijkstra方法
	 * @param expander
	 * @param costEvaluator
	 * @return
	 */
    @SuppressWarnings("rawtypes")
	public static PathFinder<WeightedPath> dijkstra( PathExpander expander,
            CostEvaluator<Double> costEvaluator )
    {
        return new MyDijkstra( expander, costEvaluator );
    }
	
}
