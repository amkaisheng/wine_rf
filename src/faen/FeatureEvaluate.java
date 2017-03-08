package faen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import faen.RandomForest.NodeInfo;
import faen.RandomForest.TreeInfo;

public class FeatureEvaluate {
	int featureSize;
	correct_wrong[] c_w;
	class correct_wrong{
		int ID;
		int correct=0;
		int wrong=0;
		double rate=-1;
		int sum=0;
		//int featureID;
		
		void caculate(){
			this.sum=this.correct+this.wrong;
			if(sum!=0){
				this.rate=(double)this.correct/((double)this.correct+(double)this.wrong);
			}
			else{
				this.rate=3.0;
			}
			
		}
	}
	
	public void sortFeature(correct_wrong[] cw){
		Arrays.sort (cw, new Comparator<correct_wrong> ()
				{
				    

					@Override
					public int compare(correct_wrong o1, correct_wrong o2) {
						// TODO Auto-generated method stub
						if (o1.rate  > o2.rate )
				        {
				            return 1;
				        }
				        else if (o1.rate  < o2.rate )
				        {
				            return -1;
				        }
				        else
				        {
				            return 0;
				        }
					}
				});
	}
	
	void setFeatureSize(int size){
		this.featureSize=size;
	}
	
	
	public int findIndexByFeature(TreeInfo treeinfo,int featureID){
		int index=-1;
		
		for(int i=0;i<treeinfo.nodeinfo.size();i++){
			
			if(treeinfo.nodeinfo.get(i).featureIndex_==featureID){
				index=i;
				break;
			}
		}
		return index;
	}
	
	public int findIndexBynodeID(TreeInfo treeinfo,long nodeID){
		int index=-1;
		
		for(int i=0;i<treeinfo.nodeinfo.size();i++){
			
			if(treeinfo.nodeinfo.get(i).nodeID==nodeID){
				index=i;
				break;
			}
		}
		return index;
	}
	
	public void getAllFeatureEvaluate(ArrayList<TreeInfo> arraytreeinfo,int featureSize){
		c_w=new correct_wrong[featureSize];
		for(int i=0;i<c_w.length;i++){
			c_w[i]=new correct_wrong();
			c_w[i].ID=i;
		}
		for(int featureID=0;featureID<featureSize;featureID++){
			getOneFeatureEvaluate(featureID,arraytreeinfo,c_w[featureID]);
		}
		for(int i=0;i<c_w.length;i++){
			c_w[i].caculate();
		}
		sortFeature(c_w);
	}
	
	public correct_wrong getOneFeatureEvaluate(int featureID,ArrayList<TreeInfo> arraytreeinfo,correct_wrong cw){
		
		for(TreeInfo ti:arraytreeinfo){
			int rootNodeOfFeature=findIndexByFeature(ti,featureID);
			//cw.featureID=featureID;
			if(rootNodeOfFeature!=-1){
				evaluateByOneTree(ti,ti.nodeinfo.get(rootNodeOfFeature),cw);
			}
		}
		return cw;
	}
	
	public void evaluateByOneTree(TreeInfo treeinfo,NodeInfo node,correct_wrong cw){
		
		if(node.isLeafNode_){
			cw.correct+=node.correct;
			cw.wrong+=node.wrong;
			
		}
		else{
			evaluateByOneTree(treeinfo,treeinfo.nodeinfo.get(findIndexBynodeID(treeinfo,node.left_NodeID)),cw);
			evaluateByOneTree(treeinfo,treeinfo.nodeinfo.get(findIndexBynodeID(treeinfo,node.right_NodeID)),cw);
		}
	}
}
