package alexey.tools.common.misc;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PathUtils {

    @NotNull
    public static String pathToClassName(@NotNull final String path) {
        return path.substring(0, path.length() - 6).replace('/', '.');
    }

    @NotNull
    @Contract(pure = true)
    public static String classNameToPath(@NotNull final String className) {
        return className.replace('.', '/') + ".class";
    }

    @NotNull
    @Contract("!null, null -> param1")
    public static String normalizePath(final String parent, final String relative) {
        if (relative == null || relative.isEmpty()) return parent == null ? "" : parent;
        if (parent == null || parent.isEmpty()) return relative;
        final int index = parent.lastIndexOf('/');
        return index == -1 ? relative : normalizePath(parent.substring(0, index + 1) + relative);
    }

    @NotNull
    @Contract("!null, null -> param1")
    public static String joinPaths(final String parent, final String relative) {
        if (relative == null || relative.isEmpty()) return parent == null ? "" : parent;
        if (parent == null || parent.isEmpty()) return relative;
        return parent.charAt(parent.length() - 1) == '/' || relative.charAt(0) == '/' ?
                parent + relative : parent + '/' + relative;
    }

    @NotNull
    @Contract("!null, null -> param1")
    public static String concatenatePaths(final String parent, final String relative) {
        if (relative == null || relative.isEmpty()) return parent == null ? "" : parent;
        if (parent == null || parent.isEmpty()) return relative;
        final int index = parent.lastIndexOf('/');
        return index == -1 ? relative : parent.substring(0, index + 1) + relative;
    }

    @NotNull
    public static String normalizePath(final String path) {
        if (path == null) return "";
        int size = path.length();
        if (size == 0) return path;
        final char[] array = new char[size + 2];
        path.getChars(0, size, array, 0);
        int i = size - 1;
        if (array[i] != '/') {
            i = size;
            array[size++] = '/';
        }
        do {
            if (array[i] == '/') {
                final int previousIndex = i - 1;
                final char previous = array[previousIndex];
                if (previous == '/') {
                    System.arraycopy(array, i + 1, array, i, size-- - i);
                } else if (previous == '.') {
                    if (i == 1) {
                        System.arraycopy(array, i + 1, array, previousIndex,
                                (size -= 2) - previousIndex);
                        break;
                    } else {
                        int bottomIndex = previousIndex - 1;
                        if (array[bottomIndex] == '/') {
                            System.arraycopy(array, i + 1, array, previousIndex,
                                    (size -= 2) - previousIndex);
                            i = bottomIndex;
                            continue;
                        }
                    }
                }
            }
            i--;
        } while (i > 0);
        i = 2;
        do {
            if (array[i] == '/' && array[i - 1] == '.' && array[i - 2] == '.') {
                if (i == 2) return "";
                if (array[i - 3] == '/') {
                    int j = i - 5;
                    while (j > -1) if (array[j] == '/') break; else j--;
                    int a = i + 1;
                    System.arraycopy(array, a, array, j + 1, size - a);
                    size -= i - j;
                    i = j + 3;
                    continue;
                }
            }
            i++;
        } while (i < size);
        if (size < 1) return "";
        return new String(array, 0, --size);
    }
}

