/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Test for Jit regressions.
 */
public class Main {
    public static void main(String args[]) throws Exception {
        b2296099Test();
        b2302318Test();
    }

    static void b2296099Test() throws Exception {
       int x = -1190771042;
       int dist = 360530809;
       int xl = -1190771042;
       int distl = 360530809;

       for (int i = 0; i < 100000; i++) {
           int b = rotateLeft(x, dist);
           if (b != 1030884493)
               throw new RuntimeException("Unexpected value: " + b
                       + " after " + i + " iterations");
       }
       for (int i = 0; i < 100000; i++) {
           long bl = rotateLeft(xl, distl);
           if (bl != 1030884493)
               throw new RuntimeException("Unexpected value: " + bl
                       + " after " + i + " iterations");
       }
       System.out.println("b2296099 Passes");
   }

    static int rotateLeft(int i, int distance) {
        return ((i << distance) | (i >>> (-distance)));
    }

    static void b2302318Test() {
        System.gc();

        SpinThread slow = new SpinThread(Thread.MIN_PRIORITY);
        SpinThread fast1 = new SpinThread(Thread.NORM_PRIORITY);
        SpinThread fast2 = new SpinThread(Thread.MAX_PRIORITY);

        slow.setDaemon(true);
        fast1.setDaemon(true);
        fast2.setDaemon(true);

        fast2.start();
        slow.start();
        fast1.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ie) {/*ignore */}
        System.gc();

        System.out.println("done");
    }
}

class SpinThread extends Thread {
    int mPriority;

    SpinThread(int prio) {
        super("Spin prio=" + prio);
        mPriority = prio;
    }

    public void run() {
        setPriority(mPriority);
        System.out.println("Spinning away at priority " + mPriority);

        while (true) {}
    }
}
