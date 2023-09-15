package com.burningstack.ghostface;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import nu.pattern.OpenCV;

@QuarkusMain
public class Main {

    public static void main(String ... args) {
        OpenCV.loadLocally();
        Quarkus.run(GhostFaceApplication.class);
    }

    public static class GhostFaceApplication implements QuarkusApplication {
        @Override
        public int run(String... args) throws Exception {
            /* This method will wait until a shutdown is requested (either from an external signal like when you
               press Ctrl+C or because a thread has called Quarkus.asyncExit()).
             */
            Quarkus.waitForExit();
            return 0;
        }
    }
}
