package rtree;

import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;
import rtree.factories.DivisionPerformerFactory;
import rtree.implementations.UniformDivisionPerformer;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class DivisionTest extends DimensionalTest {

  private final int minSubNodes = 4;

  private final int maxSubNodes = 10;

  private TreeNode nodeToSplit;

  private DivisionPerformerFactory performerFactory = UniformDivisionPerformer::new;

  public DivisionTest(Integer dimensions) {
    super(dimensions);
  }

  @Before
  public void setUp() {
    nodeToSplit = new TreeNode.InMemory(SpatialKeyTest.zeroCentredCube(20, dimensions));
  }

  @Test
  public void testSplit() {
    TreeNode nodeToSplit = this.nodeToSplit;
    performSplitAndTest(nodeToSplit);
  }

  @Test
  public void testSplitContainsExactlyMin() {
    addSubNodesEnoughToSplit(nodeToSplit);
    Set<SpatialKey> keys = nodeToSplit.subNodes().map(Node::spatialKey).collect(Collectors.toSet());
    int splitSize = performerFactory.create(keys)
        .divide(minSubNodes).size();
    assertThat(splitSize).isCloseTo(minSubNodes, Offset.offset(1));
  }

  private void performSplitAndTest(TreeNode nodeToSplit) {
    addSubNodesEnoughToSplit(nodeToSplit);
    Set<SpatialKey> keys = nodeToSplit.subNodes().map(Node::spatialKey).collect(Collectors.toSet());
    Set<SpatialKey> split = performerFactory.create(keys)
        .divide(minSubNodes);
    SpatialKey unionActual = SpatialKey.union(split);
    SpatialKey unionExpected = SpatialKey.union(keys);
    assertThat(unionActual.volume()).isEqualTo(unionExpected.volume());
  }

  protected void addSubNodesEnoughToSplit(TreeNode nodeToSplit) {
    Set<TreeNode> nodes = IntStream.range(0, maxSubNodes + 1)
        .mapToObj(i -> new TreeNode.InMemory(randomKey()))
        .collect(Collectors.toSet());
    nodes.forEach(nodeToSplit::addSubNode);
  }

  private SpatialKey randomKey() {
    return SpatialKeyTest.randomBox(nodeToSplit.spatialKey(), 1.0);
  }
}