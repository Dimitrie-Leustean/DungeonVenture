package TileInteractive;

import Entity.Entity;
import Main.GamePanel;

import java.awt.*;

public class IT_DryTree extends InteractiveTile{
    GamePanel gp;

    public IT_DryTree(GamePanel gp, int col, int row) {
        super(gp);
        this.gp = gp;

        this.worldX = gp.tileSize * col;
        this.worldY = gp.tileSize * row;

        down1 = setup("/Resources/Tiles_Interactive/drytree", gp.tileSize, gp.tileSize);
        destructible = true;
        life = 1;
    }

    public boolean isCorrectItem(Entity entity) {
        return entity.currentWeapon.type == type_axe;
    }

    public void playSe(){
        gp.playSE(11);
    }

    public InteractiveTile getDestroyedForm() {
        return new IT_Trunk(gp, worldX / gp.tileSize, worldY / gp.tileSize);
    }


    public Color getParticleColor() {
        return new Color(65, 50, 30);
    }
    public int getParticleSize(){
        return 6;
    }
    public int getParticleSpeed() {
        return 1;
    }
    public int getParticleMaxLife() {
        return 20;
    }
}
