package org.apache.commons.math3.p000ml.neuralnet.sofm;

import java.util.Iterator;
import org.apache.commons.math3.p000ml.neuralnet.Network;

/* renamed from: org.apache.commons.math3.ml.neuralnet.sofm.KohonenTrainingTask */
public class KohonenTrainingTask implements Runnable {
    private final Iterator<double[]> featuresIterator;
    private final Network net;
    private final KohonenUpdateAction updateAction;

    public KohonenTrainingTask(Network net2, Iterator<double[]> featuresIterator2, KohonenUpdateAction updateAction2) {
        this.net = net2;
        this.featuresIterator = featuresIterator2;
        this.updateAction = updateAction2;
    }

    public void run() {
        while (this.featuresIterator.hasNext()) {
            this.updateAction.update(this.net, this.featuresIterator.next());
        }
    }
}
