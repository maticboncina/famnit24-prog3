package org.vaje6;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Example {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Scanner s = new Scanner(System.in);
        while (true) {
            s.nextLine();

            executorService.submit(new Player());
        }
    }
}
