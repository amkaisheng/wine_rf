package faen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import faen.RandomForest.TreeInfo;
import faen.RandomForest.TreeNode;
import faen.RandomForest.Util;


public class ReTrain extends RandomForest{
	
	weightedResult[] wR;
	
	public void retrain(double[][] instances, int[] targets, int numOfTrees, int numOfFeatures,
            int maxDepth, int treeSize,ArrayList<TreeInfo> treeinfo) {
        Util.CHECK(instances.length == targets.length, "the length of instances does not match the length of targets");
        Util.CHECK(numOfTrees > 0, "the num of trees must more than zero");
        this.instances_ = instances;
        this.targets_ = targets;
        this.numOfTrees_ = numOfTrees;
        this.numOfFeatures_ = numOfFeatures;
        this.maxDepth_ = maxDepth;
        this.trees_ = new TreeNode[numOfTrees_];
        this.treeinfo=treeinfo;
        List<Entry<Integer, Double>> correctRateOfTrees=new LinkedList<Entry<Integer, Double>>();
        Map<Integer,Double> temp=new HashMap<Integer,Double>();
        for (int i = 0; i < treeinfo.size(); i++) {
            //System.out.println("retrain the tree:" + i);
        
            for(int j=0;j<targets.length;j++){
            	addPerformanceForNode(treeinfo.get(i),treeinfo.get(i).nodeinfo.get(0),instances[j],targets[j]);
            }
            statistics(treeinfo.get(i));
            temp.put(treeinfo.get(i).TreeID, treeinfo.get(i).correct_rate);
            
            //System.out.println("tree_correct_rate: "+treeinfo.get(i).correct_rate);
            for(int j=0;j<treeinfo.get(i).nodeinfo.size();j++){
            	//System.out.println("treeID: "+treeinfo[i].TreeID+" nodeID: "+j);
            	
            	//System.out.println(treeinfo[i].nodeinfo.get(j).weight);
            	//System.out.println(treeinfo[i].nodeinfo.get(j).correct);
            	//System.out.println(treeinfo[i].nodeinfo.get(j).wrong);
            	
            }
                
        }
        correctRateOfTrees=sortMapByValues(temp);
        int cutNum=(int)(0.1*treeinfo.size());
        //System.out.println("tree size: "+treeinfo.size());
        cutTree(treeinfo,cutNum,correctRateOfTrees);
        //System.out.println("tree size after cutting: "+treeinfo.size());
      		
        
    }
	
