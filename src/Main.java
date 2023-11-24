public class Main {
    /**
     * To wiev this properly, you need to run it in CMD
     */

    private static char[] map = {
            '#','#','#','#','#','#','#','#','#','#',
            '#',' ','#',' ','#',' ',' ',' ','#','#',
            '#',' ',' ',' ','#','#','#',' ','#','#',
            '#','#',' ','#',' ','#','#',' ','#','#',
            '#',' ',' ','#',' ',' ','#',' ','#','#',
            '#',' ','#','#','#','#','#',' ','#','#',
            '#',' ','#',' ',' ',' ','#',' ','#','#',
            '#',' ','#',' ','#',' ','#',' ','#','#',
            '#',' ',' ',' ','#',' ','#',' ','#','#',
            '#','#','#','#','#','#','#','#','#','#'
    };
    private static final long FRAME_TIME = 1_000_000_000 / 60;
    private static final int SCREEN_WIDTH = 479, SCREEN_HEIGHT = 179;
    private static final double SCREEN_FOV = Math.PI * (70.0 / 180);
    private static double playerX = 7.5, playerY = 1.5, playerA = 0;
    private static final WriteCMDBuffer CMD = new WriteCMDBuffer(SCREEN_WIDTH, SCREEN_HEIGHT);

    private static void drawScreen(){
        double start = System.nanoTime(), end = 0;
        char[][] tempScreen = new char[SCREEN_WIDTH][SCREEN_HEIGHT];
        for (int i = 0; i < SCREEN_WIDTH; i++) {
            double rayAngle = (playerA + SCREEN_FOV / 2) - ((double) i / SCREEN_WIDTH ) * SCREEN_FOV;
            double dist = 0;
            boolean hit = false;

            double eyeX = Math.sin(rayAngle), eyeY = Math.cos(rayAngle);

            while(!hit && dist < 9){
                dist += 0.0025;

                int testX = (int)(playerX + eyeX * dist);
                int testY = (int)(playerY + eyeY * dist);

                if(testX < 0 || testX > 10 || testY < 0 || testY > 10){
                    hit = true;
                    dist = 9;
                } else{
                    if(map[(testY * 10) + testX] == '#'){
                        hit = true;
                    }
                }
            }
            //int wallHeight = (int) (Math.round(((9-dist)/9) * 176) + 1);
            //int halfSpace  = (int) Math.floor((double)(SCREEN_HEIGHT - wallHeight) / 2) + 1;
            int halfSpace = (int) ((((double) SCREEN_HEIGHT) / 2) - (((double) SCREEN_HEIGHT) * dist)); //create excel to see how the height changes!!!!!!!!!!!!!!!!
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
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                if(i == 0 || i == 11 || j == 0 || j == 11) tempScreen[j][i] = ' ';
                else tempScreen[j][i] = map[((i-1)*10)+j-1];
            }
        }
        tempScreen[((int) playerX)+1][((int) playerY)+1] = '@';
        for (int i = 0; i < SCREEN_HEIGHT; i++) {
            for (int j = 0; j < SCREEN_WIDTH; j++) {
                CMD.screen[(i*SCREEN_WIDTH)+j] = tempScreen[j][i];
            }
        }
        CMD.writeScreen();
        playerA -= Math.PI / 480;
        if(playerA >= Math.PI * 2) playerA -= Math.PI * 2;
        do{
            end = System.nanoTime();
        }while(start + FRAME_TIME > end);
    }

    public static void main(String[] args) {
        while(true){
            drawScreen();
        }
    }
}