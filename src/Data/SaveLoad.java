package Data;

import Main.GamePanel;

import java.io.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;

public class SaveLoad {

    GamePanel gp;

    public SaveLoad(GamePanel gp){
        this.gp = gp;
    }

    public void save() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        DataStorage ds = new DataStorage();

        ds.level = gp.player.level;
        ds.maxLife = gp.player.maxLife;
        ds.life = gp.player.life;
        ds.maxMana = gp.player.maxMana;
        ds.mana = gp.player.mana;
        ds.strength = gp.player.strength;
        ds.dexterity = gp.player.dexterity;
        ds.exp = gp.player.exp;
        ds.nextLevelExp = gp.player.nextLevelExp;
        ds.coin = gp.player.coin;
        ds.currentPlayerX = gp.player.worldX;
        ds.currentPlayerY = gp.player.worldY;
        ds.currentPlayerMap = gp.currentMap;

        for (int i = 0; i < gp.player.inventory.size(); i++) {
            ds.itemNames.add(gp.player.inventory.get(i).name);
            ds.itemAmounts.add(gp.player.inventory.get(i).amount);
        }

        ds.currentWeaponSlot = gp.player.getCurrentWeaponSlot();
        ds.currentShieldSlot = gp.player.getCurrentShieldSlot();

        ds.mapObjectNames = new String[gp.maxMap][gp.obj[1].length];
        ds.mapObjectWorldX = new int[gp.maxMap][gp.obj[1].length];
        ds.mapObjectWorldY = new int[gp.maxMap][gp.obj[1].length];
        ds.mapObjectLootNames = new String[gp.maxMap][gp.obj[1].length];
        ds.mapObjectOpened = new boolean[gp.maxMap][gp.obj[1].length];

        for (int mapNum = 0; mapNum < gp.maxMap; mapNum++) {
            for (int i = 0; i < gp.obj[1].length; i++) {
                if (gp.obj[mapNum][i] == null){
                    ds.mapObjectNames[mapNum][i] = "NA";
                } else {
                    ds.mapObjectNames[mapNum][i] = gp.obj[mapNum][i].name;
                    ds.mapObjectWorldX[mapNum][i] = gp.obj[mapNum][i].worldX;
                    ds.mapObjectWorldY[mapNum][i] = gp.obj[mapNum][i].worldY;
                    if (gp.obj[mapNum][i].loot != null) {
                        ds.mapObjectLootNames[mapNum][i] = gp.obj[mapNum][i].loot.name;
                    }

                    ds.mapObjectOpened[mapNum][i] = gp.obj[mapNum][i].opened;
                }
            }
        }

        oos.writeObject(ds);
        oos.close();

        Connection c = null;
        Statement stmt = null;
        try{
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:DataBaseGame.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();

            String insertData = "UPDATE Saves SET Data = '"+ Base64.getEncoder().encodeToString(baos.toByteArray()) +"' WHERE ID = 1";

            stmt.executeUpdate(insertData);
            System.out.println("Statement executed");
            stmt.close();
            c.commit();
            c.close();
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

    }

    public void load() throws IOException, ClassNotFoundException {
        Connection c = null;
        Statement stmt = null;
        DataStorage ds = null;

        try{
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:DataBaseGame.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String result = "SELECT * FROM Saves ORDER BY ID DESC limit 5;";
            ResultSet rs = stmt.executeQuery(result);
            byte [] data = Base64.getDecoder().decode( rs.getBytes(2) );
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(  data ) );
            ds  =(DataStorage) ois.readObject();
            ois.close();
            rs.close();
            stmt.close();
            c.close();
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }


        gp.player.level = ds.level;
        gp.player.maxLife = ds.maxLife;
        gp.player.life = ds.life;
        gp.player.maxMana = ds.maxMana;
        gp.player.mana = ds.mana;
        gp.player.strength = ds.strength;
        gp.player.dexterity = ds.dexterity;
        gp.player.exp = ds.exp;
        gp.player.nextLevelExp = ds.nextLevelExp;
        gp.player.coin = ds.coin;
        gp.player.worldX = ds.currentPlayerX;
        gp.player.worldY = ds.currentPlayerY;
        gp.currentMap = ds.currentPlayerMap;

        gp.player.inventory.clear();
        for (int i = 0; i < ds.itemNames.size(); i++) {
            gp.player.inventory.add(gp.eGenerator.getObject(ds.itemNames.get(i)));
            gp.player.inventory.get(i).amount = ds.itemAmounts.get(i);
        }

        gp.player.currentWeapon = gp.player.inventory.get(ds.currentWeaponSlot);
        gp.player.currentShield = gp.player.inventory.get(ds.currentShieldSlot);
        gp.player.getAttack();
        gp.player.getDefence();
        gp.player.getAttackImage();

        for (int mapNum = 0; mapNum < gp.maxMap; mapNum++) {

            for (int i = 0; i < gp.obj[1].length; i++) {
                if (ds.mapObjectNames[mapNum][i].equals("NA")){
                    gp.obj[mapNum][i] = null;
                } else {
                    gp.obj[mapNum][i] = gp.eGenerator.getObject(ds.mapObjectNames[mapNum][i]);
                    gp.obj[mapNum][i].worldX = ds.mapObjectWorldX[mapNum][i];
                    gp.obj[mapNum][i].worldY = ds.mapObjectWorldY[mapNum][i];

                    if (ds.mapObjectLootNames[mapNum][i] != null){
                        gp.obj[mapNum][i].setLoot(gp.eGenerator.getObject(ds.mapObjectLootNames[mapNum][i]));
                    }

                    gp.obj[mapNum][i].opened = ds.mapObjectOpened[mapNum][i];

                    if (gp.obj[mapNum][i].opened){
                        gp.obj[mapNum][i].down1 = gp.obj[mapNum][i].image2;
                    }
                }
            }
        }
    }
}
