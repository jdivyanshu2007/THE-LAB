import java.io.*;
import javazoom.jl.player.Player;
class BackgroundMusic{
    public static void play(){
        Thread musicThread = new Thread(()->{
            try{
                while(true){
                    InputStream file = BackgroundMusic.class.getResourceAsStream("music.mp3");
                    Player player = new Player(file);
                    player.play();
                    file.close(); 
                }
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        });
        musicThread.setDaemon(true);
        musicThread.start();
        Thread musicThread1 = new Thread(()->{
            try{
                while(true){
                    InputStream file1 = BackgroundMusic.class.getResourceAsStream("Zombie.mp3");
                    Player player1 = new Player(file1);
                    player1.play();
                    file1.close(); 
                }
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        });
        musicThread1.setDaemon(true);
        musicThread1.start();
    }
}