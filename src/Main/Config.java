package Main;

import java.io.*;

public class Config {

    GamePanel gp;

    public Config(GamePanel gp){
        this.gp = gp;
    }

    public void safeConfig() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("config.txt"));
        bw.write(String.valueOf(gp.music.volumeScale));
        bw.newLine();
        bw.write(String.valueOf(gp.se.volumeScale));
        bw.newLine();

        bw.close();
    }

    public void loadConfig() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("config.txt"));
        String s;
        s = br.readLine();
        gp.music.volumeScale = Integer.parseInt(s);
        s = br.readLine();
        gp.se.volumeScale = Integer.parseInt(s);

        br.close();
    }
}
