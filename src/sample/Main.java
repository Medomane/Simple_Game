package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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





    Input input;
    Player player;
    Enemy enemy;
    Bullet bullet;
    Bomb bomb;

    BorderPane troot;



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
    List<MovingObstacle> movingObstacles=new ArrayList<>();

    Text collisionText = new Text();
    boolean player_EnemyCollision = false;
    boolean player_MapObstacleCollision=false;
    boolean attackCollision = false;
    boolean bulletCollision = false;
    HBox info ;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage=primaryStage;

        troot = new BorderPane();
        root = new Group();


        // create layers
        playfieldLayout = new Pane();
        scoreLayout = new Pane();

        root.getChildren().add(playfieldLayout);
        root.getChildren().add(scoreLayout);
        troot.setCenter(root);

        getInfo();

        scene = new Scene(troot, Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT);
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
                    players.forEach(Player::processInput);
                    // movement
                    players.forEach(sprite -> sprite.move());

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
                    if(now-lastUpdate >= Settings.attackIntervall * 1000000){
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
    HBox getInfo(){
        info = new HBox();
        info.setPadding(new Insets(5));
        info.setStyle("-fx-background-color:orange;");
        info.setAlignment(Pos.CENTER_RIGHT);
        ImageView heart1= new ImageView(new Image(getClass().getResource("/heart.png").toExternalForm()));
        ImageView heart2= new ImageView(new Image(getClass().getResource("/heart.png").toExternalForm()));
        ImageView heart3= new ImageView(new Image(getClass().getResource("/heart.png").toExternalForm()));
        heart1.setFitHeight(20);
        heart2.setFitHeight(20);
        heart3.setFitHeight(20);
        heart1.setFitWidth(30);
        heart2.setFitWidth(30);
        heart3.setFitWidth(30);
        info.getChildren().add(heart1);
        info.getChildren().add(heart2);
        info.getChildren().add(heart3);
        info.setMaxHeight(30);
        info.setMinHeight(30);
        troot.setTop(info);
        return info;
    }
    public void restart(){
        info.getChildren().clear();
        info = getInfo();
        gameLoop.stop();
        if(enemy.getHealth() <= 0){
            enemies.clear();
            createEnemy();
        }
        player.setHealth(100);
        enemy.setHealth(100);
        gameLoop.start();
    }
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
                if (cell.getType()==1 && player.collidesWithCell(cell)) {
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
        player = new Player(playfieldLayout, image, 100, 10, Settings.playerSpeed, input);
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
            if(info.getChildren().size() > 0) {
                info.getChildren().remove(info.getChildren().size()-1);
                gameLoop.start();
            }
            if(info.getChildren().size() == 0){
                gameLoop.stop();
                GameOver go = new GameOver(primaryStage,this);
                go.Init();
            }
        });
    }

    private void removeSprites(List<? extends SpriteBase> spriteList) {
        Iterator<? extends SpriteBase> iterator = spriteList.iterator();
        while (iterator.hasNext()) {
            SpriteBase sprite = iterator.next();
            if (sprite.isRemovable()) {
                sprite.removeFromLayer();
                sprite.layer.getChildren().remove(sprite.healthBar.imageView);
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