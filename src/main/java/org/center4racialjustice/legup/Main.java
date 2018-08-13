package org.center4racialjustice.legup;

import org.center4racialjustice.legup.web.ServerStarter;

public class Main {

    public static void main(String[] args){
        System.out.println("start me up");

        try {
            ServerStarter serverStarter = new ServerStarter();
            serverStarter.start();
        } catch (Exception ex){
            System.out.println("Could not start server");
            ex.printStackTrace();
        }
    }

}
