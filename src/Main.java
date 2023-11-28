import static java.awt.event.KeyEvent.*;

public class Main{
    /**
     * To view this properly, you need to run it in CMD, set in
     * CMD's properties > layout > windows size > width to SCREEN_WIDTH
     * and height to SCREEN_HEIGHT and in font tab, set a font that's
     * height is 1.5 it's width
     * -------------------------
     * (ie I use 'RasterFonts' with size 4x6, that's the smallest size font
     * that doesn't produce colorbleed effect at least on my screen and
     * displays proper shades of gray, other sometimes tint the image
     * purple or brown from my testing)
     */


    private static final char[] MAP = {
            '#','#','#','#','#','#','#','#','#','#',
            '#',' ','#',' ','#',' ',' ',' ','#','#',
            '#',' ',' ',' ','#','#','#',' ',' ','#',
            '#','#',' ',' ','#',' ',' ',' ',' ','#',
            '#',' ','#',' ',' ',' ','#',' ',' ','#',
            '#',' ','#','#','#',' ',' ',' ',' ','#',
            '#',' ','#',' ',' ',' ','#',' ',' ','#',
            '#',' ','#',' ','#',' ','#',' ',' ','#',
            '#',' ',' ',' ','#',' ','#',' ',' ','#',
            '#','#','#','#','#','#','#','#','#','#'
    };
    private static final long FRAME_TIME = 1_000_000_000 / 60;
    private static final int SCREEN_WIDTH = 479, SCREEN_HEIGHT = 179;
    private static final double SCREEN_FOV = Math.PI * (60.0 / 180), PLAYER_SPEED = 0.02;
    private static final WriteCMDBuffer CMD = new WriteCMDBuffer(SCREEN_WIDTH, SCREEN_HEIGHT);
    private static final boolean RUN = true;
    private static double playerX = 7.5, playerY = 3.5, playerA = 0;
    private static boolean isMovingForward = false, isMovingBackward = false, isMovingLeft = false, isMovingRight = false, isRotatingLeft = false, isRotatingRight = false;

    private static void drawScreen(){
        char[][] tempScreen = new char[SCREEN_WIDTH][SCREEN_HEIGHT];
        for (int i = 0; i < SCREEN_WIDTH; i++) {
            double rayAngle = (playerA + SCREEN_FOV / 2) - ((double) i / SCREEN_WIDTH ) * SCREEN_FOV;
            double dist = 0;
            boolean hit = false;

            double eyeX = Math.sin(rayAngle), eyeY = Math.cos(rayAngle);

            while(!hit && dist < 4.6){
                dist += 0.005;

                int testX = (int)(playerX + eyeX * dist);
                int testY = (int)(playerY + eyeY * dist);

                if(testX < 0 || testX > 10 || testY < 0 || testY > 10){
                    hit = true;
                    dist = 4.6;
                } else{
                    if(MAP[(testY * 10) + testX] == '#'){
                        hit = true;
                    }
                }
            }
            int halfSpace = (dist != 0)?(int) Math.min(Math.floor(((double) SCREEN_HEIGHT / 2) - ((SCREEN_HEIGHT / dist) / 1.7)), Math.floor((double) SCREEN_HEIGHT / 2)) : -1;
            int wallHeight = SCREEN_HEIGHT - (2 * halfSpace);
            wallHeight += SCREEN_HEIGHT - (wallHeight + (2 * halfSpace));
            for (int j = 0; j < SCREEN_HEIGHT; j++) {
                if(j < halfSpace) tempScreen[i][j] = ' ';
                else if (j > wallHeight + halfSpace) {
                    if (j < 112) tempScreen[i][j] = ' ';
                    else if (j < 134) tempScreen[i][j] = '─';
                    else if (j < 156) tempScreen[i][j] = '┬';
                    else tempScreen[i][j] = '┼';
                }
                else{
                    if (dist < 0.74) tempScreen[i][j] = '█';            //'█'
                    else if (dist < 2) tempScreen[i][j] = '▓';          //'▓'
                    else if (dist < 2.94)  tempScreen[i][j] = '▒';      //'▒'
                    else  if (dist < 3.46) tempScreen[i][j] = '░';      //'░'
                    else  if (dist < 3.68) tempScreen[i][j] = '¦';      //'¦'
                    else  if (dist < 3.94) tempScreen[i][j] = ':';      //':'
                    else  if (dist < 4.58) tempScreen[i][j] = '.';      //'.'
                    else tempScreen[i][j] = ' ';                        //' '
                }
            }
        }
        for (int i = 0; i < SCREEN_HEIGHT; i++) {
            for (int j = 0; j < SCREEN_WIDTH; j++) {
                CMD.screen[(i*SCREEN_WIDTH)+j] = tempScreen[j][i];
            }
        }
        CMD.writeScreen();
    }

    public static boolean getAsyncKeyState(int key){
        int keyState = KeyState.INSTANCE.GetAsyncKeyState(key);
        return ((keyState & 0x8000) != 0);
    }

    public static void setMovementFlags(){
        isMovingForward = getAsyncKeyState(VK_W);
        isMovingLeft = getAsyncKeyState(VK_A);
        isMovingBackward = getAsyncKeyState(VK_S);
        isMovingRight = getAsyncKeyState(VK_D);
        isRotatingLeft = getAsyncKeyState(VK_LEFT);
        isRotatingRight = getAsyncKeyState(VK_RIGHT);
    }

    public static void calculateMovement(){
        if(isRotatingRight && !isRotatingLeft) playerA -= Math.PI / 240;
        else if(isRotatingLeft) playerA += Math.PI / 240;

        if(playerA >= Math.PI * 2) playerA -= Math.PI * 2;
        else if (playerA < 0) playerA = (Math.PI *2) + playerA;

        if(isMovingForward && !isMovingBackward){
                playerY += Math.cos(playerA - (isMovingRight?(Math.PI/4):0) + (isMovingLeft?(Math.PI/4):0)) * PLAYER_SPEED;
                playerX += Math.sin(playerA - (isMovingRight?(Math.PI/4):0) + (isMovingLeft?(Math.PI/4):0)) * PLAYER_SPEED;
        }
        else if (isMovingBackward && !isMovingForward) {
                playerY += Math.cos(playerA - (Math.PI - (isMovingRight?(Math.PI/4):0) + (isMovingLeft?(Math.PI/4):0))) * PLAYER_SPEED;
                playerX += Math.sin(playerA - (Math.PI - (isMovingRight?(Math.PI/4):0) + (isMovingLeft?(Math.PI/4):0))) * PLAYER_SPEED;
        }
        else if(isMovingLeft && !isMovingRight) {
            playerY += Math.cos(playerA + (Math.PI/2)) * PLAYER_SPEED;
            playerX += Math.sin(playerA + (Math.PI/2)) * PLAYER_SPEED;
        }
        else if(isMovingRight && !isMovingLeft) {
            playerY += Math.cos(playerA - (Math.PI/2)) * PLAYER_SPEED;
            playerX += Math.sin(playerA - (Math.PI/2)) * PLAYER_SPEED;
        }

        playerX = Math.max(0,Math.min(9, playerX));
        playerY = Math.max(0,Math.min(9, playerY));
    }


    public static void main(String[] args){
        while(RUN){
            double start = System.nanoTime(), end;

            setMovementFlags();
            calculateMovement();
            drawScreen();

            do{
                end = System.nanoTime();
            }while(start + FRAME_TIME > end);
        }
    }
}