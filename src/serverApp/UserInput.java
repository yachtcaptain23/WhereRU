package serverApp;

import java.util.Scanner;

public class UserInput implements Runnable{

    private boolean running = true;
    
    @Override
    public void run() {
        Scanner stdIn = new Scanner(System.in);
        String userInput;
        while(this.running == true){
            if(stdIn.hasNext()){
                userInput = stdIn.nextLine();
                CommandHandler.processInput(userInput);
            }
        }
        stdIn.close();
    }
    
    public void terminate(){
        this.running = false;
    }

}