	public void reTrainByDeleteBadFeature(double[][] instances, int[] targets, int numOfTrees, int numOfFeatures,
            int maxDepth, int treeSize,ArrayList<TreeInfo> old_treeinfo,int numOfBadFeature) {
        Util.CHECK(instances.length == targets.length, "the length of instances does not match the length of targets");
        Util.CHECK(numOfTrees > 0, "the num of trees must more than zero");
        this.instances_ = instances;
        this.targets_ = targets;
        this.numOfTrees_ = numOfTrees;
        this.numOfFeatures_ = numOfFeatures;
        this.maxDepth_ = maxDepth;
        this.featureSize=instances_[0].length;
        this.trees_ = new TreeNode[numOfTrees_];
        this.treeinfo=new ArrayList<TreeInfo>(numOfTrees);;
        for (int i = 0; i < old_treeinfo.size(); i++) {
            //System.out.println("retrain the tree:" + i);
        
            for(int j=0;j<targets.length;j++){
            	addPerformanceForNode(old_treeinfo.get(i),old_treeinfo.get(i).nodeinfo.get(0),instances[j],targets[j]);
            }
            statistics(old_treeinfo.get(i));
                       
        }
        FeatureEvaluate fe=new FeatureEvaluate();
        
        fe.getAllFeatureEvaluate(old_treeinfo, this.featureSize);
        int[] badFeatureID = new int[numOfBadFeature];
        for(int i=0;i<numOfBadFeature;i++){
        	badFeatureID[i]=fe.c_w[i].ID;
        }
        for (int i = 0; i < trees_.length; i++) {
            System.out.println("rebuilding the tree:" + i);
            treeinfo.add(new TreeInfo());
        	treeinfo.get(i).setTreeID(i);
            trees_[i] = buildTreeByDeleteBadFeature(getRandomInstances(treeSize), 1,treeinfo.get(i),i,badFeatureID);
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
        
        List<Entry<Integer, Double>> correctRateOfTrees=new LinkedList<Entry<Integer, Double>>();
        Map<Integer,Double> temp=new HashMap<Integer,Double>();
        for (int i = 0; i < treeinfo.size(); i++) {
            //System.out.println("retrain the tree:" + i);
        
            for(int j=0;j<targets.length;j++){
            	addPerformanceForNode(treeinfo.get(i),treeinfo.get(i).nodeinfo.get(0),instances[j],targets[j]);
            }
            statistics(treeinfo.get(i));
            temp.put(treeinfo.get(i).TreeID, treeinfo.get(i).correct_rate);
            
            //System.out.println("tree_correct_rate: "+treeinfo.get(i).correct_rate);
            for(int j=0;j<treeinfo.get(i).nodeinfo.size();j++){
            	//System.out.println("treeID: "+treeinfo[i].TreeID+" nodeID: "+j);
            	
            	//System.out.println(treeinfo[i].nodeinfo.get(j).weight);
            	//System.out.println(treeinfo[i].nodeinfo.get(j).correct);
            	//System.out.println(treeinfo[i].nodeinfo.get(j).wrong);
            	
            }
                
        }
        correctRateOfTrees=sortMapByValues(temp);
        int cutNum=(int)(0.1*treeinfo.size());
        //System.out.println("tree size: "+treeinfo.size());
        cutTree(treeinfo,cutNum,correctRateOfTrees);
        
    }
	
	private void cutTree(ArrayList<TreeInfo> treeinfo,int cutNum,List<Entry<Integer, Double>> correctRateOfTrees){
		for(int i=0;i<cutNum;i++){
			int ID=correctRateOfTrees.get(i).getKey();
			
			for(int j=0;j<treeinfo.size();j++){
				if(treeinfo.get(j).TreeID==ID){
					treeinfo.remove(j);
				}
			}
			
		}
		
	}
	 private static List<Entry<Integer, Double>> sortMapByValues(Map<Integer, Double> aMap) {  
		  
	        Set<Entry<Integer, Double>> mapEntries = aMap.entrySet();  
	  
	  
	        // used linked list to sort, because insertion of elements in linked list is faster than an array list.   
	        List<Entry<Integer, Double>> aList = new LinkedList<Entry<Integer, Double>>(mapEntries);  
	  
	        // sorting the List  
	        Collections.sort(aList, new Comparator<Entry<Integer, Double>>() {  
	  
	            @Override  
	            public int compare(Entry<Integer, Double> ele1,  
	                    Entry<Integer, Double> ele2) {  
	  
	                return ele1.getValue().compareTo(ele2.getValue());  
	            }  
	        });  
	  
	        // Storing the list into Linked HashMap to preserve the order of insertion.   
	        
			return aList;  
	  
	        
	  
	    }  
	
	
	
	
	
	class weightedResult{
		int target;
		double weight;
		
		
	}
	
	public weightedResult getResult(TreeInfo treeinfo,NodeInfo nodeinfo, double[] instance){
		weightedResult wR=new weightedResult();
		oneTreeResult otr=rePredictByOneTree(treeinfo, nodeinfo,  instance);
		//otr=rePredictByOneTree(treeinfo, nodeinfo,  instance);
		wR.target=otr.target;
		//wR.weight=treeinfo.nodeinfo.get(findIndexByTarget(treeinfo,wR.target)).weight;
		//System.out.println("result i: "+treeinfo.nodeinfo.get(findIndexByID(treeinfo,otr.nodeID)).nodeID);
		wR.weight=treeinfo.nodeinfo.get(findIndexByID(treeinfo,otr.nodeID)).weight;
		return wR;
	}
	
	int findIndexByID(TreeInfo treeinfo,long ID){
    	int resultonetree=-2;
    	for(int i=0;i<treeinfo.nodeinfo.size();i++){
    		if(treeinfo.nodeinfo.get(i).nodeID==ID){
    			
    			resultonetree=i;
    			break;
    		}
    	}
		return resultonetree;
    }


	public int addPerformanceForNode(TreeInfo treeinfo,NodeInfo nodeinfo, double[] instance,int answer) {
    	
        if (nodeinfo.isLeafNode_) {
        	if(nodeinfo.target_==answer){
        		nodeinfo.correct++;
        	}else{
        		nodeinfo.wrong++;
        	}
            return nodeinfo.target_;
        }
        if (instance[nodeinfo.featureIndex_] <= nodeinfo.value_) {
            return addPerformanceForNode(treeinfo,treeinfo.nodeinfo.get(treeinfo.findIndexByID(nodeinfo.left_NodeID)), instance, answer);
        } else {
            return addPerformanceForNode(treeinfo,treeinfo.nodeinfo.get(treeinfo.findIndexByID(nodeinfo.right_NodeID)), instance,answer);
        }
    }
	
	
	
	public void statistics(TreeInfo treeinfo){
		for(int i=0;i<treeinfo.nodeinfo.size();i++){
			if(treeinfo.nodeinfo.get(i).isLeafNode_){
				//Double a=(double)treeinfo.nodeinfo.get(i).correct/((double)treeinfo.nodeinfo.get(i).correct+(double)treeinfo.nodeinfo.get(i).wrong);
				//treeinfo.nodeinfo.get(i).weight=Math.pow(a, 2);
				treeinfo.nodeinfo.get(i).weight=(double)treeinfo.nodeinfo.get(i).correct/((double)treeinfo.nodeinfo.get(i).correct+(double)treeinfo.nodeinfo.get(i).wrong);
				treeinfo.tree_correct+=treeinfo.nodeinfo.get(i).correct;
				treeinfo.tree_wrong+=treeinfo.nodeinfo.get(i).wrong;
			}
		}
		treeinfo.correct_rate=(double)treeinfo.tree_correct/((double)treeinfo.tree_correct+(double)treeinfo.tree_wrong);
	}
	

	public class oneTreeResult{
		long nodeID;
		int target;
	}
	
	public oneTreeResult rePredictByOneTree(TreeInfo treeinfo,NodeInfo nodeinfo, double[] instance) {
    	
        if (nodeinfo.isLeafNode_) {
        	oneTreeResult a=new oneTreeResult();
        	a.nodeID=nodeinfo.nodeID;
        	a.target=nodeinfo.target_;
            return a;
        }
        if (instance[nodeinfo.featureIndex_] <= nodeinfo.value_) {
            return rePredictByOneTree(treeinfo,treeinfo.nodeinfo.get(treeinfo.findIndexByID(nodeinfo.left_NodeID)), instance);
        } else {
            return rePredictByOneTree(treeinfo,treeinfo.nodeinfo.get(treeinfo.findIndexByID(nodeinfo.right_NodeID)), instance);
        }
    }
	
	public int predicateByWeight(double[] instance) {
        Map<Integer, Double> mii = new HashMap<Integer, Double>();
        int bestTarget = -1;
        double bestCount = -1;
        for (TreeInfo root : treeinfo) {
            //double target = rePredictByOneTree(root,root.nodeinfo.get(0), instance);
            weightedResult weightedR=getResult(root,root.nodeinfo.get(0), instance);
            int target=weightedR.target;
            Double v = mii.get(target);
            if (v == null) {
                v = (double) 0;
            }
            v=v+target*weightedR.weight*root.correct_rate;
            mii.put(target, v );
            if (v  > bestCount) {
                bestCount = v ;
                bestTarget = target;
            }
        }
        return bestTarget;
    }
}
