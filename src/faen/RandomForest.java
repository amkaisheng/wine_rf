package faen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class RandomForest {
    static class Util {
        public static void CHECK(boolean condition, String message) {
            if (!condition) {
                throw new RuntimeException(message);
            }
        }
    }

    public double[][] instances_;
    public int[] targets_;
    public int numOfTrees_;
    public int numOfFeatures_;
    public int maxDepth_;
    public int featureSize;
    public TreeNode[] trees_;
    public ArrayList<TreeInfo> treeinfo;
    public static Random rand = new Random();

    /**
     * Train the RF model
     * 
     * @param instances
     * @param targets
     * @param numOfTrees
     * @param numOfFeatures
     *            this could be -1, if so, the default value will be
     *            len(features)^0.5
     * @param maxDepth
     *            this could be -1, if so, any leaf node will have only 1
     *            instance.
     */
    public void train(double[][] instances, int[] targets, int numOfTrees, int numOfFeatures,
            int maxDepth, int treeSize) {
        Util.CHECK(instances.length == targets.length, "the length of instances does not match the length of targets");
        Util.CHECK(numOfTrees > 0, "the num of trees must more than zero");
        this.instances_ = instances;
        this.targets_ = targets;
        this.numOfTrees_ = numOfTrees;
        this.numOfFeatures_ = numOfFeatures;
        this.maxDepth_ = maxDepth;
        this.featureSize=instances_[0].length;
        this.trees_ = new TreeNode[numOfTrees_];
        this.treeinfo=new ArrayList<TreeInfo>(numOfTrees);
        for (int i = 0; i < trees_.length; i++) {
            System.out.println("building the tree:" + i);
            treeinfo.add(new TreeInfo());
        	treeinfo.get(i).setTreeID(i);
            trees_[i] = buildTree(getRandomInstances(treeSize), 1,treeinfo.get(i),i);
            treeinfo.get(i).setNumOfNode(treeinfo.get(i).nodeinfo.size());
            /*
            for(int j=0;j<treeinfo[i].nodeinfo.size();j++){
            	System.out.println("treeID: "+treeinfo[i].TreeID+" nodeID: "+j);
            	System.out.println(treeinfo[i].nodeinfo.get(j).nodeID);
            	System.out.println(treeinfo[i].nodeinfo.get(j).featureIndex_);
            	System.out.println(treeinfo[i].nodeinfo.get(j).nodeDepth);
            	System.out.println(treeinfo[i].nodeinfo.get(j).left_NodeID);
            	System.out.println(treeinfo[i].nodeinfo.get(j).right_NodeID);
            }
                */
        }
        
    }

    // Get sub set of all instances randomly.
    List<Integer> getRandomInstances(int numOfInstances) {
        List<Integer> ret = new ArrayList<Integer>(numOfInstances);
        //System.out.print("this is ret length: "+ret.size()+"\n");
        while (ret.size() < numOfInstances) {
            ret.add(rand.nextInt(instances_.length));
        }
        return ret;
    }

    // Get the majority class of all samples having indices.
    private int getMajorClass(List<Integer> indices) {
        Map<Integer, Integer> mii = new HashMap<Integer, Integer>();
        int best = -1;
        int ret = -1;
        for (int index : indices) {
            Integer v = mii.get(targets_[index]);
            if (v == null) {
                v = 0;
            }
            mii.put(targets_[index], v + 1);
            if (v + 1 > best) {
                best = v + 1;
                ret = targets_[index];
            }
        }
        return ret;
    }

    // Are all samples having indices have the same class?
    private boolean haveSameClass(List<Integer> indices) {
        for (int i = 1; i < indices.size(); i++) {
            if (targets_[indices.get(i)] != targets_[indices.get(0)]) {
                return false;
            }
        }
        return true;
    }

    // Get a list of indices of features randomly.
    private List<Integer> getRandomFeatures() {

        Set<Integer> set = new HashSet<Integer>();
        //int featureSize = instances_[0].length;
        while (set.size() < numOfFeatures_) {
        	int temp=rand.nextInt(featureSize);
        	if(!set.contains(temp))
            set.add(temp);
            
        }

        List<Integer> ret = new ArrayList<Integer>();
        ret.addAll(set);
        return ret;
    }
    boolean contain(int[] group,int ID){
    	boolean contain_result=false;
    	for(int i=0;i<group.length;i++){
    		if(group[i]==ID){
    			contain_result=true;
    			break;
    		}
    	}
		return contain_result;
    }
    private List<Integer> getRandomFeatures(int[] badFeatureID) {

        Set<Integer> set = new HashSet<Integer>();
        //int featureSize = instances_[0].length;
        while (set.size() < numOfFeatures_) {
        	int temp=rand.nextInt(featureSize);
        	if(!set.contains(temp)&&!contain(badFeatureID,temp))
            set.add(temp);
            
        }

        List<Integer> ret = new ArrayList<Integer>();
        ret.addAll(set);
        return ret;
    }

    // Get the entropy of some samples.
    private double getEntropy(List<Integer> indices, int from, int to) {
        Util.CHECK(to <= indices.size(), "index of begin can not bigger than index of end");
        Map<Integer, Integer> mii = new HashMap<Integer, Integer>();
        for (int i = from; i < to; i++) {
            Integer v = mii.get(targets_[indices.get(i)]);
            if (v == null) {
                v = 0;
            }
            mii.put(targets_[indices.get(i)], v + 1);
        }
        double ret = 0;
        for (Integer key : mii.keySet()) {
            int v = mii.get(key);
            ret += Math.log((to - from) * 1.0 / v);
        }
        return ret;
    }

    private TreeNode buildTree(List<Integer> indices, int curDepth,TreeInfo treeinfo,long ID) {
    	long nodeID=ID;
        System.out.println("building tree, depth:" + curDepth);
        if(curDepth==1){
        	treeinfo.RootNodeID=nodeID;
        }
        if (maxDepth_ == curDepth) {
        	NodeInfo nodeinfo=new NodeInfo(nodeID,-1, -1, getMajorClass(indices), -1,true,curDepth);
        	treeinfo.nodeinfo.add(nodeinfo);
            return new TreeNode(-1, -1, getMajorClass(indices), null, null, true,curDepth,nodeID,-1,-1,-1);
        }
        if (haveSameClass(indices)) {
        	NodeInfo nodeinfo=new NodeInfo(nodeID,-1, -1, getMajorClass(indices), -1,true,curDepth);
        	treeinfo.nodeinfo.add(nodeinfo);
            return new TreeNode(-1, -1, targets_[indices.get(0)], null, null, true,curDepth,nodeID,-1,-1,-1);
        }
        // this is the difference with buildTreeByDeleteBadFeature
        List<Integer> featureInices = getRandomFeatures();
        double bestEntropy = Double.MAX_VALUE;
        int bestFeatureIndex = -1;
        double splitValue = -1;
        List<Integer> leftIndices = null;
        List<Integer> rightIndices = null;
        int splitIndex = -1;

        for (final int featureIndex : featureInices) {
            Collections.sort(indices, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    if (instances_[o1][featureIndex] < instances_[o2][featureIndex]) {
                        return -1;
                    } else if (instances_[o1][featureIndex] == instances_[o2][featureIndex]) {
                        return o1 - o2;
                    } else {
                        return 1;
                    }
                }
            });
            int bestIndex = -1;
            for (int i = 0; i < indices.size() - 1; i++) {
                if (instances_[indices.get(i)][featureIndex] == instances_[indices.get(i + 1)][featureIndex]) {
                    continue;
                }
                double entropy = 1.0 * (i + 1 - 0) / indices.size() * getEntropy(indices, 0, i + 1)
                        + 1.0 * (indices.size() - (i + 1)) / indices.size()
                        * getEntropy(indices, i + 1, indices.size());
                if (entropy < bestEntropy) {
                    bestEntropy = entropy;
                    bestFeatureIndex = featureIndex;
                    bestIndex = i;
                    splitValue = instances_[indices.get(i)][featureIndex];
                }
            }
            if (bestIndex >= 0) {
            	splitIndex=bestIndex;
                leftIndices = new ArrayList<Integer>();
                rightIndices = new ArrayList<Integer>();
                leftIndices.addAll(indices.subList(0, bestIndex + 1));
                rightIndices.addAll(indices.subList(bestIndex + 1, indices.size()));
            }
        }
        if (bestFeatureIndex >= 0) {
        	       	
        	long left=get_left_nodeID(curDepth,nodeID);
        	long right=get_right_nodeID(curDepth,nodeID);
            
            NodeInfo nodeinfo=new NodeInfo(nodeID,bestFeatureIndex, splitValue, -1, splitIndex,false,left,right,curDepth);
            treeinfo.nodeinfo.add(nodeinfo);
            return new TreeNode(bestFeatureIndex, splitValue, -1, buildTree(leftIndices,
                    curDepth + 1,treeinfo,left), buildTree(rightIndices, curDepth + 1,treeinfo,right), false,curDepth,nodeID,splitIndex,left,right);
        } else {
            // All instances have the same features.
        	NodeInfo nodeinfo=new NodeInfo(nodeID,-1, -1, getMajorClass(indices), splitIndex,true,-1,-1,curDepth);
        	treeinfo.nodeinfo.add(nodeinfo);
            return new TreeNode(-1, -1, getMajorClass(indices), null, null, true,curDepth,nodeID,-1,-1,-1);
        }
    }
    
    protected TreeNode buildTreeByDeleteBadFeature(List<Integer> indices, int curDepth,TreeInfo treeinfo,long ID,int[] badFeatureID) {
    	long nodeID=ID;
        System.out.println("building tree, depth:" + curDepth);
        if(curDepth==1){
        	treeinfo.RootNodeID=nodeID;
        }
        if (maxDepth_ == curDepth) {
        	NodeInfo nodeinfo=new NodeInfo(nodeID,-1, -1, getMajorClass(indices), -1,true,curDepth);
        	treeinfo.nodeinfo.add(nodeinfo);
            return new TreeNode(-1, -1, getMajorClass(indices), null, null, true,curDepth,nodeID,-1,-1,-1);
        }
        if (haveSameClass(indices)) {
        	NodeInfo nodeinfo=new NodeInfo(nodeID,-1, -1, getMajorClass(indices), -1,true,curDepth);
        	treeinfo.nodeinfo.add(nodeinfo);
            return new TreeNode(-1, -1, targets_[indices.get(0)], null, null, true,curDepth,nodeID,-1,-1,-1);
        }
        
        List<Integer> featureInices = getRandomFeatures(badFeatureID);
        double bestEntropy = Double.MAX_VALUE;
        int bestFeatureIndex = -1;
        double splitValue = -1;
        List<Integer> leftIndices = null;
        List<Integer> rightIndices = null;
        int splitIndex = -1;

        for (final int featureIndex : featureInices) {
            Collections.sort(indices, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    if (instances_[o1][featureIndex] < instances_[o2][featureIndex]) {
                        return -1;
                    } else if (instances_[o1][featureIndex] == instances_[o2][featureIndex]) {
                        return o1 - o2;
                    } else {
                        return 1;
                    }
                }
            });
            int bestIndex = -1;
            for (int i = 0; i < indices.size() - 1; i++) {
                if (instances_[indices.get(i)][featureIndex] == instances_[indices.get(i + 1)][featureIndex]) {
                    continue;
                }
                double entropy = 1.0 * (i + 1 - 0) / indices.size() * getEntropy(indices, 0, i + 1)
                        + 1.0 * (indices.size() - (i + 1)) / indices.size()
                        * getEntropy(indices, i + 1, indices.size());
                if (entropy < bestEntropy) {
                    bestEntropy = entropy;
                    bestFeatureIndex = featureIndex;
                    bestIndex = i;
                    splitValue = instances_[indices.get(i)][featureIndex];
                }
            }
            if (bestIndex >= 0) {
            	splitIndex=bestIndex;
                leftIndices = new ArrayList<Integer>();
                rightIndices = new ArrayList<Integer>();
                leftIndices.addAll(indices.subList(0, bestIndex + 1));
                rightIndices.addAll(indices.subList(bestIndex + 1, indices.size()));
            }
        }
        if (bestFeatureIndex >= 0) {
        	       	
        	long left=get_left_nodeID(curDepth,nodeID);
        	long right=get_right_nodeID(curDepth,nodeID);
            
            NodeInfo nodeinfo=new NodeInfo(nodeID,bestFeatureIndex, splitValue, -1, splitIndex,false,left,right,curDepth);
            treeinfo.nodeinfo.add(nodeinfo);
            return new TreeNode(bestFeatureIndex, splitValue, -1, buildTree(leftIndices,
                    curDepth + 1,treeinfo,left), buildTree(rightIndices, curDepth + 1,treeinfo,right), false,curDepth,nodeID,splitIndex,left,right);
        } else {
            // All instances have the same features.
        	NodeInfo nodeinfo=new NodeInfo(nodeID,-1, -1, getMajorClass(indices), splitIndex,true,-1,-1,curDepth);
        	treeinfo.nodeinfo.add(nodeinfo);
            return new TreeNode(-1, -1, getMajorClass(indices), null, null, true,curDepth,nodeID,-1,-1,-1);
        }
    }

    int predicateByOneTree(TreeNode node, double[] instance) {
        if (node.isLeafNode_) {
            return node.target_;
        }
        if (instance[node.featureIndex_] <= node.value_) {
            return predicateByOneTree(node.left_, instance);
        } else {
            return predicateByOneTree(node.right_, instance);
        }
    }
    
     int predicateByOneTreeBySavedData(TreeInfo treeinfo,NodeInfo nodeinfo, double[] instance) {
    	
        if (nodeinfo.isLeafNode_) {
            return nodeinfo.target_;
        }
        if (instance[nodeinfo.featureIndex_] <= nodeinfo.value_) {
            return predicateByOneTreeBySavedData(treeinfo,treeinfo.nodeinfo.get(treeinfo.findIndexByID(nodeinfo.left_NodeID)), instance);
        } else {
            return predicateByOneTreeBySavedData(treeinfo,treeinfo.nodeinfo.get(treeinfo.findIndexByID(nodeinfo.right_NodeID)), instance);
        }
    }
    
    

    // Predicate one instance.
    public int predicate(double[] instance) {
        Map<Integer, Integer> mii = new HashMap<Integer, Integer>();
        int bestTarget = -1;
        int bestCount = -1;
        for (TreeNode root : trees_) {
            int target = predicateByOneTree(root, instance);
            Integer v = mii.get(target);
            if (v == null) {
                v = 0;
            }
            mii.put(target, v + 1);
            if (v + 1 > bestCount) {
                bestCount = v + 1;
                bestTarget = target;
            }
        }
        return bestTarget;
    }
    
    public int predicateBySavedData(double[] instance) {
        Map<Integer, Integer> mii = new HashMap<Integer, Integer>();
        int bestTarget = -1;
        int bestCount = -1;
        for (TreeInfo root : treeinfo) {
            int target = predicateByOneTreeBySavedData(root,root.nodeinfo.get(0), instance);
            Integer v = mii.get(target);
            if (v == null) {
                v = 0;
            }
            mii.put(target, v + 1);
            if (v + 1 > bestCount) {
                bestCount = v + 1;
                bestTarget = target;
            }
        }
        return bestTarget;
    }

    // TreeNode of the decision tree.
    static class TreeNode {
        public int featureIndex_;
        public double value_;
        public int target_;
        public TreeNode left_;
        public TreeNode right_;
        public boolean isLeafNode_;
        //public NodeInfo nodeinfo;
        public long nodeID;
    	//public int father_NodeID;
    	//public int nodeType;
        public int split_Indice_ID;
        public long left_NodeID;
        public long right_NodeID;
        public int nodeDepth;
        
       
        

        public TreeNode(int featureIndex, double value_, int target_, TreeNode left_,
                TreeNode right_, boolean isLeafNode,int curDepth,long nodeID,int split_Indice_ID,long left_NodeID,long right_NodeID) {
            this.featureIndex_ = featureIndex;
            this.value_ = value_;
            this.target_ = target_;
            this.left_ = left_;
            this.right_ = right_;
            this.isLeafNode_ = isLeafNode;
            this.nodeDepth=curDepth;
            this.nodeID=nodeID;
            //this.father_NodeID=father_NodeID;
            this.split_Indice_ID=split_Indice_ID;
            this.left_NodeID=left_NodeID;
            this.right_NodeID=right_NodeID;
            
        }
    }
    
    public long get_left_nodeID(int curDepth,long nodeID){
    	long left_nodeID=numOfTrees_*sum8421(curDepth)+(nodeID-numOfTrees_*sum8421(curDepth-1))*2+1;
		return left_nodeID;
    }
    public long get_right_nodeID(int curDepth,long nodeID){
    	long right_nodeID=numOfTrees_*sum8421(curDepth)+(nodeID-numOfTrees_*sum8421(curDepth-1))*2+2;
		return right_nodeID;
    }
    
     class TreeInfo{
    	int TreeID;
    	long RootNodeID;
    	List<NodeInfo> nodeinfo=new ArrayList<NodeInfo>();
    	long NumOfNode;
    	int tree_correct=0;
    	int tree_wrong=0;
    	double correct_rate=0;
    	public void setTreeID(int TreeID){
    		this.TreeID=TreeID;
    	}
    	public void setRootNodeID(int RootNodeID){
    		this.RootNodeID=RootNodeID;
    	}
    	public void setNumOfNode(int NumOfNode){
    		this.NumOfNode=NumOfNode;
    	}
    	int findIndexByID(long nodeID){
        	int result=-1;
        	for(int i=0;i<this.nodeinfo.size();i++){
        		if(this.nodeinfo.get(i).nodeID==nodeID){
        			result=i;
        		}
        	}
    		return result;
        }
    }
    
    public class NodeInfo{
    	public long nodeID;
    	public long father_NodeID;
    	public int nodeType;
    	public int featureIndex_;
        public double value_;
        public int target_;
        public int split_Indice_ID;
        public boolean isLeafNode_;
        public long left_NodeID;
        public long right_NodeID;
        public int nodeDepth;
        public double weight=0.5;
        public int correct=0;
        public int wrong=0;
        
        public NodeInfo(long nodeID,int featureIndex, double value_, int target_, int split_Indice_ID,
                 boolean isLeafNode,int nodeDepth) {
        	this.nodeID=nodeID;
            this.featureIndex_ = featureIndex;
            this.value_ = value_;
            this.target_ = target_;
            this.split_Indice_ID = split_Indice_ID;
            this.isLeafNode_ = isLeafNode;
            this.nodeDepth=nodeDepth;
            
        }
        
        public NodeInfo(long nodeID,int featureIndex, double value_, int target_, int split_Indice_ID,
                boolean isLeafNode,long left_NodeID,long right_NodeID,int nodeDepth) {
           this.nodeID=nodeID;
           this.featureIndex_ = featureIndex;
           this.value_ = value_;
           this.target_ = target_;
           this.split_Indice_ID = split_Indice_ID;
           this.isLeafNode_ = isLeafNode;
           this.left_NodeID=left_NodeID;
           this.right_NodeID=right_NodeID;
           this.nodeDepth=nodeDepth;
           
       }
        
    	
    }
    
    public static long sum8421(long curDepth){  
        long result=0; 
        while(curDepth>0){  
            result=result+(long)Math.pow(2, (--curDepth));  
        }  
        return result;
    }  

    public static void printTree(TreeNode node, String indedent) {
        if (node.isLeafNode_) {
            System.out.println(indedent + "target:" + node.target_);
        } else {
            System.out.println(indedent + "feature index:" + node.featureIndex_ + ", split value:"
                    + node.value_);
            printTree(node.left_, indedent + "    ");
            printTree(node.right_, indedent + "    ");
        }
    }

    
    private static double[][] test_instances = null;
    private static int[] test_targets = null;
    
    public static void main(String[] args) {
    	//9-13
    	test_instances = new double[][]{{0,0,0,1},{1,1,1,0}};;
        //test_targets = new int[]{2,4,8};
        test_targets = new int[]{0,7};
        double[][] instances = new double[][] { {0, 0, 0, 0 },  {0, 0, 1, 0 }, {0, 0, 1, 1 },
        		 {0, 1, 0, 0 }, {0, 1, 0, 1 }, {0, 1, 1, 0 }, {0, 1, 1, 1 }, {1, 0, 0, 0 }, {1, 0, 0, 1 }, {1, 0, 1, 0 }, {1, 0, 1, 1 },
        		 {1, 1, 0, 0 }, {1, 1, 0, 1 }, {1, 1, 1, 1 }};
        //int[] targets = new int[] { 1, 2, 3, 4, 5, 6, 7, 8 };
        int[] targets = new int[] { 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7};
        RandomForest rf = new RandomForest();
        rf.train(instances, targets, 30, 4, 4, 12);
        for(int i=0;i<rf.trees_.length;i++){
        	printTree(rf.trees_[i], String.valueOf(i));
        }
        
        //System.out.println(rf.trees_.length);
        int correct = 0;
        int wrong = 0;
        for (int i = 0; i < test_targets.length; i++) {
            int actual = rf.predicateBySavedData(test_instances[i]);
            System.out.println("actual: " + actual + ", expected: " + test_targets[i]);
            if (actual == test_targets[i]) {
                correct++;
            } else {
                wrong++;
            }
        }
        System.out.println("correct:" + correct + ", wrong:" + wrong + ", accuracy:" + 1.0
                * correct / (correct + wrong));
    }
}
