package sample;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Bomb extends Circle {
    Image bomb_Image;
    Node bomb_Node;
    Player target;
    Pane layer;
    ImageView bombImageView;
    public SpriteAnimation bomb_Animation;

    public Bomb(Image bomb_Image,Node bomb_Node,Player target){
        this.bomb_Node = bomb_Node;
        this.target = target;
        this.bomb_Image = bomb_Image;
        this.bombImageView = new ImageView(bomb_Image);

        //For bullet's animation
        bomb_Animation = new SpriteAnimation(bombImageView, Duration.millis(1000), 36, 6, 0, 0, 171, 171);
        bomb_Animation.setCycleCount(1);
        bombImageView.relocate(bomb_Node.getBoundsInParent().getMinX(),bomb_Node.getBoundsInParent().getMinY());


    }
}
