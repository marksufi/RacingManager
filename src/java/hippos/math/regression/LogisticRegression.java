package hippos.math.regression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogisticRegression {
    //private HipposUpdatingRegression linReg;
    /*
        Instancelist
     */
    private List<LogisticRegression.Instance> instances = new ArrayList<LogisticRegression.Instance>();

    /**
     * Use B0
     */
    private boolean b0 = false;

    /**
     * the learning rate
     */
    private double alpha;

    /**
     * the weight to learn
     */
    private double[] weights;

    /**
     * x length
     */
    private int n;
    /**
     * the number of iterations
     *  mle a(0.3)) 10: -1.28, 1000: -0.10
     *  mle a(0.7)) 10: -0.70  1000: -0.06
     *
     *  mle b(0.3)) 10: -0.24   100: -0.038  1000: -0.0045 (961) false: -0.0048 (905)
     *  mle b(0.6): 10: -0.094  100: -0.018  1000: -0.0045 (467) false: -0.0048 (441)
     *  mle b(0.7): 10: -0.055  100: -0.015  1000: -0.0045 (391) false: -0.0048 (350)
     *  mle b(0.8): 10: -0.080  100: -0.014  1000: -0.0044 (357) false: -0.0048 (329)
     *  mle b(0.9): 10: -0.067  100: -0.013  1000: -0.0044 (313) false: -0.0048 (290)
     *  mle b(1.5): 10: -0.0188 100: -0.0063 1000: -0.0046 (158) false: -0.0048 (148)
     *  mle b(1.9): 10: -0.0057 100: -0.0043 (46)                false: -0.0047 (44)
     *  mle b(2.0): 10: -0.0042 (5)
     *  mle b(2.1): NaN
     */
    private int ITERATIONS = 100;


    public LogisticRegression(int n) {
        this.alpha = 0.0001;
        this.n = n;

        weights = new double[n+1];
    }

    private static double sigmoid(double z) {

        return 1.0 / (1.0 + Math.exp(-z));
    }

    public void train() {
        weights = new double[n+1];

        train(this.instances);
    }

    public void train(List<LogisticRegression.Instance> instances) {
        weights = new double[n+1];

        for (int n = 0; n < ITERATIONS; n++) {
            double lik = 0.0;
            for (int i = 0; i < instances.size(); i++) {
                double [] x = instances.get(i).x;
                double predicted = classify(x);
                int y = instances.get(i).y;

                if(b0) {
                    weights[0] = weights[0] + alpha * (y - predicted) * predicted * (1 - predicted) * 1.0;
                }

                for (int j = 1; j < weights.length; j++) {
                    weights[j] = weights[j] + alpha * (y - predicted) * x[j-1];
                    //weights[j] = weights[j] + alpha * (y - predicted) * predicted * (1 - predicted) * x[j-1];
                }
                // not necessary for learning
                lik += y * Math.log(classify(x)) + (1 - y) * Math.log(1 - classify(x));
            }
            //System.out.println("iteration: " + n + " " + Arrays.toString(weights) + " mle: " + lik);
        }
    }

    /**
     * @param instance
     */
    public void train(LogisticRegression.Instance instance) {

        for (int n = 0; n < ITERATIONS; n++) {
            double lik = 0.0;
            //for (int i = 0; i < instances.size(); i++) {
                double [] x = instance.x;
                double predicted = classify(x);
                int y = instance.y;

                if(b0) {
                    weights[0] = weights[0] + alpha * (y - predicted) * predicted * (1 - predicted) * 1.0;
                }

                for (int j = 1; j < weights.length; j++) {
                    weights[j] = weights[j] + alpha * (y - predicted) * x[j-1];
                    //weights[j] = weights[j] + alpha * (y - predicted) * predicted * (1 - predicted) * x[j-1];
                }
                // not necessary for learning
                lik += y * Math.log(classify(x)) + (1 - y) * Math.log(1 - classify(x));
            //}
            System.out.println("iteration: " + n + " " + Arrays.toString(weights) + " mle: " + lik);
        }
    }

    public double classify(double [] x) {
        double logit = weights[0];
        for (int i = 1; i < weights.length; i++) {
            logit += weights[i] * x[i-1];
        }
        return sigmoid(logit);
    }

    public static class Instance {
        public int y;
        public double [] x;

        public Instance(double [] x, int y) {
            this.y = y;
            this.x = x;
        }
    }

    public void addInstance(LogisticRegression.Instance newInstance) {
        this.instances.add(newInstance);
    }

    public void add(double [] x, int y) {
        this.instances.add(new Instance(x, y));
    }

    public double get(double [] x) {
        return classify(x);
    }

    int size() {
        return instances.size();
    }

    public static void main(String... args) {
        List<LogisticRegression.Instance> instances = new ArrayList<LogisticRegression.Instance>();

        /*
        instances.add(new LogisticRegression.Instance(new double[]{2.7810836,	2.550537003}, 0));
        instances.add(new LogisticRegression.Instance(new double[]{1.465489372,	2.362125076}, 0));
        instances.add(new LogisticRegression.Instance(new double[]{3.396561688,	4.400293529}, 0));
        instances.add(new LogisticRegression.Instance(new double[]{1.38807019,	1.850220317}, 0));
        instances.add(new LogisticRegression.Instance(new double[]{3.06407232,	3.005305973}, 0));
        instances.add(new LogisticRegression.Instance(new double[]{7.627531214,	2.759262235}, 1));
        instances.add(new LogisticRegression.Instance(new double[]{5.332441248,	2.088626775}, 1));
        instances.add(new LogisticRegression.Instance(new double[]{6.922596716,	1.77106367}, 1));
        instances.add(new LogisticRegression.Instance(new double[]{8.675418651,	-0.2420686549}, 1));
        instances.add(new LogisticRegression.Instance(new double[]{7.673756466,	3.508563011}, 1));
        */
        instances.add(new LogisticRegression.Instance(new double[]{1,0}, 1));
        instances.add(new LogisticRegression.Instance(new double[]{1,0}, 1));
        instances.add(new LogisticRegression.Instance(new double[]{1,0}, 1));
        instances.add(new LogisticRegression.Instance(new double[]{1,0}, 1));
        instances.add(new LogisticRegression.Instance(new double[]{1,0}, 1));
        instances.add(new LogisticRegression.Instance(new double[]{1,0}, 1));
        instances.add(new LogisticRegression.Instance(new double[]{0,1}, 0));
        instances.add(new LogisticRegression.Instance(new double[]{0,1}, 0));
        instances.add(new LogisticRegression.Instance(new double[]{0,1}, 0));
        instances.add(new LogisticRegression.Instance(new double[]{0,1}, 0));
        LogisticRegression logisticRegression = new LogisticRegression(2);
        logisticRegression.train(instances);


        for(LogisticRegression.Instance ins : instances) {
            System.out.println(Arrays.toString(ins.x) + " = " + logisticRegression.classify(ins.x));
        }

        /*
        LogisticRegression.Instance newIns = new LogisticRegression.Instance(new double[]{7.673756466,	3.508563011}, 1);
        logisticRegression.train(newIns);
        instances.add(newIns);
        for(LogisticRegression.Instance ins : instances) {
            System.out.println(Arrays.toString(ins.x) + " = " + logisticRegression.classify(ins.x));
        }*/

    }
}
