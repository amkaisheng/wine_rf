package faen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ReadInstance{
	public class Data{
		double[] dataset;
	}
	double[][] instances;
    int[] targets ;
    double[][] test_instances;
    int[] test_targets;
    ArrayList<Data>total_data=new ArrayList<Data>();
    ArrayList<Integer>total_target=new ArrayList<Integer>();
    public static Random rand = new Random();
    
    
	void readCsvFromWine(int m_train, int m_test) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("data/wine.csv"));
        int total = m_train+m_test;
        int lable=13;
        this.instances=new double[m_train][lable];
        this.targets=new int[m_train];
        this.test_instances=new double[m_test][lable];
        this.test_targets=new int[m_test];
        
        
        br.readLine();
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }

            String[] lineArray = line.split(",");
            int m = lable;
            int n = lable;
            double[] item = new double[lable];
            for (int i = 0; i < m; i++) {
            	item[i] = Double.parseDouble(lineArray[i+1]);
                      
                
            }
            Data data=new Data();
            data.dataset=item;
            total_data.add(data);
            total_target.add(Integer.parseInt(lineArray[0]));
            
        }
        br.close();
        for(int i=0;i<total;i++){
        	if (i < m_train) {
        		int r=rand.nextInt(total_data.size());
            	this.instances[i] = total_data.get(r).dataset;
            	this.targets[i]=total_target.get(r);
            	total_data.remove(r);
            	total_target.remove(r);
            } else {
            	int r=rand.nextInt(total_data.size());
            	this.test_instances[i-m_train] = total_data.get(r).dataset;
            	this.test_targets[i-m_train]=total_target.get(r);
            	total_data.remove(r);
            	total_target.remove(r);
            }
        }
    }
	
	void readCsvFromA1(int m_train, int m_test) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("data/a1_va4.csv"));
        int total = m_train+m_test;
        int lable=32;
        this.instances=new double[m_train][lable];
        this.targets=new int[m_train];
        this.test_instances=new double[m_test][lable];
        this.test_targets=new int[m_test];
        
        
        br.readLine();
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }

            String[] lineArray = line.split(",");
            int m = lable;
            int n = lable;
            double[] item = new double[lable];
            for (int i = 0; i < m; i++) {
            	item[i] = Double.parseDouble(lineArray[i+1]);
                      
                
            }
            Data data=new Data();
            data.dataset=item;
            total_data.add(data);
            total_target.add(Integer.parseInt(lineArray[0]));
            
        }
        br.close();
        for(int i=0;i<total;i++){
        	if (i < m_train) {
        		int r=rand.nextInt(total_data.size());
            	this.instances[i] = total_data.get(r).dataset;
            	this.targets[i]=total_target.get(r);
            	total_data.remove(r);
            	total_target.remove(r);
            } else {
            	int r=rand.nextInt(total_data.size());
            	this.test_instances[i-m_train] = total_data.get(r).dataset;
            	this.test_targets[i-m_train]=total_target.get(r);
            	total_data.remove(r);
            	total_target.remove(r);
            }
        }
    }

}
