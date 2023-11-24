import jnr.ffi.LibraryLoader;
import jnr.ffi.Pointer;
import jnr.ffi.Runtime;
import jnr.ffi.TypeAlias;

public class WriteCMDBuffer {

    public char[] screen;
    private final int nScreenWidth;
    private final int nScreenHeight;
    Kernel32 kernel32 = Kernel32.INSTANCE;
    private final Pointer lpNumberOfCharsWritten = Runtime.getSystemRuntime().getMemoryManager().allocate(Runtime.getSystemRuntime().findType(TypeAlias.int32_t).size());
    private final Pointer hConsole = kernel32.CreateConsoleScreenBuffer(Kernel32.GENERIC_WRITE | Kernel32.GENERIC_READ, Kernel32.FILE_SHARE_READ | Kernel32.FILE_SHARE_WRITE, null, Kernel32.CONSOLE_TEXTMODE_BUFFER, null);

    public WriteCMDBuffer(int width, int height) {
        nScreenHeight = height;
        nScreenWidth = width;
        kernel32.SetConsoleActiveScreenBuffer(hConsole);
        screen = new char[width * height +1];
        for (int i = 0; i < width * height; i++) {
            screen[i] = ' ';
        }
        screen[width*height] = '\0';
    }

    public interface Kernel32 {
        Kernel32 INSTANCE = LibraryLoader.create(Kernel32.class).load("kernel32");
        int GENERIC_READ = 0x80000000;
        int GENERIC_WRITE = 0x40000000;
        int FILE_SHARE_READ = 0x00000001;
        int FILE_SHARE_WRITE = 0x00000002;
        int CONSOLE_TEXTMODE_BUFFER = 1;
        Pointer CreateConsoleScreenBuffer(int dwDesiredAccess, int dwShareMode, Pointer lpSecurityAttributes, int dwFlags, Pointer lpScreenBufferData);
        void SetConsoleActiveScreenBuffer(Pointer hConsoleOutput);
        int WriteConsoleOutputCharacterW(Pointer hConsoleOutput, char[] lpCharacter, int nLength, int dwWriteCoord, Pointer lpNumberOfCharsWritten);
    }

    public void writeScreen(){
        try {
            System.out.println(kernel32.WriteConsoleOutputCharacterW(hConsole, screen, (nScreenWidth * nScreenHeight) + 1, 0, lpNumberOfCharsWritten));
        } catch (Exception t) {
            t.printStackTrace();
        }
    }
}