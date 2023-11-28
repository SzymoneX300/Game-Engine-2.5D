import jnr.ffi.LibraryLoader;
import jnr.ffi.annotations.StdCall;

public interface KeyState {
    KeyState INSTANCE = LibraryLoader.create(KeyState.class).load("user32");

    @StdCall
    int GetAsyncKeyState(int vKey);
}