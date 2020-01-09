package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import map.Cell;
import map.Grid;
import map.MovingObstacle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main extends Application {
    Stage primaryStage;
    Group root;
    Scene scene;

    Pane playfieldLayout;
    Pane scoreLayout;
    Grid map;
    int[][] gameMap ={
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };
    int playerSpeed = 2;
    int playerDamage =100;





    Input input;
    Player player;
    Enemy enemy;
    Bullet bullet;
    Bomb bomb;



    Image playerImage;
    Image enemyImage;
    Image borderImage;
    Image bulletImage;
    Image bombImage;

    AnimationTimer gameLoop;






    //Creating lists
    List<Player> players = new ArrayList<>();
    List<Enemy> enemies = new ArrayList<>();
    List<Cell> Cells = new ArrayList<>();
    List<Bullet> bullets = new ArrayList<>();
    List<Bomb> bombs = new ArrayList<>();
    //List<Treasure> treasures = new ArrayList<>();
    List<MovingObstacle> movingObstacles=new ArrayList<>();


    /*Here collision text we will change it later and
    Make it health bar and whenever we attack an enemy
    it's health bar will be decrease if it attacks vice versa*/

    Text collisionText = new Text();
    boolean player_EnemyCollision = false;
    boolean player_MovingObstacleCollision=false;
    boolean player_MapObstacleCollision=false;
    boolean attackCollision = false;
    boolean cellCollision = false;
    boolean bulletCollision = false;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage=primaryStage;
        root = new Group();

        // create layers
        playfieldLayout = new Pane();
        scoreLayout = new Pane();

        root.getChildren().add(playfieldLayout);
        root.getChildren().add(scoreLayout);

        scene = new Scene(root, Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT);
        primaryStage.setTitle("Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        createMap();
        loadGame();
        createPlayers();
        createEnemy();


        gameLoop = new AnimationTimer() {
            private long lastUpdate =0;

            @Override

            public void handle(long now) {
                //input
                if(player.isAlive()){
                    players.forEach(sprite -> sprite.processInput());
                    // movement
                    players.forEach(sprite -> sprite.move());

                    if(player_MovingObstacleCollision){
                        movingObstacles.forEach(movingObstacle->movingObstacle.processInput());
                        movingObstacles.forEach(movingObstacle->movingObstacle.move());
                    }
                    // check collisions
                    player_CheckCollisionWithEnemy();
                    player_enemyBlock();

                    player_CheckCollisionWithMapObstacle();
                    player_mapObstacleBlock();

                    checkAttackCollisionWithEnemy();
                    checkCollisionWithBullet();

                    // update sprites in scene
                    players.forEach(sprite -> sprite.updateUI());
                    enemies.forEach(sprite -> sprite.updateUI());
                    bullets.forEach(sprite -> sprite.updateUI());
                    //bombs.forEach(sprite -> sprite.updateUI());
                    movingObstacles.forEach(mv->mv.updateUI());

                    // for enemy's attack (it shouldn't be called every frame so we should restrict it
                    if(now-lastUpdate >= 1000000000){
                        enemies.forEach(Enemy -> createBullet());
                        lastUpdate=now;
                    }
                    bulletRemove();

                    // check if sprite can be removed
                    enemies.forEach(sprite -> sprite.checkRemovability());

                    // remove removables from list, layer, etc
                    removeSprites(enemies);
                    // update score, health, etc
                    updateScore();
                }
                else {
                    CreateBomb();
                    player.setHealth(100);
                    gameLoop.stop();
                }
            }

        };
        gameLoop.start();

    }
    private void createMap(){
        map=new Grid(gameMap);
        map.Draw(playfieldLayout);
    }
    private void loadGame() {
        playerImage = new Image(getClass().getResource("/spritesheet.png").toExternalForm());
        enemyImage = new Image(getClass().getResource("/mageattack.png").toExternalForm());
        bulletImage = new Image(getClass().getResource("/plasmaball.png").toExternalForm());
        borderImage = new Image(getClass().getResource("/border.jpg").toExternalForm());
        bombImage = new Image(getClass().getResource("/explosion.png").toExternalForm());
    }

    //Collisions
    private void player_CheckCollisionWithEnemy() {
        player_EnemyCollision = false;

        for (Player player : players) {
            for (Enemy enemy : enemies) {
                if (player.collidesWith(enemy)) {
                    player_EnemyCollision = true;
                }
            }
        }
    }
    private void checkAttackCollisionWithEnemy(){
        attackCollision = false;

        for (Player player: players){
            for(Enemy enemy : enemies){
                if(player.attackCollides(enemy)){
                    attackCollision = true;
                }
            }
        }
    }
    private void player_CheckCollisionWithMapObstacle() {
        player_MapObstacleCollision = false;

        for (Player player : players) {
            for (Cell cell:map.mapArraylist) {
                if (cell.getType()==1&&player.collidesWithCell(cell)) {
                    player_MapObstacleCollision = true;
                }
            }
        }
    }
    private void checkCollisionWithBullet(){
        bulletCollision = false;

        for(Player player : players){
            for(Bullet bullet : bullets){
                if(bullet.checkBulletCollision(player)){
                    bulletCollision = true;
                    bullet.BulletRemove =true;
                }
            }
        }
    }

    //blocks for player
    public void player_enemyBlock(){
        if(player_EnemyCollision){
            getAfterCollision();
        }
    }

    public void player_mapObstacleBlock(){
        if(player_MapObstacleCollision){
            getAfterCollision();
        }
    }

    private void getAfterCollision(){
        player.rectangle.setX(player.rectangle.getX()-player.getDx());
        player.rectangle.setY(player.rectangle.getY()-player.getDy());
    }

    public void bulletRemove() {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()){
            Bullet bullet = bulletIterator.next();
            if (bullet.BulletRemove) {
                player.setHealth(player.getHealth()-enemy.getDamage());
                bulletIterator.remove();
            }
        }
    }

    private void createPlayers() {
        //For inputs
        input = new Input(scene);
        input.addListeners();

        Image image = playerImage;
        //Setting players' qualities
        player = new Player(playfieldLayout, image, 100, playerDamage, playerSpeed, input);
        //Add all players in a list so it will be easier to work
        players.add(player);

    }

    private void createEnemy() {

        Image image = enemyImage;
        //Setting enemies' qualities
        enemy = new Enemy(playfieldLayout, image, 300, 200, 100, 10);
        //Add all enemies in a list so it will be easier to work
        enemies.add(enemy);

    }

    public void createBullet(){
        Image image = bulletImage;
        bullet = new Bullet(image, new Circle(enemy.rectangle.getX()+enemy.rectangle.getWidth()/2,enemy.rectangle.getY()+enemy.rectangle.getHeight()/2, 1, Color.RED), enemy, player);
        playfieldLayout.getChildren().add(bullet.bulletImageView);
        bullet.layer = playfieldLayout;
        bullets.add(bullet);
        bullet.pathTransitionBullets(bullet).play();
        enemy.attackAnimation(player);
        bullet.bullet_Animation.play();
    }

    public void CreateBomb(){
        Image image = bombImage;
        bomb = new Bomb(image, new Circle((player.rectangle.getX()+player.rectangle.getWidth()/2)-85.5,(player.rectangle.getY()+player.rectangle.getHeight()/2)-85.5, 1, Color.RED),player);
        playfieldLayout.getChildren().add(bomb.bombImageView);
        bomb.layer = playfieldLayout;
        bombs.add(bomb);
        //enemy.attackAnimation(player);
        bomb.bomb_Animation.play();
        bomb.bomb_Animation.setOnFinished(e->{
            //System.out.println("salam");
            gameLoop.start();
        });
    }

    private void removeSprites(List<? extends SpriteBase> spriteList) {
        Iterator<? extends SpriteBase> iterator = spriteList.iterator();
        while (iterator.hasNext()) {
            SpriteBase sprite = iterator.next();

            if (sprite.isRemovable()) {

                // remove from layer
                sprite.removeFromLayer();
                sprite.layer.getChildren().remove(sprite.healthBar.imageView);

                // remove from list
                iterator.remove();
            }
        }
    }

    private void updateScore() {
        if (attackCollision && input.isAttack() && !Input.getIsAttacking()) {
            Input.setIsAttacking(true);
            enemy.getDamagedBy(player);

        } else {
            collisionText.setText("");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}