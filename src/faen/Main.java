package faen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import faen.RandomForest.TreeInfo;

public class Main {
    private static int BLACK_WHITE_THRESHOLD = 100;

    private static void printImage(int[][] pic) {
        for (int i = 0; i < pic.length; i++) {
            for (int j = 0; j < pic[0].length; j++) {
                if (pic[i][j] > 0) {
                    System.out.print(1);
                } else {
                    System.out.print(0);
                }
            }
            System.out.println();
        }
        System.out.println("---------");
    }

    private static int getFeatureValue(int raw) {
        if (raw >= BLACK_WHITE_THRESHOLD) {
            return 1;
        }
        return 0;
    }

    private static void readCsv(int m_train, int m_test) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("data/wine.csv"));
        int total = 0;
        // one instance is 28*28 picture.
        instances = new double[m_train][28 * 28];
        targets = new int[m_train];
        test_instances = new double[m_test][28 * 28];
        test_targets = new int[m_test];
        // The first line is header.
        br.readLine();
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }

            String[] lineArray = line.split(",");
            int m = 28;
            int n = 28;
            int[][] image = new int[m][n];
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    image[i][j] = getFeatureValue(Integer.parseInt(lineArray[i * m + j + 1]));
                    if (total < m_train) {
                        instances[total][i * m + j] = image[i][j];
                    } else {
                        test_instances[total - m_train][i * m + j] = image[i][j];
                    }

                }
            }
            if (total < m_train) {
                targets[total] = Integer.parseInt(lineArray[0]);
            } else {
                test_targets[total - m_train] = Integer.parseInt(lineArray[0]);
            }
            if (total % 200 == 1) {
                printImage(image);
            }
            if (++total >= m_train + m_test) {
                break;
            }
        }
        br.close();
    }
    
    

    private static double[][] instances = null;
    private static int[] targets = null;
    private static double[][] test_instances = null;
    private static int[] test_targets = null;

    public static void main(String[] args) throws Exception {
    	ReadInstance RT=new ReadInstance();
        RT.readCsvFromWine(100, 50);
        RandomForest rf = new RandomForest();
        
        rf.train(RT.instances, RT.targets, 10, 10, -1, 10);
        int correct = 0;
        int wrong = 0;
        for (int i = 0; i < RT.test_targets.length; i++) {
            int actual = rf.predicateBySavedData(RT.test_instances[i]);
            //System.out.println("actual: " + actual + ", expected: " + RT.test_targets[i]);
            if (actual == RT.test_targets[i]) {
                correct++;
            } else {
                wrong++;
            }
        }
        System.out.println("correct:" + correct + ", wrong:" + wrong + ", accuracy:" + 1.0
                * correct / (correct + wrong));
        
        ArrayList<TreeInfo> copy=new ArrayList<TreeInfo>();
        copy.addAll(rf.treeinfo);
        
        ReadInstance RT1=new ReadInstance();
        RT1.readCsvFromWine(0, 100);
        ReTrain rt=new ReTrain();
        //FeatureEvaluate fe=new FeatureEvaluate();
        rt.reTrainByDeleteBadFeature(RT1.test_instances, RT1.test_targets, 10, 10, -1, 10,rf.treeinfo,2);
        //fe.getAllFeatureEvaluate(rf.treeinfo, rf.featureSize);
        int re_correct = 0;
        int re_wrong = 0;
        for (int i = 0; i < RT1.test_instances.length; i++) {
            int re_actual = rt.predicateByWeight(RT1.test_instances[i]);
            System.out.println("(cut_feature)actual: " + re_actual + ", expected: " + RT1.test_targets[i]);
            if (re_actual == RT1.test_targets[i]) {
                re_correct++;
            } else {
                re_wrong++;
            }
        }
        System.out.println("(cut_feature)correct:" + re_correct + ", (cut_feature)wrong:" + re_wrong + ", (cut_feature)accuracy:" + 1.0
                * re_correct / (re_correct + re_wrong));
        
        
        ReadInstance RT2=new ReadInstance();
        RT2.readCsvFromWine(0, 100);
        ReTrain rt2=new ReTrain();
        rt2.retrain(RT2.test_instances, RT2.test_targets, 10, 3, -1, 10,copy);
        int re2_correct = 0;
        int re2_wrong = 0;
        for (int i = 0; i < RT2.test_targets.length; i++) {
            int re2_actual = rt2.predicateByWeight(RT2.test_instances[i]);
            System.out.println("(weight)actual: " + re2_actual + ", expected: " + RT2.test_targets[i]);
            if (re2_actual == RT2.test_targets[i]) {
                re2_correct++;
            } else {
                re2_wrong++;
            }
        }
        System.out.println("(weight)correct:" + re2_correct + ", (weight)wrong:" + re2_wrong + ", (weight)accuracy:" + 1.0
                * re2_correct / (re2_correct + re2_wrong));
                
        
        /*
        ReadInstance RT3=new ReadInstance();
        RT3.readCsvFromWine(100, 100);
        ReTrain rt3=new ReTrain();
        rt3.retrain(RT3.instances, RT3.targets, 30, 3, -1, 30,rf.treeinfo);
        int re3_correct = 0;
        int re3_wrong = 0;
        for (int i = 0; i < RT3.test_targets.length; i++) {
            int re3_actual = rt3.predicateByWeight(RT3.test_instances[i]);
            //System.out.println("re3_actual: " + re3_actual + ", expected: " + RT3.test_targets[i]);
            if (re3_actual == RT3.test_targets[i]) {
                re3_correct++;
            } else {
                re3_wrong++;
            }
        }
        System.out.println("re3_correct:" + re3_correct + ", re3_wrong:" + re3_wrong + ", re3_accuracy:" + 1.0
                * re3_correct / (re3_correct + re3_wrong));
                * */
                
        //rf.printTree(rf.trees_[0], "first tree comment");
    }
}
