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
    private static final long FRAME_TIME = 1_000_000_000 / 240;
    private static final int SCREEN_WIDTH = 479, SCREEN_HEIGHT = 179;
    private static final double SCREEN_FOV = Math.PI * (90.0 / 180), PLAYER_SPEED = 0.005;
    private static final WriteCMDBuffer CMD = new WriteCMDBuffer(SCREEN_WIDTH, SCREEN_HEIGHT);
    private static double playerX = 7.5, playerY = 3.5, playerA = 0;
    private static boolean isMovingForward = false, isMovingBackward = false, isMovingLeft = false, isMovingRight = false, isRotatingLeft = false, isRotatingRight = false, run = true;

    private static void drawScreen(){
        char[][] tempScreen = new char[SCREEN_WIDTH][SCREEN_HEIGHT];
        for (int i = 0; i < SCREEN_WIDTH; i++) {
            double rayAngle = (playerA + SCREEN_FOV / 2) - ((double) i / SCREEN_WIDTH ) * SCREEN_FOV, rayX, rayY, offsetX = 0, offsetY = 0, dist = 0, horizontalDist = 0, verticalDist = 0;


            //Checking horizontal lines for collisions
            double angleTan = -1 / Math.tan(rayAngle);

            //Looking north
            if(rayAngle > Math.PI / 2 && rayAngle < (3 * Math.PI) / 2) {
                rayY = Math.floor(playerY) - 0.0001;
                rayX = (playerY - rayY) * angleTan + playerX;
                offsetY = -1;
                offsetX = offsetY * -1 * angleTan;
            }
            //Looking south
            else if(rayAngle < Math.PI / 2 || rayAngle > (3 * Math.PI) / 2) {
                rayY = Math.floor(playerY) + 1;
                rayX = (playerY - rayY) * angleTan + playerX;
                offsetY = 1;
                offsetX = offsetY * -1 * angleTan;
            }
            //Looking straigth east or west
            else{
                rayX = playerX;
                rayY = playerY;
                horizontalDist = 5;
            }

            //Iterating untill ray hits a wall or distance gets over 5
            while (horizontalDist < 5){
                int mapIndex = (((int)rayY) * 10) + ((int)rayX);
                horizontalDist = Math.sqrt((rayX * rayX) + (rayY * rayY));
                if(mapIndex < MAP.length && mapIndex >= 0 && MAP[mapIndex] == '#')break;
                else{
                    rayX += offsetX;
                    rayY += offsetY;
                }
            }


            //Checking vertical lines for collisions
            angleTan = -1 * Math.tan(rayAngle);

            //Looking west
            if(rayAngle > Math.PI  && rayAngle < 2 * Math.PI) {
                rayX = Math.floor(playerX - 0.0001);
                rayY = (playerX - rayX) * angleTan + playerY;
                offsetX = -1;
                offsetY = offsetX * -1 * angleTan;
            }
            //Looking east
            else if(rayAngle < Math.PI || rayAngle > 0) {
                rayX = Math.floor(playerX) + 1;
                rayY = (playerX - rayX) * angleTan + playerY;
                offsetX = 1;
                offsetY = offsetX * -1 * angleTan;
            }
            //Loogind straight north or south
            else{
                rayX = playerX;
                rayY = playerY;
                verticalDist = 5;
            }
            //Iterating untill ray hits a wall or distance gets over 5
            while (verticalDist < 5){
                int mapIndex = (((int)rayY) * 10) + ((int)rayX);
                verticalDist = Math.sqrt((rayX * rayX) + (rayY * rayY));
                if(mapIndex < MAP.length && mapIndex >= 0 && MAP[mapIndex] == '#')break;
                else{
                    rayX += offsetX;
                    rayY += offsetY;
                }
            }

            dist = Math.min(verticalDist, horizontalDist);

            /*while(!hit && dist < 4.8){
                dist += 0.005;

                int testX = (int)(playerX + eyeX * dist);
                int testY = (int)(playerY + eyeY * dist);

                if(testX < 0 || testX > 10 || testY < 0 || testY > 10){
                    hit = true;
                    dist = 4.8;
                } else{
                    if(MAP[(testY * 10) + testX] == '#'){
                        hit = true;
                    }
                }
            }*/
            double playerRayAngleDiff = Math.abs(rayAngle - playerA) * -1;
            playerRayAngleDiff += (playerRayAngleDiff < 0) ? 2 * Math.PI : ((playerRayAngleDiff >= 2* Math.PI) ? ((-2) * Math.PI) : 1);
            int wallHeight = Math.min((int)((SCREEN_HEIGHT)/(dist * Math.cos(playerRayAngleDiff))),SCREEN_HEIGHT);
            int halfSpace = (int) Math.floor(((double)(SCREEN_HEIGHT - wallHeight)) / 2);
            if(wallHeight < SCREEN_HEIGHT) wallHeight += SCREEN_HEIGHT - (wallHeight + (2 * halfSpace));

            for (int j = 0; j < SCREEN_HEIGHT; j++) {
                if(j < halfSpace) tempScreen[i][j] = ' ';

                else if (j > wallHeight + halfSpace) {
                    if (j < 105)        tempScreen[i][j] = ' ';
                    if (j < 118)        tempScreen[i][j] = ((i + j) % 2 == 0) ? ' ' : '─';
                    else if (j < 127)   tempScreen[i][j] = '─';
                    else if (j < 140)   tempScreen[i][j] = ((i + j) % 2 == 0) ? '─' : '┬';
                    else if (j < 149)   tempScreen[i][j] = '┬';
                    else if (j < 163)   tempScreen[i][j] = ((i + j) % 2 == 0) ? '┬' : '┼';
                    else                tempScreen[i][j] = '┼';
                }

                else{
                    if(dist < 2.18) {
                        if (dist < 0.52)        tempScreen[i][j] = '█';                             //'██████'
                        else if (dist < 1)      tempScreen[i][j] = ((i + j) % 2 == 0) ? '█' : '▓';  //'█▓█▓█▓'
                        else if (dist < 1.84)   tempScreen[i][j] = '▓';                             //'▓▓▓▓▓▓'
                        else                    tempScreen[i][j] = ((i + j) % 2 == 0) ? '▓' : '▒';  //'▓▒▓▒▓▒'
                    }
                    else if (dist < 3.72) {
                        if (dist < 2.82)        tempScreen[i][j] = '▒';                             //'▒▒▒▒▒▒'
                        else if (dist < 3.08)   tempScreen[i][j] = ((i + j) % 2 == 0) ? '▒' : '░';  //'▒░▒░▒░'
                        else if (dist < 3.44)   tempScreen[i][j] = '░';                             //'░░░░░░'
                        else if (dist < 3.48)   tempScreen[i][j] = ((i + j) % 2 == 0) ? '░' : '¦';  //'░¦░¦░¦'
                        else if (dist < 3.64)   tempScreen[i][j] = '¦';                             //'¦¦¦¦¦¦'
                        else                    tempScreen[i][j] = ((i + j) % 2 == 0) ? '¦' : ':';  //'¦:¦:¦:'
                    }
                    else if (dist < 3.92)   tempScreen[i][j] = ':';                                 //'::::::'
                    else if (dist < 4)      tempScreen[i][j] = ((i + j) % 2 == 0) ? ':' : '.';      //':.:.:.'
                    else if (dist < 4.44)   tempScreen[i][j] = '.';                                 //'......'
                    else if (dist < 4.8)    tempScreen[i][j] = ((i + j) % 2 == 0) ? '.' : ' ';      //'. . . '
                    else                    tempScreen[i][j] = ' ';                                 //'      '

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
        double tempPlayerX = playerX, tempPlayerY = playerY;
        if(isRotatingRight && !isRotatingLeft) playerA -= Math.PI / 480;
        else if(isRotatingLeft) playerA += Math.PI / 480;

        if(playerA >= Math.PI * 2) playerA -= Math.PI * 2;
        else if (playerA < 0) playerA = (Math.PI *2) + playerA;

        if(isMovingForward && !isMovingBackward){
            tempPlayerY += Math.cos(playerA - (isMovingRight?(Math.PI/4):0) + (isMovingLeft?(Math.PI/4):0)) * PLAYER_SPEED;
            tempPlayerX += Math.sin(playerA - (isMovingRight?(Math.PI/4):0) + (isMovingLeft?(Math.PI/4):0)) * PLAYER_SPEED;
        }
        else if (isMovingBackward && !isMovingForward) {
            tempPlayerY += Math.cos(playerA - (Math.PI - (isMovingRight?(Math.PI/4):0) + (isMovingLeft?(Math.PI/4):0))) * PLAYER_SPEED;
            tempPlayerX += Math.sin(playerA - (Math.PI - (isMovingRight?(Math.PI/4):0) + (isMovingLeft?(Math.PI/4):0))) * PLAYER_SPEED;
        }
        else if(isMovingLeft && !isMovingRight) {
            tempPlayerY += Math.cos(playerA + (Math.PI/2)) * PLAYER_SPEED;
            tempPlayerX += Math.sin(playerA + (Math.PI/2)) * PLAYER_SPEED;
        }
        else if(isMovingRight && !isMovingLeft) {
            tempPlayerY += Math.cos(playerA - (Math.PI/2)) * PLAYER_SPEED;
            tempPlayerX += Math.sin(playerA - (Math.PI/2)) * PLAYER_SPEED;
        }

        tempPlayerX = Math.max(1.1,Math.min(8.9, tempPlayerX));
        tempPlayerY = Math.max(1.1,Math.min(8.9, tempPlayerY));

        /**buggy wall collision code
        /*if(Math.abs(tempPlayerY - playerY) > Math.abs(tempPlayerX - playerX)){
            if (tempPlayerX < playerX && (MAP[(((int) (tempPlayerY - 0.1)) * 10) + (int) (tempPlayerX - 0.1)] == '#' || MAP[(((int) (tempPlayerY + 0.1)) * 10) + (int) (tempPlayerX - 0.1)] == '#'))
                tempPlayerX = ((int) (tempPlayerX - 0.1)) + 1.1;
            else if (tempPlayerX > playerX && (MAP[(((int) (tempPlayerY - 0.1)) * 10) + (int) (tempPlayerX + 0.1)] == '#' || MAP[(((int) (tempPlayerY + 0.1)) * 10) + (int) (tempPlayerX + 0.1)] == '#'))
                tempPlayerX = ((int) (tempPlayerX + 0.1)) - 0.1;
        }
        else if(Math.abs(tempPlayerY - playerY) < Math.abs(tempPlayerX - playerX)){
            if (tempPlayerY < playerY && (MAP[(((int) (tempPlayerY + 0.1)) * 10) + (int) (tempPlayerX + 0.1)] == '#' || MAP[(((int) (tempPlayerY + 0.1)) * 10) + (int) (tempPlayerX - 0.1)] == '#'))
                tempPlayerY = ((int) (tempPlayerY - 0.1)) + 1.1;
            else if (tempPlayerY > playerY && (MAP[(((int) (tempPlayerY - 0.1)) * 10) + (int) (tempPlayerX + 0.1)] == '#' || MAP[(((int) (tempPlayerY - 0.1)) * 10) + (int) (tempPlayerX - 0.1)] == '#'))
                tempPlayerY = ((int) (tempPlayerY + 0.1)) - 0.1;
        }*/

        playerX = tempPlayerX;
        playerY = tempPlayerY;
    }

    public static void main(String[] args){
        while(run){
            double start = System.nanoTime(), end;

            setMovementFlags();
            calculateMovement();
            drawScreen();

            do{
                end = System.nanoTime();
            }while(start + FRAME_TIME > end);
        }
        run = true;
    }
}